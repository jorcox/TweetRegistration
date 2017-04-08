package es.unizar.tmdad.ucode.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import es.unizar.tmdad.ucode.domain.TargetedTweet;

@Service
@Component
public interface ChooserProccessorService {

	public void propagateTweet(TargetedTweet tweet);
	
}
