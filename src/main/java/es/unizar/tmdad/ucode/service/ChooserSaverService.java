package es.unizar.tmdad.ucode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.repository.TweetRepository;

@Service
public class ChooserSaverService {
	
	@Autowired
	protected TweetRepository tweetRepository;
	
	public void saveTweet(TargetedTweet tweet) {
		System.out.println("UEEEEEEEEOOOOOOOOOOOOO");
		tweetRepository.save(tweet);
	}
	
}
