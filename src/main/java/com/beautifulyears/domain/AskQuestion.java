package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "askquestion")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AskQuestion {

	@Id
	private String 	id;
	private String 	question;

	@DBRef
	private AskCategory askCategory;

	@DBRef
	private User	askedBy;

	@DBRef
	private User	answeredBy;
	
	private Boolean	answered;
	private final Date createdAt = new Date();
	private Date lastModifiedAt = new Date();

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
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

	public User getAnsweredBy() {
		return answeredBy;
	}

	public void setAnsweredBy(User answeredBy) {
		this.answeredBy = answeredBy;
	}

	public Boolean getAnswered() {
		return answered;
	}

	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}
	
	public User getAskedBy() {
		return askedBy;
	}

	public void setAskedBy(User askedBy) {
		this.askedBy = askedBy;
	}

	public AskCategory getAskCategory() {
		return askCategory;
	}

	public void setAskCategory(AskCategory askCategory) {
		this.askCategory = askCategory;
	}
	
	public AskQuestion() {
	}

	public AskQuestion(String question, AskCategory askCategory, User askedBy, User answeredBy, Boolean answered) {
		this.question = question;
		this.askCategory = askCategory;
		this.askedBy = askedBy;
		this.answeredBy = answeredBy;
		this.answered = answered;
	}
}
