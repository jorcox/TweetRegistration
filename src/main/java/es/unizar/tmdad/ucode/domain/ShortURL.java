package es.unizar.tmdad.ucode.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class ShortURL {
	
	@Id
	private String hash;
	private String target;
	private URI uri;
	private Date created;
	private Date expire;
	private String owner;
	private String token;
	private Integer mode;
	private String ip;
	private String country;
	private boolean isPrivate;
	private List<String> allowedUsers;
	private ArrayList<String> rules=new ArrayList<String>();

	public ShortURL(String hash, String target, URI uri, Date created,
			Date expire, String owner, String token, Integer mode, String ip, 
			String country, boolean isPrivate, List<String> allowedUsers) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.created = created;
		this.expire = expire;
		this.owner = owner;
		this.token = token;
		this.mode = mode;
		this.ip = ip;
		this.country = country;
		this.isPrivate = isPrivate;
		this.allowedUsers = allowedUsers;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public Date getCreated() {
		return created;
	}
	
	public Date getExpire() {
		return expire;
	}

	public String getOwner() {
		return owner;
	}
	
	public String getToken() {
		return token;
	}

	public Integer getMode() {
		return mode;
	}

	public String getIP() {
		return ip;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public void setAllowedUsers(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}
	
	public void setRules(ArrayList<String> rules){
		this.rules=rules;
	}

	public String getCountry() {
		return country;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}

	public List<String> getAllowedUsers() {
		return allowedUsers;
	}
	
	public ArrayList<String> getRules(){
		return rules;
	}
	
	public void addRule(String rule){
		rules.add(rule);
	}
	
	public void removeRule(int i){
		rules.remove(i);
	}
	

}
