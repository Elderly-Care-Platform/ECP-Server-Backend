package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.ServiceRatings;
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
				ServiceRatings userRating = new ServiceRatings();
				float rating = 0;
				if (serviceReview.getUserId() != null) {
					reviewUser = getUserProfile(serviceReview.getUserId());
					userRating = getUserRating(serviceReview.getUserId(), serviceReview.getServiceId());
					if (userRating != null) {
						rating = userRating.getRating();
					}
					reviewUser.setAggrRatingPercentage(getAllUserReviews(serviceReview.getUserId()));
				}
				this.content.add(new ServiceReviewEntity(serviceReview, user, reviewUser, rating));
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

		public ServiceRatings getUserRating(String userId, String serviceId) {
			ServiceRatings userRating = null;

			Query q = new Query();
			q.addCriteria(Criteria.where("userId").is(userId));
			q.addCriteria(Criteria.where("serviceId").is(serviceId));
			userRating = this.mongoTemplate.findOne(q, ServiceRatings.class);
			// userProfile = userProfileRepository.findByUserId(userId);
			return userRating;
		}

		public float getAllUserReviews(String userId) {
			List<ServiceReview> userReviews = null;
			List<ProductReview> userProdReviews= null;
			Query q = new Query();
			q.addCriteria(Criteria.where("userId").is(userId));
			userReviews = this.mongoTemplate.find(q, ServiceReview.class);
			userProdReviews = this.mongoTemplate.find(q, ProductReview.class);
			float total = 0;
			if (userReviews != null && userProdReviews != null) {
				total = userReviews.size() + userProdReviews.size();
			} else if(userReviews != null){
				total =  userReviews.size();
			}else if(userProdReviews != null){
				total = userProdReviews.size();
			}
			return total;
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
		private String title;
		private String userName;
		private String parentReviewId;
		private float contributors;
		private Date createdAt;
		private Date lastModifiedAt;
		private Map<String, String> userImage;

		public ServiceReviewEntity(ServiceReview serviceReview, User user, UserProfile reviewUser, Float userRating) {
			this.setId(serviceReview.getId());
			this.setServiceId(serviceReview.getServiceId());
			this.setUserId(serviceReview.getUserId());
			this.setRating(userRating);
			this.setReview(serviceReview.getReview());
			this.setLikeCount(serviceReview.getLikeCount());
			this.setUnLikeCount(serviceReview.getUnLikeCount());
			this.setTitle(serviceReview.getTitle());
			this.setUserName(reviewUser.getBasicProfileInfo().getFirstName());
			this.setParentReviewId(serviceReview.getParentReviewId());
			this.setContributors(reviewUser.getAggrRatingPercentage());
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public float getContributors() {
			return contributors;
		}

		public void setContributors(float contributors) {
			this.contributors = contributors;
		}

	}

	public void add(List<ServiceReview> serviceReviewArray) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null, null, null));
		}
	}

	public void add(ServiceReview serviceReview) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, null, null, null));
	}

	public void add(List<ServiceReview> serviceReviewArray, User user) {
		for (ServiceReview serviceReview : serviceReviewArray) {
			this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user, null, null));
		}
	}

	public void add(ServiceReview serviceReview, User user) {
		this.serviceReviewArray.add(new ServiceReviewEntity(serviceReview, user, null, null));
	}

	public static ServiceReviewPage getPage(PageImpl<ServiceReview> page, User user, MongoTemplate mongoTemplate) {
		ServiceReviewPage res = new ServiceReviewPage(page, user, mongoTemplate);
		return res;
	}

	public ServiceReviewEntity getServiceReviewEntity(ServiceReview serviceReview, User user) {
		return new ServiceReviewEntity(serviceReview, user, null, null);
	}

}
