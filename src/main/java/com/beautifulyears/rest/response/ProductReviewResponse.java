package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ProductRating;
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ProductReviewResponse implements IResponse {

	private List<ProductReviewEntity> productReviewArray = new ArrayList<ProductReviewEntity>();

	@Override
	public List<ProductReviewEntity> getResponse() {
		return this.productReviewArray;
	}

	public static class ProductReviewPage {
		private List<ProductReviewEntity> content = new ArrayList<ProductReviewEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;
		private MongoTemplate mongoTemplate;

		public ProductReviewPage() {
			super();
		}

		public ProductReviewPage(PageImpl<ProductReview> page, User user, MongoTemplate mongoTemplate) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			this.mongoTemplate = mongoTemplate;
			for (ProductReview productReview : page.getContent()) {
				UserProfile reviewUser = new UserProfile();
				ProductRating userRating = new ProductRating();
				float rating = 0;
				if (productReview.getUserId() != null) {
					reviewUser = getUserProfile(productReview.getUserId());
					userRating = getUserRating(productReview.getUserId(), productReview.getProductId());
					if (userRating != null) {
						rating = userRating.getRating();
					}
					reviewUser.setAggrRatingPercentage(getAllUserReviews(productReview.getUserId()));
				}
				this.content.add(new ProductReviewEntity(productReview, user, reviewUser, rating));
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

		public List<ProductReviewEntity> getContent() {
			return content;
		}

		public void setContent(List<ProductReviewEntity> content) {
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

		public ProductRating getUserRating(String userId, String productId) {
			ProductRating userRating = null;

			Query q = new Query();
			q.addCriteria(Criteria.where("userId").is(userId));
			q.addCriteria(Criteria.where("productId").is(productId));
			userRating = this.mongoTemplate.findOne(q, ProductRating.class);
			// userProfile = userProfileRepository.findByUserId(userId);
			return userRating;
		}

		public float getAllUserReviews(String userId) {
			List<ServiceReview> userReviews = null;
			List<ProductReview> userProdReviews = null;
			Query q = new Query();
			q.addCriteria(Criteria.where("userId").is(userId));
			userReviews = this.mongoTemplate.find(q, ServiceReview.class);
			userProdReviews = this.mongoTemplate.find(q, ProductReview.class);
			if (userReviews != null || userProdReviews != null) {
				return userReviews.size() + userProdReviews.size();
			} else {
				return 0;
			}
		}

	}

	public static class ProductReviewEntity {
		private String id;
		private String productId;
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

		public ProductReviewEntity(ProductReview productReview, User user, UserProfile reviewUser, float userRating) {
			this.setId(productReview.getId());
			this.setProductId(productReview.getProductId());
			this.setUserId(productReview.getUserId());
			this.setRating(userRating);
			this.setReview(productReview.getReview());
			this.setLikeCount(productReview.getLikeCount());
			this.setUnLikeCount(productReview.getUnLikeCount());
			this.setTitle(productReview.getTitle());
			this.setUserName(reviewUser.getBasicProfileInfo().getFirstName());
			this.setParentReviewId(productReview.getParentReviewId());
			this.setContributors(reviewUser.getAggrRatingPercentage());
			this.setCreatedAt(productReview.getCreatedAt());
			this.setLastModifiedAt(productReview.getLastModifiedAt());
			this.setUserImage(reviewUser.getBasicProfileInfo().getProfileImage());

		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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

		public float getContributors() {
			return contributors;
		}

		public void setContributors(float contributors) {
			this.contributors = contributors;
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

		public Map<String, String> getUserImage() {
			return userImage;
		}

		public void setUserImage(Map<String, String> userImage) {
			this.userImage = userImage;
		}

	}

	public void add(List<ProductReview> productReviewArray) {
		for (ProductReview productReview : productReviewArray) {
			this.productReviewArray.add(new ProductReviewEntity(productReview, null, null, 0));
		}
	}

	public void add(ProductReview productReview) {
		this.productReviewArray.add(new ProductReviewEntity(productReview, null, null, 0));
	}

	public void add(List<ProductReview> productReviewArray, User user) {
		for (ProductReview productReview : productReviewArray) {
			this.productReviewArray.add(new ProductReviewEntity(productReview, user, null, 0));
		}
	}

	public void add(ProductReview productReview, User user) {
		this.productReviewArray.add(new ProductReviewEntity(productReview, user, null, 0));
	}

	public static ProductReviewPage getPage(PageImpl<ProductReview> page, User user,MongoTemplate mongoTemplate) {
		ProductReviewPage res = new ProductReviewPage(page, user, mongoTemplate);
		return res;
	}

	public ProductReviewEntity getProductReviewEntity(ProductReview productReview, User user) {
		return new ProductReviewEntity(productReview, user, null, 0);
	}

}
