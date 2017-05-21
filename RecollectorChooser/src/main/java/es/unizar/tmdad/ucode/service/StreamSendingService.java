package es.unizar.tmdad.ucode.service;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.FilterStreamParameters;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

@Service
public class StreamSendingService {	

	//@Autowired
	//private SimpMessageSendingOperations ops;
	
	@Autowired
	private TwitterTemplate twitterTemplate;

	@Autowired
	private TwitterLookupService lookupService;
	
	private Stream stream;

	@Autowired
	private StreamListener integrationStreamListener;
	
	@PostConstruct
	public void initialize() {
		FilterStreamParameters fsp = new FilterStreamParameters();
		fsp.addLocation(-180, -90, 180, 90);

		// Primer paso
		// Registro un gateway para recibir los mensajes
		// Ver @MessagingGateway en MyStreamListener en TwitterFlow.java
		stream = twitterTemplate.streamingOperations().filter(fsp, Collections.singletonList(integrationStreamListener));
	}

	// Cuarto paso
	// Recibe un tweet y hay que enviarlo a tantos canales como preguntas hay registradas en lookupService
	//
	
	
	
	/*public void sendTweet(Tweet tweet) {

		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		
		// Expresión lambda: si el tweet contiene s, devuelve true
		Predicate<String> containsTopic = s -> tweet.getText().contains(s);
		// Expresión lambda: envia un tweet al canal asociado al tópico s
		Consumer<String> convertAndSend = s -> ops.convertAndSend("/queue/search/" + s, tweet, map);

		lookupService.getQueries().stream().filter(containsTopic).forEach(convertAndSend);
	}*/

	/*public void sendTweet(TargetedTweet tweet) {
		
		Map<String, Object> mapa = new HashMap<>();

		mapa.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		
		System.out.println("SENDING TWEET TO CLIENT"  + "(StreanSendinfService.java)");

		ops.convertAndSend("/queue/search/" + tweet.getFirstTarget(), tweet.getTweet(), mapa);

	}
	
	public void sendAttendee(Attendee attendee) {
		
		Map<String, Object> mapa = new HashMap<>();

		mapa.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
		
		System.out.println("SENDING ATTENDEE TO CLIENT"  + "(StreanSendinfService.java)");

		ops.convertAndSend("/queue/search/" + attendee, attendee, mapa);

	}*/


	public Stream getStream() {
		return stream;
	}

}