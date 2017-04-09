package es.unizar.tmdad.ucode.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import es.unizar.tmdad.ucode.domain.Hackathon;
import es.unizar.tmdad.ucode.domain.Hash;
import es.unizar.tmdad.ucode.domain.Query;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
import es.unizar.tmdad.ucode.domain.User;
import es.unizar.tmdad.ucode.repository.HackathonRepository;
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
    	System.out.println(tweets);
    	return Lists.reverse(tweets);
    }
    
    @ResponseBody
    @RequestMapping(value = "/hackathons", method = RequestMethod.GET)
    public List<Hackathon> hackathon() {
    	List<Hackathon> hackathons = hackathonRepository.findAll();
    	System.out.println(hackathons);
    	return hackathons;
    }
    
	@RequestMapping(value = "/addHackathon", method = RequestMethod.POST)
	public void add(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "venue", required = true) String venue,
			@RequestParam(value = "web", required = true) String web,
			@RequestParam(value = "tag", required = true) String tag,
			Model model) throws ServletException {

		hackathonRepository.save(new Hackathon(null, name, venue, web, tag));
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