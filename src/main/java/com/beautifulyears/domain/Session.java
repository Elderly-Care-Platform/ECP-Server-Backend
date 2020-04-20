package com.beautifulyears.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.DiscussConstants;

@Document(collection = "session")
public class Session implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;
	@Id
	private String sessionId = UUID.randomUUID().toString();
	private String userId;
	private String userName;
	private String userEmail;
	private Integer userIdType;
	private String phoneNumber;
	private Date createdAt = new Date();
	private int status = DiscussConstants.SESSION_STATUS_ACTIVE;
	private String ipAddress;
	private int sessionType;

	@Transient
	private User user;

	public Session() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Session(User user, boolean isPasswordEntered, HttpServletRequest req) {
		if (null != user) {
			this.setUserId(user.getId());
			this.setUserEmail(user.getEmail());
			this.setUserIdType(user.getUserIdType());
			this.setPhoneNumber(user.getPhoneNumber());
			this.setUserName(user.getUserName());
			this.setUser(user);
			if(user.getUserRegType() != null){
				if (user.getUserRegType() == BYConstants.USER_REG_TYPE_FULL
						|| user.getUserRegType() == BYConstants.USER_REG_TYPE_SOCIAL) {
					if (isPasswordEntered) {
						this.sessionType = BYConstants.SESSION_TYPE_FULL;
					} else {
						this.sessionType = BYConstants.SESSION_TYPE_PARTIAL;
					}
				} else if (user.getUserRegType() == BYConstants.USER_REG_TYPE_GUEST) {
					this.sessionType = BYConstants.SESSION_TYPE_GUEST;
				}
			}
		}
		String ipAddress = req.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = req.getRemoteAddr();
		}
		this.setIpAddress(ipAddress);

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getSessionType() {
		return sessionType;
	}

	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
