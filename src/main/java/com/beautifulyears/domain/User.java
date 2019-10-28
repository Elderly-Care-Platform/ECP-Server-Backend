package com.beautifulyears.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.beautifulyears.constants.UserRolePermissions;

/**
 * @author Nitin
 *
 */
@Document(collection = "users")
public class User implements Serializable {

	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	// Unique
	String userName;
	String password;

	// Unique
	String email;
	private Date createdAt = new Date();
	private String verificationCode = UUID.randomUUID().toString();
	private Date verificationCodeExpiry = this
			.setCodeExpiryDate(new Date(), 15);
	private String socialSignOnId;
	private String socialSignOnPlatform;
	private String passwordCode;
	private Date passwordCodeExpiry;
	private String userRoleId;
	private String isActive;
	private Integer userIdType;
	private Integer userRegType;
	private String phoneNumber;
	private List<String> userTags = new ArrayList<String>();
	private List<String> favEvents = new ArrayList<String>();

	private List<Integer> permissions = new ArrayList<Integer>();

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<String> userTags) {
		this.userTags = userTags;
	}

	public Integer getUserRegType() {
		return userRegType;
	}

	public void setUserRegType(Integer userRegType) {
		this.userRegType = userRegType;
	}

	public Integer getUserIdType() {
		return userIdType;
	}

	public void setUserIdType(Integer userIdType) {
		this.userIdType = userIdType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public List<Integer> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Integer> permissions) {
		this.permissions = permissions;
	}

	private Date setCodeExpiryDate(Date now, long daysToExpire) {
		now.setTime(now.getTime() + daysToExpire * 1000L * 60 * 60 * 24);
		return now;
	}

	public String isActive() {
		return isActive;
	}

	public void setActive(String isActive) {
		this.isActive = isActive;
	}

	public User() {

	}

	public User(String userName, Integer userIdType, Integer userRegType,
			String password, String email, String phoneNumber,
			String verificationCode, Date verificationCodeExpiry,
			String socialSignOnId, String socialSignOnPlatform,
			String passwordCode, Date passwordCodeExpiry, String userRoleId,
			String isActive, List<String> userTags, List<String> favEvents) {
		super();
		this.userName = userName;
		this.password = password;
		this.email = email != null ? email.toLowerCase() : email;
		this.userRegType = userRegType;
		this.userIdType = userIdType;
		this.phoneNumber = phoneNumber;
		this.verificationCode = verificationCode;
		this.verificationCodeExpiry = verificationCodeExpiry;
		this.socialSignOnId = socialSignOnId;
		this.socialSignOnPlatform = socialSignOnPlatform;
		this.passwordCode = passwordCode;
		this.passwordCodeExpiry = passwordCodeExpiry;
		this.userRoleId = userRoleId;
		this.isActive = isActive;
		this.permissions = UserRolePermissions.getUserPermsForRole(userRoleId);
		this.userTags = userTags;
		this.favEvents = favEvents;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email != null ? email.toLowerCase() : email;
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public Date getVerificationCodeExpiry() {
		return verificationCodeExpiry;
	}

	public void setVerificationCodeExpiry(Date verificationCodeExpiry) {
		this.verificationCodeExpiry = verificationCodeExpiry;
	}

	public String getSocialSignOnId() {
		return socialSignOnId;
	}

	public void setSocialSignOnId(String socialSignOnId) {
		this.socialSignOnId = socialSignOnId;
	}

	public String getSocialSignOnPlatform() {
		return socialSignOnPlatform;
	}

	public void setSocialSignOnPlatform(String socialSignOnPlatform) {
		this.socialSignOnPlatform = socialSignOnPlatform;

	}

	public String getPasswordCode() {
		return passwordCode;
	}

	public void setPasswordCode(String passwordCode) {
		this.passwordCode = passwordCode;
	}

	public Date getPasswordCodeExpiry() {
		return passwordCodeExpiry;
	}

	public void setPasswordCodeExpiry(Date passwordCodeExpiry) {
		this.passwordCodeExpiry = passwordCodeExpiry;
	}

	public String getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
		this.permissions = UserRolePermissions.getUserPermsForRole(userRoleId);

	}

	public List<String> getFavEvents() {
		return favEvents;
	}

	public void setFavEvents(List<String> favEvents) {
		this.favEvents = favEvents;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password="
				+ password + ", email=" + email + ", createdAt=" + createdAt
				+ ", verificationCode=" + verificationCode
				+ ", verificationCodeExpiry=" + verificationCodeExpiry
				+ ", socialSignOnId=" + socialSignOnId
				+ ", socialSignOnPlatform=" + socialSignOnPlatform
				+ ", passwordCode=" + passwordCode + ", passwordCodeExpiry="
				+ passwordCodeExpiry + ", userRoleId=" + userRoleId
				+ ", isActive=" + isActive + ", permissions=" + permissions + "]";
	}

}
