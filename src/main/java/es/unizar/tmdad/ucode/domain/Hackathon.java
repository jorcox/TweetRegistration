package es.unizar.tmdad.ucode.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Hackathon {
	
	@Id
	private BigInteger id;
	private String nombre;
	private String lugar;
	private String web;	
	private String tag;
	@DBRef
	private List<Attendee> attendees = new ArrayList<>();
	
	
	
	public Hackathon(BigInteger id, String nombre, String lugar, String web, String tag) {
		this.id = id;
		this.nombre = nombre;
		this.lugar = lugar;
		this.web = web;
		this.tag = tag;
		//this.attendees = attendees;
	}

	public Hackathon() {
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<Attendee> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}

	@Override
	public String toString() {
		return "Hackathon [id=" + id + ", nombre=" + nombre + ", lugar=" + lugar + ", web=" + web + "]";
	}
	
	

}
