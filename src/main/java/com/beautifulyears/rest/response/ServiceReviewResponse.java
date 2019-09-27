package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.domain.User;

public class ServiceReviewResponse implements IResponse {

	private List<ServiceReviewEntity> serviceReviewArray = new ArrayList<ServiceReviewEntity>();

	@Override
	public List<ServiceReviewEntity> getResponse() {
		return this.serviceReviewArray;
	}

	public static class ServiceReviewPage {
		private List<ServiceReviewEntity> content = new ArrayList<ServiceReviewEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public ServiceReviewPage() {
			super();
		}

		public ServiceReviewPage(PageImpl<ServiceReview> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (ServiceReview serviceReview : page.getContent()) {
				this.content.add(new ServiceReviewEntity(serviceReview, user));
			}
			this.size = page.getSize();
			this.total = page.getTotal();
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public List<ServiceReviewEntity> getContent() {
			return content;
		}

		public void setContent(List<ServiceReviewEntity> content) {
			this.content = content;
		}

		public boolean isLastPage() {
			return lastPage;
		}

		public void setLastPage(boolean lastPage) {
			this.lastPage = lastPage;
		}

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

	}

	public static class ServiceReviewEntity {
		private String id;
		private String serviceId;
		private String userId;
		private Float rating;
		private String review;
		private Integer likeCount;
		private Integer unLikeCount;
		private int status;
		private String userName;
		private String parentReviewId;
		private Date createdAt;
		private Date lastModifiedAt;

		public ServiceReviewEntity(ServiceReview serviceReview, User user) {
			this.setServiceId(serviceReview.getServiceId());
			this.setUserId(serviceReview.getUserId());
			this.setRating(serviceReview.getRating());
			this.setReview(serviceReview.getReview());
			this.setLikeCount(serviceReview.getLikeCount());
			this.setUnLikeCount(serviceReview.getUnLikeCount());
			this.setStatus(serviceReview.getStatus());
			this.setUserName(serviceReview.getUserName());
			this.setParentReviewId(serviceReview.getParentReviewId());
			this.setCreatedAt(serviceReview.getCreatedAt());
			this.setLastModifiedAt(serviceReview.getLastModifiedAt());

		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public Float getRating() {
			return rating;
		}

		public void setRating(Float rating) {
			this.rating = rating;
		}

		public String getReview() {
			return review;
		}

		public void setReview(String review) {
			this.review = review;
		}

		public Integer getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(Integer likeCount) {
			this.likeCount = likeCount;
		}

		public Integer getUnLikeCount() {
			return unLikeCount;
		}

		public void setUnLikeCount(Integer unLikeCount) {
			this.unLikeCount = unLikeCount;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getParentReviewId() {
			return parentReviewId;
		}

		public void setParentReviewId(String parentReviewId) {
			this.parentReviewId = parentReviewId;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public Date getLastModifiedAt() {
			return lastModifiedAt;
		}

		public void setLastModifiedAt(Date lastModifiedAt) {
			this.lastModifiedAt = lastModifiedAt;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
	}

	public void add(List<ServiceReview> serviceReviewArray) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null));
		}
	}

	public void add(ServiceReview serviceReview) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null));
	}

	public void add(List<ServiceReview> serviceReviewArray, User user) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user));
		}
	}

	public void add(ServiceReview serviceReview, User user) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user));
	}

	public static ServiceReviewPage getPage(PageImpl<ServiceReview> page, User user) {
		ServiceReviewPage res = new ServiceReviewPage(page, user);
		return res;
	}

	public ServiceReviewEntity getServiceReviewEntity(ServiceReview serviceReview, User user) {
		return new ServiceReviewEntity(serviceReview, user);
	}

}
