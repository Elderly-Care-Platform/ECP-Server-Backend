package com.beautifulyears.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

//The discuss_comment collection represents comments
@Document(collection = "discuss_replies")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscussReply {

	@Id
	private String id;
	private String discussId;
	
	private int contentType;

	private String userId;

	private String parentReplyId;

	private List<String> ancestorsId = new ArrayList<String>();

	private String userName;

	private int replyType;

	private Date createdAt = new Date();

	private int childrenCount;

	private int directChildrenCount;

	private List<String> likedBy = new ArrayList<String>();

	private String text;

	private Date lastModifiedAt;

	private int status;
	
	private Float userRating;
	
	@JsonIgnore
	public int getContentType() {
		return contentType;
	}
	
	@JsonIgnore
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	@JsonProperty
	public Float getUserRating() {
		return userRating;
	}

	@JsonIgnore
	public void setUserRating(Float userRating) {
		this.userRating = userRating;
	}

	@Transient
	private int likeCount;

	@Transient
	private boolean isLikedByUser = false;

	@JsonProperty
	public int getLikeCount() {
		return likeCount;
	}

	@JsonIgnore
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	@JsonProperty
	public boolean isLikedByUser() {
		return isLikedByUser;
	}

	@JsonIgnore
	public void setLikedByUser(boolean isLikedByUser) {
		this.isLikedByUser = isLikedByUser;
	}

	@JsonProperty
	public int getStatus() {
		return status;
	}

	@JsonProperty
	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	private List<DiscussReply> replies = new ArrayList<DiscussReply>();

	@JsonProperty
	public List<DiscussReply> getReplies() {
		return replies;
	}

	@JsonIgnore
	public void setReplies(List<DiscussReply> replies) {
		this.replies = replies;
	}

	@JsonProperty
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	@JsonIgnore
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	@JsonProperty
	public String getText() {
		return text;
	}

	@JsonProperty
	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty
	public int getReplyType() {
		return replyType;
	}

	@JsonIgnore
	public void setReplyType(int replyType) {
		this.replyType = replyType;
	}

	@JsonProperty
	public String getId() {
		return id;
	}

	@JsonIgnore
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty
	public String getDiscussId() {
		return discussId;
	}

	@JsonProperty
	public void setDiscussId(String discussId) {
		this.discussId = discussId;
	}

	@JsonProperty
	public String getUserId() {
		return userId;
	}

	@JsonIgnore
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonIgnore
	public String getParentReplyId() {
		return parentReplyId;
	}

	@JsonProperty
	public void setParentReplyId(String parentReplyId) {
		this.parentReplyId = parentReplyId;
	}

	@JsonIgnore
	public List<String> getAncestorsId() {
		return ancestorsId;
	}

	@JsonIgnore
	public void setAncestorsId(List<String> ancestorsId) {
		this.ancestorsId = ancestorsId;
	}

	@JsonProperty
	public String getUserName() {
		return userName;
	}

	@JsonIgnore
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty
	public Date getCreatedAt() {
		return createdAt;
	}

	@JsonIgnore
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty
	public int getChildrenCount() {
		return childrenCount;
	}

	@JsonIgnore
	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	@JsonProperty
	public int getDirectChildrenCount() {
		return directChildrenCount;
	}

	@JsonIgnore
	public void setDirectChildrenCount(int directChildrenCount) {
		this.directChildrenCount = directChildrenCount;
	}

	@JsonIgnore
	public List<String> getLikedBy() {
		return likedBy;
	}

	@JsonIgnore
	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}

}
