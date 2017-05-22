package es.unizar.tmdad.ucode.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

import es.unizar.tmdad.ucode.domain.Attendee;
import es.unizar.tmdad.ucode.domain.Hackathon;
import es.unizar.tmdad.ucode.domain.Hashtag;
import es.unizar.tmdad.ucode.domain.Query;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.repository.HackathonRepository;
import es.unizar.tmdad.ucode.repository.HashtagRepository;
import es.unizar.tmdad.ucode.repository.TweetRepository;
import es.unizar.tmdad.ucode.service.TwitterLookupService;

@Controller
public class IndexController {

    @Autowired
    TwitterLookupService twitter;
    
    @Autowired
    HackathonRepository hackathonRepository;
    
    @Autowired
    TweetRepository tweetRepository;
    
    @Autowired
    HashtagRepository hashtagRepository;

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }

    @ResponseBody
    @MessageMapping("/search")
    public void search(Query q) {
        twitter.search(q.getQuery());
    }
    
    @ResponseBody
    @RequestMapping(value = "/hack", method = RequestMethod.GET)
    public List<TargetedTweet> hackathonInfo(Query q) {    	
    	List<TargetedTweet> tweets = tweetRepository.findByHackathon(q.getQuery());
    	//System.out.println(tweets);
    	return Lists.reverse(tweets);
    }
    
    @ResponseBody
    @RequestMapping(value = "/hackathons", method = RequestMethod.GET)
    public List<Hackathon> hackathon() {
    	List<Hackathon> hackathons = hackathonRepository.findAll();
    	//System.out.println(hackathons);
    	return hackathons;
    }
    
    @ResponseBody
    @RequestMapping(value = "/attendees", method = RequestMethod.GET)
    public List<Attendee> attendee(Query q) {
    	Hackathon hackathon = hackathonRepository.findByTag(q.getQuery());
    	//System.out.println(hackathon);
    	return hackathon.getAttendees();
    }
    
    @ResponseBody
    @RequestMapping(value = "/hashtags", method = RequestMethod.GET)
    public List<Hashtag> getAllHashtags() {
    	List<Hashtag> hashtagList = hashtagRepository.findAll();
    	hashtagList.sort((o1, o2) -> o2.getCount()-o1.getCount());
    	//System.out.println(hashtagList);
    	return hashtagList;
    }
    
	@RequestMapping(value = "/addHackathon", method = RequestMethod.POST)
	public void add(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "venue", required = true) String venue,
			@RequestParam(value = "web", required = true) String web,
			@RequestParam(value = "tag", required = true) String tag,
			@RequestParam(value = "att-name", required = true) boolean attName,
			@RequestParam(value = "att-size", required = true) boolean attSize,
			@RequestParam(value = "att-age", required = true) boolean attAge,
			@RequestParam(value = "att-mail", required = true) boolean attMail,
			Model model) throws ServletException {
		
		Map<String,Boolean> prop = new LinkedHashMap<>();
		prop.put("name",  attName);
		prop.put("size",  attSize);
		prop.put("age",  attAge);
		prop.put("mail",  attMail);
		
		Hackathon hack = new Hackathon(null, name, venue, web, tag);
		hack.setProperties(prop);

		hackathonRepository.save(hack);
		// User or password incorrect
		//model.addAttribute("msgerror", "User os password incorrect");
		//return "login";
	}
	
	@RequestMapping(value = "/editHackathon", method = RequestMethod.PUT)
	public void edit(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "venue", required = true) String venue,
			@RequestParam(value = "web", required = true) String web,
			@RequestParam(value = "tag", required = true) String tag,
			@RequestParam(value = "att-name", required = true) boolean attName,
			@RequestParam(value = "att-size", required = true) boolean attSize,
			@RequestParam(value = "att-age", required = true) boolean attAge,
			@RequestParam(value = "att-mail", required = true) boolean attMail,
			Model model) throws ServletException {
		
		Hackathon currentHackathon = hackathonRepository.findByNombre(name);
		if(currentHackathon != null) {
			currentHackathon.setLugar(venue);
			currentHackathon.setWeb(web);
			currentHackathon.setTag(tag);
			Map<String,Boolean> prop = new LinkedHashMap<>();
			prop.put("name",  attName);
			prop.put("size",  attSize);
			prop.put("age",  attAge);
			prop.put("mail",  attMail);
			currentHackathon.setProperties(prop);
	
			hackathonRepository.save(currentHackathon);
		} else {
			Map<String,Boolean> prop = new LinkedHashMap<>();
			prop.put("name",  attName);
			prop.put("size",  attSize);
			prop.put("age",  attAge);
			prop.put("mail",  attMail);
			
			Hackathon hack = new Hackathon(null, name, venue, web, tag);
			hack.setProperties(prop);

			hackathonRepository.save(hack);
		}
		// User or password incorrect
		//model.addAttribute("msgerror", "User os password incorrect");
		//return "login";
	}
	
	@ResponseBody
	@RequestMapping(value = "/editHackathon", method = RequestMethod.GET)
	public Hackathon edit(Query q) throws ServletException {
		
		Hackathon currentHackathon = hackathonRepository.findByTag(q.getQuery());
		
		return currentHackathon;

		//hackathonRepository.save(currentHackathon);
		// User or password incorrect
		//model.addAttribute("msgerror", "User os password incorrect");
		//return "login";
	}
    
	protected static String getOwnerMail() {
		Authentication currentAuthentication = SecurityContextHolder
				.getContext().getAuthentication();
		String email = "";
		if (currentAuthentication instanceof SocialAuthenticationToken) {
			SocialAuthenticationToken social = (SocialAuthenticationToken) currentAuthentication;

			switch (social.getProviderId()) {
			case ("google"):
				Google google = (Google) social.getConnection().getApi();
				email = google.plusOperations().getGoogleProfile()
						.getAccountEmail();
				break;
			case ("facebook"):
				Facebook facebook = (Facebook) social.getConnection().getApi();
				email = facebook.userOperations().getUserProfile().getEmail();
				break;
			}
		}
		else if (currentAuthentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken local = (UsernamePasswordAuthenticationToken) currentAuthentication;
			email = (String) local.getPrincipal();
		} else if (currentAuthentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return email;
	}
}