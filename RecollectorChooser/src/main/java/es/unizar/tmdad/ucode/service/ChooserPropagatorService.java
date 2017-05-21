package es.unizar.tmdad.ucode.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import es.unizar.tmdad.ucode.domain.TargetedTweet;


/**
 * Cada vez que se llama al método propagateTweet. En la clase ChooserPropagatorGatewayListener se redirige 
 * a la salida del chooser
 * @author jorco
 *
 */
@Service
@Component
public interface ChooserPropagatorService {

	public void propagateTweet(TargetedTweet tweet);
	
}
