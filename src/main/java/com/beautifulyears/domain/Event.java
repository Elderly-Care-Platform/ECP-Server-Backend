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
	private int 	capacity;
	private int 	entryFee;
	private int 	eventType;
	private int 	status;
	private String 	address;
	private String 	landmark;
	private String 	languages;
	private String 	organiser;
	private String 	orgPhone;
	private String 	orgEmail;
	private String	createdBy;
	private final Date createdAt = new Date();
	private Date lastModifiedAt = new Date();

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getLanguages() {
		return languages;
	}
	public void setLanguages(String languages) {
		this.languages = languages;
	}
	public String getOrganiser() {
		return organiser;
	}
	public void setOrganiser(String organiser) {
		this.organiser = organiser;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	public String getOrgPhone() {
		return orgPhone;
	}
	public void setOrgPhone(String orgPhone) {
		this.orgPhone = orgPhone;
	}
	public String getOrgEmail() {
		return orgEmail;
	}
	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public Event(String title, Date datetime, String description, int capacity, int entryFee, int eventType,
			int status, String address, String landmark, String languages,
			String organiser, String orgPhone, String orgEmail, String createdBy) {
		this.title = title;
		this.datetime = datetime;
		this.description = description;
		this.capacity = capacity;
		this.entryFee = entryFee;
		this.eventType = eventType;
		this.status = status;
		this.address = address;
		this.landmark = landmark;
		this.languages = languages;
		this.organiser = organiser;
		this.orgPhone = orgPhone;
		this.orgEmail = orgEmail;
		this.createdBy = createdBy;
	}

	public Event() {
	}
}
