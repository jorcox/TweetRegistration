package es.unizar.tmdad.ucode.flows;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.social.twitter.api.Tweet;

import es.unizar.tmdad.ucode.domain.MyTweet;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.service.TwitterLookupService;

abstract public class UpdaterFlow {

	@Autowired
	private TwitterLookupService tls;

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

	abstract protected AbstractMessageChannel requestUpdateChannel();

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