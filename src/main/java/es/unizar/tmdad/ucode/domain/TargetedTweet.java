package es.unizar.tmdad.ucode.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;

public class TargetedTweet implements Serializable {
	
	@Id
	private BigInteger id;
	
	private MyTweet tweet;
	
	private List<String> targets;

	public TargetedTweet(BigInteger id, MyTweet tweet, List<String> targets) {
		this.id = id;
		this.tweet = tweet;
		this.targets = targets;
	}
	
	public TargetedTweet() {
		/*
		 * Default constructor is necessary for JSON to object conversion
		 */
	}
	
	public TargetedTweet(MyTweet tweet, String target) {
		this.tweet = tweet;
		this.targets = Collections.singletonList(target);
	}

	public MyTweet getTweet() {
		return tweet;
	}
	
	public List<String> getTargets() {
		return targets;
	}

	public String getFirstTarget() {
		return targets.get(0);
	}

}
