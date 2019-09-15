package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.User;

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

		public ProductReviewPage() {
			super();
		}

		public ProductReviewPage(PageImpl<ProductReview> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (ProductReview productReview : page.getContent()) {
				this.content.add(new ProductReviewEntity(productReview, user));
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

	}

	public static class ProductReviewEntity {
		private String 	id;
		private String 	productId;
		private String 	rating;
		private String 	review;
		private Boolean likeCount;
		private Integer unLikeCount;
		private int 	status;
		private String 	userName;
		private String  parentReviewId;
		private Date createdAt;
		private Date lastModifiedAt;

		public ProductReviewEntity(ProductReview productReview, User user) {
			this.setProductId(productReview.getProductId());
			this.setRating(productReview.getRating());
			this.setReview(productReview.getReview());
			this.setLikeCount(productReview.getLikeCount());
			this.setUnLikeCount(productReview.getUnLikeCount());
			this.setStatus(productReview.getStatus());
			this.setUserName(productReview.getUserName());
			this.setParentReviewId(productReview.getParentReviewId());
			this.setCreatedAt(productReview.getCreatedAt());
			this.setLastModifiedAt(productReview.getLastModifiedAt());
			
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

		public String getRating() {
			return rating;
		}

		public void setRating(String rating) {
			this.rating = rating;
		}

		public String getReview() {
			return review;
		}

		public void setReview(String review) {
			this.review = review;
		}

		public Boolean getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(Boolean likeCount) {
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
	}

	public void add(List<ProductReview> productReviewArray) {
		for (ProductReview productReview : productReviewArray) {
			this.productReviewArray.add(new ProductReviewEntity(productReview, null));
		}
	}

	public void add(ProductReview productReview) {
		this.productReviewArray.add(new ProductReviewEntity(productReview, null));
	}

	public void add(List<ProductReview> productReviewArray, User user) {
		for (ProductReview productReview : productReviewArray) {
			this.productReviewArray.add(new ProductReviewEntity(productReview, user));
		}
	}

	public void add(ProductReview productReview, User user) {
		this.productReviewArray.add(new ProductReviewEntity(productReview, user));
	}

	public static ProductReviewPage getPage(PageImpl<ProductReview> page, User user) {
		ProductReviewPage res = new ProductReviewPage(page, user);
		return res;
	}

	public ProductReviewEntity getProductReviewEntity(ProductReview productReview, User user) {
		return new ProductReviewEntity(productReview, user);
	}

}
