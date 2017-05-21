package es.unizar.tmdad.ucode.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class User {
	
	@Id
	private BigInteger id;
	private String mail;
	private String password;
	private Serializable provider;
	@DBRef
	private List<Hackathon> hackathons;
	
	public User() {
		/*
		 * Default constructor is necessary for JSON to object conversion
		 */
	}
	
	public User(String mail, String password) {
		this.mail = mail;
		this.password = password;
	}
	
	public User(String mail, Serializable provider) {
		this.mail = mail;
		this.provider = provider;
	}

	public BigInteger getId() {
		return id;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Serializable getProvider() {
		return provider;
	}


	public void setId(BigInteger id) {
		this.id = id;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setProvider(Serializable provider) {
		this.provider = provider;
	}

	public List<Hackathon> getHackathons() {
		return hackathons;
	}

	public void setHackathons(List<Hackathon> hackathons) {
		this.hackathons = hackathons;
	}

	@Override
	public String toString() {
		return "User[id=" + id + ", mail='" + mail + "', password='" + password 
				+ "', provider='" + provider + "]";
	}
}
