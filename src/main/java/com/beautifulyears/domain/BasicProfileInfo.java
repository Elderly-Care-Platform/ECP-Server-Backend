package com.beautifulyears.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.TextIndexed;

import com.beautifulyears.domain.UserAddress;

/**
 * The BasicProfileInfo class contains basic information of all types of users
 * Documents corresponding to this class are stored as part of user_profile
 * document in MongodB.
 * 
 * @author jharana
 */

public class BasicProfileInfo {

	// corresponds to company Name in institutions and user first name for
	// individuals. name in
	@TextIndexed(weight=1)
	private String firstName;

	// in case of individuals it is the profile or it is the company logo.
	private Map<String, String> profileImage;

	private String primaryEmail; // the

	private List<String> secondaryEmails = new ArrayList<String>();

	private String primaryPhoneNo;

	private List<String> secondaryPhoneNos = new ArrayList<String>();

	// personal story or professional services description
	@TextIndexed(weight=2)
	private String description;

	private List<Map<String, String>> photoGalleryURLs = new ArrayList<Map<String, String>>();

	private UserAddress primaryUserAddress = new UserAddress();

	private List<UserAddress> otherAddresses = new ArrayList<UserAddress>();
	
	private String shortDescription;

	private Boolean isSubscribedForNewsletter;

	public UserAddress getPrimaryUserAddress() {
		return primaryUserAddress;
	}

	public void setPrimaryUserAddress(UserAddress primaryUserAddress) {
		this.primaryUserAddress = primaryUserAddress;
	}

	public List<UserAddress> getOtherAddresses() {
		return otherAddresses;
	}

	public void setOtherAddresses(List<UserAddress> otherAddresses) {
		this.otherAddresses = otherAddresses;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Map<String, String> getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(Map<String, String> profileImage) {
		this.profileImage = profileImage;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	public List<String> getSecondaryEmails() {
		return secondaryEmails;
	}

	public void setSecondaryEmails(List<String> secondaryEmails) {
		this.secondaryEmails = secondaryEmails;
	}

	public String getPrimaryPhoneNo() {
		return primaryPhoneNo;
	}

	public void setPrimaryPhoneNo(String primaryPhoneNo) {
		this.primaryPhoneNo = primaryPhoneNo;
	}

	public List<String> getSecondaryPhoneNos() {
		return secondaryPhoneNos;
	}

	public void setSecondaryPhoneNos(List<String> secondaryPhoneNos) {
		this.secondaryPhoneNos = secondaryPhoneNos;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Map<String, String>> getPhotoGalleryURLs() {
		return photoGalleryURLs;
	}

	public void setPhotoGalleryURLs(List<Map<String, String>> photoGalleryURLs) {
		this.photoGalleryURLs = photoGalleryURLs;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@Override
	public String toString() {
		return "BasicProfileInfo [firstName=" + firstName + ", profileImage="
				+ profileImage + ", primaryEmail=" + primaryEmail
				+ ", secondaryEmails=" + secondaryEmails + ", primaryPhoneNo="
				+ primaryPhoneNo + ", secondaryPhoneNos=" + secondaryPhoneNos
				+ ", description=" + description + ", photoGalleryURLs="
				+ photoGalleryURLs + ", primaryUserAddress="
				+ primaryUserAddress + ", otherAddresses=" + otherAddresses
				+ ", shortDescription=" + shortDescription + "]";
	}

	public Boolean getIsSubscribedForNewsletter() {
		return isSubscribedForNewsletter;
	}

	public void setIsSubscribedForNewsletter(Boolean isSubscribedForNewsletter) {
		this.isSubscribedForNewsletter = isSubscribedForNewsletter;
	}

	

}
