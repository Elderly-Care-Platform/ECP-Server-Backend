package com.beautifulyears.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.domain.menu.Tag;
import com.beautifulyears.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//The discuss collection represents Articles, Questions and Posts
@Document(collection = "discuss")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discuss {

	@Id
	private String id;
	@TextIndexed(weight=1)
	private String title;

	private Map<String, String> articlePhotoFilename;

	private String userId;
	@TextIndexed(weight=2)
	private String username;

	private String discussType; // Q, P and A (Question, Post and Article)
	@TextIndexed(weight=3)
	private String text;

	private int status; // published, unpublished
	
	@DBRef
	private UserProfile userProfile;

	@DBRef
	private List<Tag> systemTags = new ArrayList<Tag>();

	private List<String> userTags = new ArrayList<String>();

	private int aggrReplyCount;

	private int directReplyCount;

	private int viewCount;

	private final Date createdAt = new Date();

	private Date lastModifiedAt = new Date();

	private List<String> topicId;

	private List<String> likedBy = new ArrayList<String>();

	private boolean isFeatured;

	private Boolean isPromotion;

	private long shareCount = 0;

	private String shortSynopsis;

	private int contentType;

	@TextIndexed(weight=4)
	private LinkInfo linkInfo;

	public Discuss() {

	}

	public Discuss(String userId, String username, String discussType,
			List<String> topicId, String title, String text, int status,
			int aggrReplyCount,int viewCount, List<Tag> systemTags, Long sharedCount,
			List<String> userTags, Map<String, String> articlePhotoFilename,
			Boolean isFeatured,Boolean isPromotion, int contentType, LinkInfo linkInfo,UserProfile profile) {
		super();
		this.userId = userId;
		this.username = username;
		this.discussType = discussType;
		this.title = title;
		this.topicId = topicId;
		this.text = text;
		org.jsoup.nodes.Document doc = Jsoup.parse(this.text);
		String domText = doc.text();
		if (domText.length() > DiscussConstants.DISCUSS_TRUNCATION_LENGTH) {
			this.setShortSynopsis(Util.truncateText(domText));
		}
		this.status = status;
		this.aggrReplyCount = aggrReplyCount;
		this.viewCount = viewCount;
		this.articlePhotoFilename = articlePhotoFilename;
		this.isFeatured = isFeatured;
		this.isPromotion = isPromotion;
		this.systemTags = systemTags;
		this.shareCount = sharedCount;
		this.userTags = userTags;
		this.contentType = contentType;
		this.linkInfo = linkInfo;
		this.userProfile = profile;
	}
	
	

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public Boolean isPromotion() {
		return isPromotion;
	}

	public void setPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public LinkInfo getLinkInfo() {
		return linkInfo;
	}

	public void setLinkInfo(LinkInfo linkInfo) {
		this.linkInfo = linkInfo;
	}

	public String getShortSynopsis() {
		return shortSynopsis;
	}

	public void setShortSynopsis(String shortSynopsis) {
		this.shortSynopsis = shortSynopsis;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	public int getDirectReplyCount() {
		return directReplyCount;
	}

	public void setDirectReplyCount(int directReplyCount) {
		this.directReplyCount = directReplyCount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public boolean isFeatured() {
		return isFeatured;
	}

	public void setFeatured(boolean isFeatured) {
		this.isFeatured = isFeatured;
	}

	public List<String> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}

	public Map<String, String> getArticlePhotoFilename() {
		return articlePhotoFilename;
	}

	public void setArticlePhotoFilename(Map<String, String> articlePhotoFilename) {
		this.articlePhotoFilename = articlePhotoFilename;
	}

	public List<String> getTopicId() {
		return topicId;
	}

	public void setTopicId(List<String> topicId) {
		this.topicId = topicId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDiscussType() {
		return discussType;
	}

	public void setDiscussType(String postType) {
		this.discussType = postType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Tag> getSystemTags() {
		return systemTags;
	}

	public void setSystemTags(List<Tag> systemTags) {
		this.systemTags = systemTags;
	}

	public List<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<String> userTags) {
		this.userTags = userTags;
	}

	public int getAggrReplyCount() {
		return aggrReplyCount;
	}

	public void setAggrReplyCount(int aggrReplyCount) {
		this.aggrReplyCount = aggrReplyCount;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
}
