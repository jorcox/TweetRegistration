package es.unizar.tmdad.ucode.flows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.transformer.GenericTransformer;

import es.unizar.tmdad.ucode.domain.Attendee;
import es.unizar.tmdad.ucode.service.TwitterLookupService;

@Profile("dashboard")
abstract public class UpdaterFlow {

	@Autowired
	private TwitterLookupService tls;

	abstract protected AbstractMessageChannel requestUpdateChannel();
	
	abstract protected AbstractMessageChannel requestUpdateAttendeesChannel();

	abstract protected DirectChannel requestUpdateAttendeesChannel2();
	
	@Bean
	public IntegrationFlow sendTweet() {
		return IntegrationFlows
				.from(requestUpdateChannel())
				.filter("payload instanceof T(es.unizar.tmdad.ucode.domain.TargetedTweet)")
				//.transform(identifyTopics())
				//.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				.handle("streamSendingService", "sendTweet").get();
	}
	
	@Bean
	public IntegrationFlow sendAttendee() {
		return IntegrationFlows
				.from(requestUpdateAttendeesChannel())
				//.transform(highlight())
				.filter("payload instanceof T(es.unizar.tmdad.ucode.domain.Attendee)")
				//.transform(identifyTopics())
				//.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				.handle("streamSendingService", "sendAttendee").get();
	}
	
	@Bean
	public IntegrationFlow sendHashtag() {
		return IntegrationFlows
				.from(requestUpdateAttendeesChannel2())
				//.filter("payload instanceof T(es.unizar.tmdad.ucode.domain.TargetedTweet)")
				//.transform(identifyTopics())
				//.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				.handle("streamSendingService", "sendHashtag").get();
	}
	
	private GenericTransformer<Attendee, Attendee> highlight() {
		return t -> {			
			//String tag = t.getFirstTarget();
			//String text = t.getTweet().getUnmodifiedText();
			System.out.println("ATTENDEE RECEIVED BY THE UPDATER" + t);
			//t.getTweet().setUnmodifiedText(
			//		text.replaceAll(tag, "<b>" + tag + "</b>"));			
			return t;
		};
	}

	/*private Function<TargetedTweet, ?> duplicateByTopic() {
		return t -> t.getTargets().stream()
				.map(x -> new TargetedTweet(t.getTweet(), x))
				.collect(Collectors.toList());
	}

	private GenericTransformer<Tweet, TargetedTweet> identifyTopics() {
		// The first argument is the MongoDb Id 
		return t -> new TargetedTweet(null, new MyTweet(t), 
										tls.getQueries().stream()
										.filter(x -> t.getText().contains(x))
										.collect(Collectors.toList())
									);
	}*/
}