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
import es.unizar.tmdad.ucode.domain.Tweetoo;
import es.unizar.tmdad.ucode.repository.HackathonRepository;
import es.unizar.tmdad.ucode.service.TwitterLookupService;

abstract public class RecollectorInputFlow {
	
	@Autowired
	protected HackathonRepository hackathonRepository;
	
	
	List<String> topics = new ArrayList<String>();
	
	
	abstract protected AbstractMessageChannel requestChooserChannelTwitter();
	
	@Scheduled(fixedDelay=5000)
	public void getTopicsFromDB() {		
		//System.out.println("QUERIESSSSSSSS -> " + tls.getQueries() + "(RecollectorInputFanout.java)");		
		//hackathonRepository.save(new Hackathon(null,"uCode", "EINA" , "buena web", "to"));
		List<Hackathon> hackathons = hackathonRepository.findAll();
		topics = hackathons.stream()
				.map(x -> x.getTag()).collect(Collectors.toList());
	}	
	
	/*
	 * First chooser
	 */
	@Bean
	public IntegrationFlow chooserOne() {
		return IntegrationFlows
				.from(requestChooserChannelTwitter())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				//.transform(highlight2())
				.transform(localTransform())
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				.transform(highlight(1))
				.handle("chooserPropagatorService", "propagateTweet").get();
	}
	
	/*
	 * Second chooser
	 */
	@Bean
	public IntegrationFlow chooserTwo() {
		return IntegrationFlows
				.from(requestChooserChannelTwitter())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				//.transform(highlight2())
				.transform(localTransform())
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				.transform(highlight(2))
				.handle("chooserPropagatorService", "propagateTweet")
				//.handle("streamSendingService", "sendTweet")
				//.handle("chooserSaverService", "saveTweet")
				.get();
	}
	
	/*
	 * Third chooser
	 */
	@Bean
	public IntegrationFlow chooserThree() {
		return IntegrationFlows
				.from(requestChooserChannelTwitter())
				.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")				
				.transform(localTransform())
				//.transform(highlight2())
				.transform(identifyTopics())
				.split(TargetedTweet.class, duplicateByTopic())
				.transform(highlight(3))
				//.handle("requestProcessorChannel")
				.handle("chooserPropagatorService", "propagateTweet")
				//.handle("streamSendingService", "sendTweet")
				//.handle("chooserSaverService", "saveTweet")
				.get();
	}
	
	


	private GenericTransformer<TargetedTweet, TargetedTweet> highlight(int i) {
		return t -> {			
			String tag = t.getFirstTarget();
			String text = t.getTweet().getUnmodifiedText();
			System.out.println("POST " + i + " --> " + text + "(RecollectorInputFanout.java)");
			t.getTweet().setUnmodifiedText(
					text.replaceAll(tag, "<b>" + tag + "</b>"));
			return t;
		};
	}
	
	private GenericTransformer<Tweetoo, Tweetoo> highlight2() {
		return t -> {
			String text = t.getUnmodifiedText();
			System.out.println("ESEEE "+ " --> " + text + "(RecollectorInputFanout.java)");
			return t;
		};
	}

	private Function<TargetedTweet, ?> duplicateByTopic() {
		return t -> t.getTargets().stream()
				.map(x -> new TargetedTweet(t.getTweet(), x))
				.collect(Collectors.toList());
	}
	
	private GenericTransformer<Tweet, Tweetoo> localTransform() {			
		return t -> new Tweetoo(t);
		// The first argument is the MongoDb Id	
		// return t -> new Tweetoo(t.getId(), t.getText(), t.getCreatedAt(), t.getFromUser(), 
		//      t.getProfileImageUrl(), t.getToUserId(), t.getFromUserId(), t.getLanguageCode(), t.getSource());
	}

	private GenericTransformer<Tweetoo, TargetedTweet> identifyTopics() {
		// The first argument is the MongoDb Id 		
		return t -> new TargetedTweet(null, new MyTweet(t), 
										// Debería mirar en la base de datos los hackathones disponibles DONE
										//tls.getQueries().stream()
										topics.stream()
										.filter(x -> t.getText().contains(x))
										.collect(Collectors.toList())
									);
	}
}