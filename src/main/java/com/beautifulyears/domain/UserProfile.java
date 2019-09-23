package com.beautifulyears.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.beautifulyears.domain.menu.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The UserProfile class specifies profile information of all types of users
 * including individual and institutions. Documents corresponding to this class
 * are stored in user_profile collection in MongodB.
 * 
 * @author jharana
 */
@Document(collection = "user_profile")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {
	@Id
	private String id;

	// primary user ID from User.java
	private String userId;

	// @see @UserTypes.java
	private List<Integer> userTypes = new ArrayList<Integer>();

	// contains all common user profile information.
	private BasicProfileInfo basicProfileInfo = new BasicProfileInfo();

	// contains information applicable to an individual
	private IndividualProfileInfo individualInfo = new IndividualProfileInfo();

	// contains information about service provider
	private ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
	
	@DBRef
	private List<UserProfile> serviceBranches = new ArrayList<UserProfile>();

	@DBRef
	private List<HousingFacility> facilities = new ArrayList<HousingFacility>();

	private String tags;

	private boolean isFeatured;

	private boolean verified;

	private int age;
	
	private String workTitle;

	@DBRef
	private List<AskCategory> experties;

	private final Date createdAt = new Date();

	private Date lastModifiedAt = new Date();
	@DBRef
	private List<Tag> systemTags = new ArrayList<Tag>();

	private List<String> userTags = new ArrayList<String>();

	private int status; // Unparroved, verified, etc.

	@JsonIgnore
	private List<String> reviewedBy = new ArrayList<String>();

	@JsonIgnore
	private List<String> ratedBy = new ArrayList<String>();

	private Float aggrRatingPercentage = 0f;

	@Transient
	private boolean isReviewedByUser = false;

	@Transient
	private boolean isRatedByUser = false;

	@JsonProperty
	public boolean isReviewedByUser() {
		return isReviewedByUser;
	}

	@JsonIgnore
	public void setReviewedByUser(boolean isReviewedByUser) {
		this.isReviewedByUser = isReviewedByUser;
	}

	@JsonProperty
	public boolean isRatedByUser() {
		return isRatedByUser;
	}

	@JsonIgnore
	public void setRatedByUser(boolean isRatedByUser) {
		this.isRatedByUser = isRatedByUser;
	}

	@JsonProperty
	public Float getAggrRatingPercentage() {
		return aggrRatingPercentage;
	}

	@JsonIgnore
	public void setAggrRatingPercentage(Float aggrRating) {
		this.aggrRatingPercentage = aggrRating;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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

	public List<Integer> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<Integer> userTypes) {
		this.userTypes = userTypes;
	}

	public BasicProfileInfo getBasicProfileInfo() {
		return basicProfileInfo;
	}

	public void setBasicProfileInfo(BasicProfileInfo basicProfileInfo) {
		this.basicProfileInfo = basicProfileInfo;
	}

	public IndividualProfileInfo getIndividualInfo() {
		return individualInfo;
	}

	public void setIndividualInfo(IndividualProfileInfo individualInfo) {
		this.individualInfo = individualInfo;
	}
	
	public List<UserProfile> getServiceBranches() {
		return serviceBranches;
	}

	public void setServiceBranches(List<UserProfile> serviceBranches) {
		this.serviceBranches = serviceBranches;
	}

	public ServiceProviderInfo getServiceProviderInfo() {
		return serviceProviderInfo;
	}

	public void setServiceProviderInfo(ServiceProviderInfo serviceProviderInfo) {
		this.serviceProviderInfo = serviceProviderInfo;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean isFeatured() {
		return isFeatured;
	}

	public void setFeatured(boolean isFeatured) {
		this.isFeatured = isFeatured;
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

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(List<String> reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public List<String> getRatedBy() {
		return ratedBy;
	}

	public void setRatedBy(List<String> ratedBy) {
		this.ratedBy = ratedBy;
	}

	public List<HousingFacility> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<HousingFacility> facilities) {
		this.facilities = facilities;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public List<AskCategory> getExperties() {
		return experties;
	}

	public void setExperties(List<AskCategory> experties) {
		this.experties = experties;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", userId=" + userId + ", userTypes=" + userTypes + ", basicProfileInfo="
				+ basicProfileInfo + ", individualInfo=" + individualInfo + ", serviceProviderInfo="
				+ serviceProviderInfo + ", serviceBranches=" + serviceBranches + ", facilities=" + facilities
				+ ", tags=" + tags + ", isFeatured=" + isFeatured + ", verified=" + verified + ", createdAt="
				+ createdAt + ", lastModifiedAt=" + lastModifiedAt + ", systemTags=" + systemTags + ", userTags="
				+ userTags + ", status=" + status + ", reviewedBy=" + reviewedBy + ", ratedBy=" + ratedBy
				+ ", aggrRatingPercentage=" + aggrRatingPercentage + ", isReviewedByUser=" + isReviewedByUser
				+ ", age=" + age + ", workTitle=" + workTitle + ", experties=" + experties
				+ ", isRatedByUser=" + isRatedByUser + "]";
	}

}
