package es.unizar.tmdad.ucode.web;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.unizar.tmdad.ucode.domain.Query;
import es.unizar.tmdad.ucode.domain.TargetedTweet;
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
    @MessageMapping("/hack-info/old")
    public List<TargetedTweet> hackathonInfo(Query q) {
    	List<TargetedTweet> tweets = tweetRepository.findByTargetsContaining(q.getQuery());
    	System.out.println(tweets);
    	return tweets;
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