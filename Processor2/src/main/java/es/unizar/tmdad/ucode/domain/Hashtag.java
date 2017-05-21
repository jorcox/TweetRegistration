package es.unizar.tmdad.ucode.domain;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class Hashtag implements Serializable{	
	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private BigInteger id;
	private String name;
	private int count; 
		
	
	public Hashtag(String name, int count) {
		super();
		this.name = name;
		this.count = count;
	}
	
	public Hashtag() {
		/*
		 * Default constructor is necessary for JSON to object conversion
		 */
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increment(){
		this.count++;
	}

	@Override
	public String toString() {
		return "Hashtag [id=" + id + ", name=" + name + ", count=" + count + "]";
	}

}
