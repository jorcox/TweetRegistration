package es.unizar.tmdad.ucode.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.DisconnectInterceptor;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import es.unizar.tmdad.ucode.domain.User;
import es.unizar.tmdad.ucode.repository.UserRepository;

@Controller
@Scope("session")
@RequestMapping("/connect")
public class CustomConnectController extends ConnectController {

	private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

	@Autowired
	private Twitter twitter;

	@Autowired
	private Facebook facebook;

	@Autowired
	private Google google;

	@Autowired
	protected UserRepository userRepository;

	private String mail;

	private ConnectionRepository connectionRepository;

	private ConnectionFactoryLocator connectionFactoryLocator;

	private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

	private final UrlPathHelper urlPathHelper = new UrlPathHelper();

	private final MultiValueMap<Class<?>, ConnectInterceptor<?>> connectInterceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();

	private final MultiValueMap<Class<?>, DisconnectInterceptor<?>> disconnectInterceptors = new LinkedMultiValueMap<Class<?>, DisconnectInterceptor<?>>();

	private ConnectSupport connectSupport;

	private boolean deleting = false;

	private String applicationUrl = null;
	
	@Inject
	public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
		this.connectionRepository = connectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
		// super.setApplicationUrl("http://ired.ml");
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the user authorizes the connection, generally done by having
	 * he or she click "Allow" in their web browser at the provider's site. On
	 * authorization verification, connects the user's local account to the
	 * account they hold at the service provider.
	 * 
	 * @param providerId
	 *            the provider ID to connect to
	 * @param request
	 *            the request
	 * @return a RedirectView to the connection status page
	 */
	@Override
	@RequestMapping(value = "/{providerId}", method = RequestMethod.GET, params = "code")
	public RedirectView oauth2Callback(@PathVariable String providerId, NativeWebRequest request) {
		connectSupport = new ConnectSupport(sessionStrategy);
		try {
			OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator
					.getConnectionFactory(providerId);
			Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);

			String uniqueId = "";

			Serializable userProfile = null;
			switch (providerId) {
			case ("google"):
				google = (Google) connection.getApi();
				uniqueId = google.plusOperations().getGoogleProfile().getAccountEmail();
				userProfile = google.plusOperations().getGoogleProfile().toString();
				User aux = userRepository.findByMail(uniqueId);
				if (aux == null) {
					User user = new User(uniqueId, userProfile.toString());
					userRepository.save(user);
				}
				break;
			case ("facebook"):
				facebook = (Facebook) connection.getApi();
				userProfile = facebook.userOperations().getUserProfile();
				uniqueId = facebook.userOperations().getUserProfile().getEmail();
				aux = userRepository.findByMail(uniqueId);
				if (aux == null) {
					User userf = new User(uniqueId, facebook.userOperations().getUserProfile());
					userRepository.save(userf);
				}
				break;
			case ("twitter"):
				twitter = (Twitter) connection.getApi();
				userProfile = twitter.userOperations().getUserProfile();
				// uniqueId =
				// twitter.userOperations().getUserProfile().getScreenName();
				break;
			}

			ArrayList<GrantedAuthority> ja = new ArrayList();
			ja.add(new SimpleGrantedAuthority("USER"));

			SecurityContextHolder.getContext()
					.setAuthentication(new SocialAuthenticationToken(connection, userProfile, null, ja));
			addConnection(connection, connectionFactory, request);
		} catch (Exception e) {
			sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);

			logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + "). Redirecting to "
					+ providerId + " connection status page.");
		}
		return connectionStatusRedirect(providerId, request);
	}
	
	
	/**
	 * Render the status of the connections to the service provider to the user as HTML in their web browser.
	 * @param providerId the ID of the provider to show connection status
     * @param request the request
     * @param model the model
     * @return the view name of the connection status page for all providers
	 */
	@Override
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET)
	public String connectionStatus(@PathVariable String providerId, NativeWebRequest request, Model model) {
		setNoCache(request);
		processFlash(request, model);
		List<Connection<?>> connections = connectionRepository.findConnections(providerId);
		setNoCache(request);
		if (connections.isEmpty()) {
			return connectView(providerId); 
		} else {
			model.addAttribute("connections", connections);
			return connectedView(providerId, request);			
		}
	}
	
	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 * For OAuth2, redirects the user to the provider's site for authorization.
	 * @param providerId the provider ID to connect to
	 * @param request the request
	 * @return a RedirectView to the provider's authorization page or to the connection status page if there is an error
	 */
	@Override
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, NativeWebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(); 
		preConnect(connectionFactory, parameters, request);
		try {
			return new RedirectView(connectSupport.buildOAuthUrl(connectionFactory, request, parameters));
		} catch (Exception e) {
			sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);
			return connectionStatusRedirect(providerId, request);
		}
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the user authorizes the connection, generally done by having
	 * he or she click "Allow" in their web browser at the provider's site. On
	 * authorization verification, connects the user's local account to the
	 * account they hold at the service provider Removes the request token from
	 * the session since it is no longer valid after the connection is
	 * established.
	 * 
	 * @param providerId
	 *            the provider ID to connect to
	 * @param request
	 *            the request
	 * @return a RedirectView to the connection status page
	 */
	@Override
	@RequestMapping(value = "/{providerId}", method = RequestMethod.GET, params = "oauth_token")

	public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
		connectSupport = new ConnectSupport(sessionStrategy);
		try {
			OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator
					.getConnectionFactory(providerId);
			Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);
			String uniqueId = "";
			Serializable userProfile = null;
			switch (providerId) {
			case ("google"):
				google = (Google) connection.getApi();
				uniqueId = google.plusOperations().getGoogleProfile().getAccountEmail();
				userProfile = google.plusOperations().getGoogleProfile().toString();
				User aux = userRepository.findByMail(uniqueId);
				if (aux == null) {
					User user = new User(uniqueId, userProfile.toString());
					userRepository.save(user);
				}
				break;
			case ("facebook"):
				facebook = (Facebook) connection.getApi();
				userProfile = facebook.userOperations().getUserProfile();

				uniqueId = facebook.userOperations().getUserProfile().getEmail();
				aux = userRepository.findByMail(uniqueId);
				if (aux == null) {
					User userf = new User(uniqueId, facebook.userOperations().getUserProfile());
					userRepository.save(userf);
				}
				break;
			case ("twitter"):
				twitter = (Twitter) connection.getApi();
				userProfile = twitter.userOperations().getUserProfile();
				break;
			}
			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(uniqueId, null, null));
			addConnection(connection, connectionFactory, request);
		} catch (Exception e) {
			sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);
			logger.warn("Exception while handling OAuth1 callback (" + e.getMessage() + "). Redirecting to "
					+ providerId + " connection status page.");
		}
		return connectionStatusRedirect(providerId, request);
	}

	/**
	 * Remove all provider connections for a user account. The user has decided
	 * they no longer wish to use the service provider from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the
	 * '_method' request parameter set to 'DELETE' to convert web browser POSTs
	 * to DELETE requests.
	 * 
	 * @param providerId
	 *            the provider ID to remove the connections for
	 * @param request
	 *            the request
	 * @return a RedirectView to the connection status page
	 */
	@RequestMapping(value = "/{providerId}/remove", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeConnectionsC(@PathVariable String providerId, NativeWebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		preDisconnect(connectionFactory, request);
		connectionRepository.removeConnections(providerId);
		postDisconnect(connectionFactory, request);
		SecurityContextHolder.getContext().setAuthentication(null);
		return connectionStatusRedirectC(providerId, request);
	}

	protected String connectedView(String providerId, NativeWebRequest request) {
		// if (providerId.equals("facebook")) {
		// facebook = (Facebook)
		// connectionRepository.getPrimaryConnection(Facebook.class).getApi();
		// String mail = facebook.userOperations().getUserProfile().getEmail();
		// boolean sd = facebook.isAuthorized();
		// logger.info("AUTH: " + sd);
		// logger.info("mail: " + mail);
		// } else if (providerId.equals("twitter")) {
		// twitter = (Twitter)
		// connectionRepository.getPrimaryConnection(Twitter.class).getApi();
		// String twitterName =
		// twitter.userOperations().getUserProfile().getScreenName();
		// /* GUARDADO EN LA BASE DE DATOS DEL PAR TWITTER-MAIL */
		// // TODO
		// /* HACER */
		// } else if (providerId.equals("google")){
		// google = (Google)
		// connectionRepository.getPrimaryConnection(Google.class).getApi();
		// String mail =
		// google.plusOperations().getGoogleProfile().getAccountEmail();
		// logger.info("Mail: " +
		// google.plusOperations().getGoogleProfile().getAccountEmail());
		// }
		// connectionRepository.removeConnections(providerId);

		HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
		String red = (String) nativeReq.getSession().getAttribute("redirect");
		nativeReq.getSession().removeAttribute("redirect");
		if(red != null){
			return "redirect:/" + red;
		}
		logger.info("Deleting : " + deleting);
		return "redirect:/";
	}

	/*
	 * Returns a RedirectView with the URL to redirect to after a connection is
	 * created or deleted. Defaults to "/connect/{providerId}" relative to
	 * DispatcherServlet's path. May be overridden to handle custom redirection
	 * needs.
	 * 
	 * @param providerId the ID of the provider for which a connection was
	 * created or deleted.
	 * 
	 * @param request the NativeWebRequest used to access the servlet path when
	 * constructing the redirect path.
	 * 
	 * @return a RedirectView to the page to be displayed after a connection is
	 * created or deleted
	 */
	protected ResponseEntity<?> connectionStatusRedirectC(String providerId, NativeWebRequest request) {
		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		String path = getPathExtension(servletRequest);
		Iterator<String> it = request.getHeaderNames();
		String har = request.getContextPath();
		if (prependServletPath(servletRequest)) {
			path = servletRequest.getServletPath() + path;
		}
		HttpHeaders h = new HttpHeaders();
		return new ResponseEntity<>(h, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/{providerId}/mail", method = RequestMethod.POST)
	public ResponseEntity<?> mailChecker(@PathVariable String providerId, NativeWebRequest request,
			@RequestParam(value = "mail", required = true) String mail) {
		this.mail = mail;
		HttpHeaders h = new HttpHeaders();
		return new ResponseEntity<>(h, HttpStatus.OK);
	}

	@Deprecated
	@RequestMapping(value = "/{providerId}/check", method = RequestMethod.GET)
	private ResponseEntity<?> response(@PathVariable String providerId) {
		HttpHeaders h = new HttpHeaders();
		switch (providerId) {
		case ("twitter"):
			if (!connectionRepository.findConnections(Twitter.class).isEmpty()) {
				twitter = (Twitter) connectionRepository.getPrimaryConnection(Twitter.class).getApi();
				TwitterProfile a = twitter.userOperations().getUserProfile();
				return new ResponseEntity<>(a, h, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(h, HttpStatus.FORBIDDEN);
			}
		case ("facebook"):
			if (!connectionRepository.findConnections(Facebook.class).isEmpty()) {
				String a = connectionRepository.getPrimaryConnection(Facebook.class).getImageUrl();
				return new ResponseEntity<>(a, h, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(h, HttpStatus.FORBIDDEN);
			}
		case ("google"):
			if (!connectionRepository.findConnections(Google.class).isEmpty()) {
				google = (Google) connectionRepository.getPrimaryConnection(Google.class).getApi();
				String a = google.plusOperations().getGoogleProfile().getImageUrl();
				return new ResponseEntity<>(a, h, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(h, HttpStatus.FORBIDDEN);
			}
		default:
			return new ResponseEntity<>(null, h, HttpStatus.OK);
		}

	}

	private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) {
		try {
			connectionRepository.addConnection(connection);
			postConnect(connectionFactory, connection, request);
		} catch (DuplicateConnectionException e) {
			sessionStrategy.setAttribute(request, DUPLICATE_CONNECTION_ATTRIBUTE, e);
		}
	}
	
	// From InitializingBean
	@Override
	public void afterPropertiesSet() throws Exception {
		connectSupport = new ConnectSupport(sessionStrategy);
		if (applicationUrl != null) {
			this.connectSupport.setApplicationUrl(applicationUrl);
		}
	}

	/*
	 * Determines the path extension, if any. Returns the extension, including
	 * the period at the beginning, or an empty string if there is no extension.
	 * This makes it possible to append the returned value to a path even if
	 * there is no extension.
	 */
	private String getPathExtension(HttpServletRequest request) {
		String fileName = WebUtils.extractFullFilenameFromUrlPath(request.getRequestURI());
		String extension = StringUtils.getFilenameExtension(fileName);
		return extension != null ? "." + extension : "";
	}

	private boolean prependServletPath(HttpServletRequest request) {
		return !this.urlPathHelper.getPathWithinServletMapping(request).equals("");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preConnect(ConnectionFactory<?> connectionFactory, MultiValueMap<String, String> parameters,
			WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.preConnect(connectionFactory, parameters, request);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.postConnect(connection, request);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preDisconnect(ConnectionFactory<?> connectionFactory, WebRequest request) {
		for (DisconnectInterceptor interceptor : interceptingDisconnectionsTo(connectionFactory)) {
			interceptor.preDisconnect(connectionFactory, request);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void postDisconnect(ConnectionFactory<?> connectionFactory, WebRequest request) {
		for (DisconnectInterceptor interceptor : interceptingDisconnectionsTo(connectionFactory)) {
			interceptor.postDisconnect(connectionFactory, request);
		}
	}

	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ConnectionFactory<?> connectionFactory) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(),
				ConnectionFactory.class);
		List<ConnectInterceptor<?>> typedInterceptors = connectInterceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}
	
	private void processFlash(WebRequest request, Model model) {
		convertSessionAttributeToModelAttribute(DUPLICATE_CONNECTION_ATTRIBUTE, request, model);
		convertSessionAttributeToModelAttribute(PROVIDER_ERROR_ATTRIBUTE, request, model);
		model.addAttribute(AUTHORIZATION_ERROR_ATTRIBUTE, sessionStrategy.getAttribute(request, AUTHORIZATION_ERROR_ATTRIBUTE));
		sessionStrategy.removeAttribute(request, AUTHORIZATION_ERROR_ATTRIBUTE);
	}
	
	private void convertSessionAttributeToModelAttribute(String attributeName, WebRequest request, Model model) {
		if (sessionStrategy.getAttribute(request, attributeName) != null) {
			model.addAttribute(attributeName, Boolean.TRUE);
			sessionStrategy.removeAttribute(request, attributeName);
		}
	}
	
	private void setNoCache(NativeWebRequest request) {
		HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
		if (response != null) {
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 1L);
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Cache-Control", "no-store");
		}
	}

	private List<DisconnectInterceptor<?>> interceptingDisconnectionsTo(ConnectionFactory<?> connectionFactory) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(),
				ConnectionFactory.class);
		List<DisconnectInterceptor<?>> typedInterceptors = disconnectInterceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}

}