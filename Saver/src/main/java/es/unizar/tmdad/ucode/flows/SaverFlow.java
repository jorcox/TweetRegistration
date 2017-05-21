package es.unizar.tmdad.ucode.flows;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

//import es.unizar.tmdad.ucode.service.TwitterLookupService;

abstract public class SaverFlow {

	//@Autowired
	//private TwitterLookupService tls;
	
	abstract protected AbstractMessageChannel requestSaverChannel();

	@Bean
	public IntegrationFlow saveTweet() {
		return IntegrationFlows
				.from(requestSaverChannel())
				//.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				//.transform(identifyTopics())
				//.split(TargetedTweet.class, duplicateByTopic())
				//.transform(highlight())
				.handle("saverService", "saveTweet").get();
	}

	

	/*private GenericTransformer<TargetedTweet, TargetedTweet> highlight() {
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
										tls.getQueries().stream()
										.filter(x -> t.getText().contains(x))
										.collect(Collectors.toList())
									);
	}*/
}