package es.unizar.tmdad.ucode.flows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Tweet;

import es.unizar.tmdad.ucode.domain.Hackathon;
import es.unizar.tmdad.ucode.domain.MyTweet;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.repository.HackathonRepository;
import es.unizar.tmdad.ucode.service.TwitterLookupService;

abstract public class TwitterToRecollectorToChooserFlow {

	@Autowired
	private TwitterLookupService tls;
	
	@Autowired
	protected HackathonRepository hackathonRepository;
	
	
	List<String> topics = new ArrayList<String>();
	
	@Scheduled(fixedDelay=5000)
	public void getTopicsFromDB() {
		
		System.out.println("QUERIESSSSSSSS -> " + tls.getQueries());
		
		hackathonRepository.save(new Hackathon(null,"uCode", "EINA" , "buena web", "to"));
		List<Hackathon> hackathons = hackathonRepository.findAll();
		topics = hackathons.stream()
		.map(x -> x.getTag()).collect(Collectors.toList());
	}
	
	/*
	 * Updater
	 */
	@Bean
	public IntegrationFlow sendTweet() {
		return IntegrationFlows
				.from(requestChannelRabbitMQUpdater())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				.handle("streamSendingService", "sendTweet").get();
	}
	
	/*
	 * Chooser Saver
	 */
	@Bean
	public IntegrationFlow propagateTweet() {
		return IntegrationFlows
				.from(requestChannelRabbitMQProccessor())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				.transform(highlight())
				.handle("chooserProccessorService", "propagateTweet")
				//.handle("streamSendingService", "sendTweet")
				//.handle("chooserSaverService", "saveTweet")
				.get();
	}
	
	@Bean
	public IntegrationFlow saveTweet() {
		return IntegrationFlows
				.from(requestChannelRabbitMQSaver())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				//.handle("requestProcessorChannel")
				//.handle("streamSendingService", "sendTweet")
				.handle("chooserSaverService", "saveTweet")
				.get();
	}

	abstract protected AbstractMessageChannel requestChannelRabbitMQUpdater();
	
	abstract protected AbstractMessageChannel requestChannelRabbitMQSaver();
	
	abstract protected AbstractMessageChannel requestChannelRabbitMQProccessor();

	private GenericTransformer<TargetedTweet, TargetedTweet> highlight() {
		return t -> {			
			String tag = t.getFirstTarget();
			String text = t.getTweet().getUnmodifiedText();
			System.out.println("POST --> " + text);
			t.getTweet().setUnmodifiedText(
					text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}

	private Function<TargetedTweet, ?> duplicateByTopic() {
		return t -> t.getTargets().stream()
				.map(x -> new TargetedTweet(t.getTweet(), x))
				.collect(Collectors.toList());
	}

	private GenericTransformer<Tweet, TargetedTweet> identifyTopics() {
		// The first argument is the MongoDb Id 
		return t -> new TargetedTweet(null, new MyTweet(t), 
										// Debería mirar en la base de datos los hackathones disponibles DONE
										tls.getQueries().stream()
										.filter(x -> t.getText().contains(x))
										.collect(Collectors.toList())
									);
	}
}