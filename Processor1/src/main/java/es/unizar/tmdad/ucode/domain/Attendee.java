package es.unizar.tmdad.ucode.domain;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class Attendee implements Serializable{	
	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private BigInteger id;
	private String name;
	private String teeSize;
	private int age;
	private String mail;
	private String hackathonTag; 
		
	
	public Attendee(String name, String teeSize, int age, String mail, String hackathonTag) {
		super();
		this.name = name;
		this.teeSize = teeSize;
		this.age = age;
		this.mail = mail;
		this.hackathonTag = hackathonTag;
	}
	
	public Attendee() {
		/*
		 * Default constructor is necessary for JSON to object conversion
		 */
	}
	
	
	public BigInteger getId() {
		return id;
	}
	
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTeeSize() {
		return teeSize;
	}
	
	public void setTeeSize(String teeSize) {
		this.teeSize = teeSize;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getHackathonTag() {
		return hackathonTag;
	}

	public void setHackathonTag(String hackathonTag) {
		this.hackathonTag = hackathonTag;
	}

	@Override
	public String toString() {
		return "Attendee [id=" + id + ", name=" + name + ", teeSize=" + teeSize + ", age=" + age + ", mail=" + mail
				+ ", hackathonTag=" + hackathonTag + "]";
	}

}
