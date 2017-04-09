package es.unizar.tmdad.ucode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.repository.TweetRepository;

@Service
public class SaverService {
	
	@Autowired
	protected TweetRepository tweetRepository;
	
	public void saveTweet(TargetedTweet tweet) {
		System.out.println("SAVING TWEET");
		tweetRepository.save(tweet);
	}
	
}
