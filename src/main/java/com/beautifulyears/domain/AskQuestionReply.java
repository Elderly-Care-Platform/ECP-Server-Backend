package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "askquestionreply")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AskQuestionReply {

	@Id
	private String 	id;
	private String 	askQuestionId;
	private String 	reply;
	private User 	user;
	private final Date createdAt = new Date();
	private Date lastModifiedAt = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAskQuestionId() {
		return askQuestionId;
	}

	public void setAskQuestionId(String askQuestionId) {
		this.askQuestionId = askQuestionId;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AskQuestionReply(String askQuestionId, String reply, User user) {
		this.askQuestionId = askQuestionId;
		this.reply = reply;
		this.user = user;
		this.lastModifiedAt = new Date();
	}
	public AskQuestionReply(){
	}
}
