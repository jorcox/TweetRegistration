package es.unizar.tmdad.ucode.domain;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class Attendee {	
	@Id
	private BigInteger id;
	private String name;
	private String teeSize;
	private int age;
	private String mail;
	
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
	@Override
	public String toString() {
		return "Attendee [id=" + id + ", name=" + name + ", teeSize=" + teeSize + ", age=" + age + ", mail=" + mail
				+ "]";
	}
	
	

}
