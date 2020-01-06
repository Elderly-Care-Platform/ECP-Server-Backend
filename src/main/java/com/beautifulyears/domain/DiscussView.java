/**
 * Jun 25, 2015
 * Nitin
 * 10:09:50 AM
 */
package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "discuss_view")
public class DiscussView {

	@Id
	private String id;
	private String contentId;
	private String userId;
	private String ipAddress;
	private Date viewAt;
	
	public DiscussView() {
		super();
	}

	public DiscussView(String contentId, String userId, Date viewAt, String ipAddress) {
		this.userId = userId;
		this.contentId = contentId;
		this.viewAt = viewAt;
		this.ipAddress = ipAddress;
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

	public Date getViewAt() {
		return viewAt;
	}

	public void setViewAt(Date viewAt) {
		this.viewAt = viewAt;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
