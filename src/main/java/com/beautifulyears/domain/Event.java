package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "event")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

	@Id
	private String 	id;
	private String 	title;
	private Date 	datetime;
	private String 	description;
	private int 	entryFee;
	private int 	perPerson;
	private int 	eventType;
	private int 	status;
	private String 	email;
	private String 	location;
	private String 	locLat;
	private String 	locLng;
	private String 	languages;
	private String 	phone;
	private String 	organiser;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getEntryFee() {
		return entryFee;
	}
	public void setEntryFee(int entryFee) {
		this.entryFee = entryFee;
	}
	public int getPerPerson() {
		return perPerson;
	}
	public void setPerPerson(int perPerson) {
		this.perPerson = perPerson;
	}
	public int getEventType() {
		return eventType;
	}
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocLat() {
		return locLat;
	}
	public void setLocLat(String locLat) {
		this.locLat = locLat;
	}
	public String getLocLng() {
		return locLng;
	}
	public void setLocLng(String locLng) {
		this.locLng = locLng;
	}
	public String getLanguages() {
		return languages;
	}
	public void setLanguages(String languages) {
		this.languages = languages;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOrganiser() {
		return organiser;
	}
	public void setOrganiser(String organiser) {
		this.organiser = organiser;
	}

	public Event(String title, Date datetime, String description, int entryFee, int perPerson, int eventType,
			int status, String email, String location, String locLat, String locLng, String languages, String phone,
			String organiser) {
		this.title = title;
		this.datetime = datetime;
		this.description = description;
		this.entryFee = entryFee;
		this.perPerson = perPerson;
		this.eventType = eventType;
		this.status = status;
		this.email = email;
		this.location = location;
		this.locLat = locLat;
		this.locLng = locLng;
		this.languages = languages;
		this.phone = phone;
		this.organiser = organiser;
	}

	public Event() {
	}
}
