package com.beautifulyears.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "productreview")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReview {

	@Id
	private String 	id;
	private String 	productId;
	private String userId;
    private String review;
    private List<String> likeCount;
    private List<String> unLikeCount;
    private String title;
    private String parentReviewId;
    private final Date createdAt = new Date();
    private Date lastModifiedAt = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public ProductReview(String productId, String userId, String review, List<String> likeCount,
			List<String> unLikeCount, String title, String parentReviewId) {
		this.productId = productId;
		this.userId = userId;
		this.review = review;
		this.likeCount = likeCount;
		this.unLikeCount = unLikeCount;
		this.title = title;
		this.parentReviewId = parentReviewId;
	}

	public ProductReview(){
		
	}
}
