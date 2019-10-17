package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.repository.UserProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ServiceReviewResponse implements IResponse {

	private List<ServiceReviewEntity> serviceReviewArray = new ArrayList<ServiceReviewEntity>();

	@Override
	public List<ServiceReviewEntity> getResponse() {
		return this.serviceReviewArray;
	}

	// private MongoTemplate mongoTemplate;
	// private UserProfileRepository userProfileRepository;

	// @Autowired
	// ServiceReviewResponse(UserProfileRepository userProfileRepository) {
	// this.userProfileRepository = userProfileRepository;
	// // setUserRepository(userProfileRepository);
	// }

	// private static void setUserRepository(UserProfileRepository
	// userProfileRepository) {
	// ServiceReviewResponse.userProfileRepository = userProfileRepository;
	// }

	public static class ServiceReviewPage {
		private List<ServiceReviewEntity> content = new ArrayList<ServiceReviewEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;
		private MongoTemplate mongoTemplate;

		public ServiceReviewPage() {
			super();
		}

		public ServiceReviewPage(PageImpl<ServiceReview> page, User user, MongoTemplate mongoTemplate) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			this.mongoTemplate = mongoTemplate;
			for (ServiceReview serviceReview : page.getContent()) {
				UserProfile reviewUser = new UserProfile();
				if(serviceReview.getUserId() != null){
					reviewUser = getUserProfile(serviceReview.getUserId());
				}
				this.content.add(new ServiceReviewEntity(serviceReview, user,reviewUser));
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

		public UserProfile getUserProfile(String userId) {
			UserProfile userProfile = null;

			Query q = new Query();
			q.addCriteria(Criteria.where("userId").is(userId));
			userProfile = this.mongoTemplate.findOne(q, UserProfile.class);
			// userProfile = userProfileRepository.findByUserId(userId);
			return userProfile;

		}

	}

	public static class ServiceReviewEntity {
		private String id;
		private String serviceId;
		private String userId;
		private Float rating;
		private String review;
		private List<String> likeCount;
		private List<String> unLikeCount;
		private int status;
		private String userName;
		private String parentReviewId;
		private Date createdAt;
		private Date lastModifiedAt;
		private Map<String, String> userImage;

		public ServiceReviewEntity(ServiceReview serviceReview, User user,UserProfile reviewUser) {
			this.setId(serviceReview.getId());
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
			this.setUserImage(reviewUser.getBasicProfileInfo().getProfileImage());
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

		public List<String> getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(List<String> likeCount) {
			this.likeCount = likeCount;
		}

		public List<String> getUnLikeCount() {
			return unLikeCount;
		}

		public void setUnLikeCount(List<String> unLikeCount) {
			this.unLikeCount = unLikeCount;
		}

		public Map<String, String> getUserImage() {
			return userImage;
		}

		public void setUserImage(Map<String, String> userImage) {
			this.userImage = userImage;
		}

	}

	public void add(List<ServiceReview> serviceReviewArray) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null,null));
		}
	}

	public void add(ServiceReview serviceReview) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null,null));
	}

	public void add(List<ServiceReview> serviceReviewArray, User user) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user,null));
		}
	}

	public void add(ServiceReview serviceReview, User user) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user,null));
	}

	public static ServiceReviewPage getPage(PageImpl<ServiceReview> page, User user,MongoTemplate mongoTemplate) {
		ServiceReviewPage res = new ServiceReviewPage(page, user,mongoTemplate);
		return res;
	}

	public ServiceReviewEntity getServiceReviewEntity(ServiceReview serviceReview, User user) {
		return new ServiceReviewEntity(serviceReview, user,null);
	}

}
