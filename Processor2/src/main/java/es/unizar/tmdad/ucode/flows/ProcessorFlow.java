package es.unizar.tmdad.ucode.flows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.transformer.GenericTransformer;

import es.unizar.tmdad.ucode.domain.Hashtag;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
//import es.unizar.tmdad.ucode.service.TwitterLookupService;
import es.unizar.tmdad.ucode.repository.AttendeeRepository;
import es.unizar.tmdad.ucode.repository.HackathonRepository;
import es.unizar.tmdad.ucode.repository.HashtagRepository;

abstract public class ProcessorFlow {

	//@Autowired
	//private TwitterLookupService tls;
	
	abstract protected AbstractMessageChannel requestProcessorChannel();
	
	@Autowired
	protected AttendeeRepository attendeeRepository;
	
	@Autowired
	protected HackathonRepository hackathonRepository;
	
	@Autowired
	protected HashtagRepository hashtagRepository;
	
	@Bean
	public IntegrationFlow processTweet() {
		return IntegrationFlows
				.from(requestProcessorChannel())
				//.filter("payload instanceof T(org.springframework.social.twitter.api.Tweet)")
				//.transform(identifyTopics())
				.split(TargetedTweet.class, tweetSplit)
				.transform(extractHashtag())
				.handle("processorOutputService", "sendHashtagList").get();
				// Transformar el tweet en un attendee, guardarlo y reenviarlo al cliente.
	}
	
	private GenericTransformer<String, List<Hashtag>> extractHashtag() {
		return t -> {
			Hashtag hashtag = hashtagRepository.findByName(t);
			if (hashtag != null){
				hashtag.increment();
			} else {
				hashtag = new Hashtag(t,1);
			}
			hashtagRepository.save(hashtag);
			List<Hashtag> hashtagList = hashtagRepository.findAll();				
			
			return hashtagList;
		};
	}

	Function<TargetedTweet, ?> tweetSplit = tweet ->{
		//String[] salida = tweet.getTweet().getText().split("#[^\\s]*");
		
		List<String> matches = new ArrayList<>();
		Matcher m = Pattern.compile("(#[^\\s]*)").matcher(tweet.getTweet().getUnmodifiedText());
		while(m.find()){
		   matches.add(m.group(1));
		}

		return matches
				.stream()
				.collect(Collectors.toList());
	};
	
	/*private GenericTransformer<TargetedTweet, Hashtag> extractHashtag() {
		return t -> {
			Attendee attendee = parseTweet(t);
			System.out.println("TWEET PROCESSED (ProcessorService.java)");
			if(attendee != null) {
				System.out.println("ATTENDEE SAVED (ProcessorService.java)");
				Hackathon hack = hackathonRepository.findByTag(t.getFirstTarget());
				List<Attendee> list = hack.getAttendees();
				list.add(attendee);
				hack.setAttendees(list);
				hackathonRepository.save(hack);
				attendeeRepository.save(attendee);
			}
			return attendee;
		};
	}*/
	
//	  private Attendee parseTweet(TargetedTweet tweet) {
//		Hackathon hack = hackathonRepository.findByTag(tweet.getFirstTarget());
//		if(hack != null){
//			ArrayList<String> attendeeFields = new ArrayList<String>();
//			attendeeFields.add(0,"NombreNeutro");
//			attendeeFields.add(1,"M");
//			attendeeFields.add(2,"18");
//			attendeeFields.add(3,"mail@Neutro.com");
//						
//			Map<String,Boolean> properties =  hack.getProperties();			
//			/* Check if this tweet is a registration tweet */
//			if(tweet.getTweet().getUnmodifiedText().toLowerCase().indexOf("registro") != -1){
//				/* For each property */
//				for(String prop : properties.keySet()){
//					if(properties.get(prop)){
//						String[] fields = tweet.getTweet().getUnmodifiedText().split(";");
//						int count = 0;
//						/* Find each property */
//						for (String field : fields){
//							String[] str = field.split(":");
//							if(str[0].contains(prop)){
//								attendeeFields.add(count,str[str.length-1]);
//							}
//							count++;
//							//attendeeFields.add(str[str.length-1]);
//							//System.out.println(str[str.length-1]);
//						}
//					}				
//					//TODO
//				}
//				try{
//					int age = attendeeFields.get(2) != null ? Integer.parseInt(attendeeFields.get(2)) : 0;
//					return new Attendee(attendeeFields.get(0), attendeeFields.get(1), age, attendeeFields.get(3), tweet.getFirstTarget());
//				} catch (Exception e) {
//					return null;
//				}
//			} else if(tweet.getTweet().getUnmodifiedText().toLowerCase().indexOf("desregistro") != -1){
//				
//			}
//		}
//		return null;
//	} 

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