package com.beautifulyears.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "service_review")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceReview {

    @Id
    private String id;

    private String serviceId;
    private String userId;
    private String review;
    private List<String> likeCount;
    private List<String> unLikeCount;
    private boolean isDeleted;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ServiceReview(String serviceId, String review, List<String> likeCount, List<String> unLikeCount,
            String userId, String parentReviewId, String title, boolean isDeleted) {
        this.serviceId = serviceId;
        this.userId = userId;
        this.review = review;
        this.likeCount = likeCount;
        this.unLikeCount = unLikeCount;
        this.title = title;
        this.isDeleted = isDeleted;
        this.parentReviewId = parentReviewId;
        this.lastModifiedAt = new Date();
    }

    public ServiceReview() {
    }

}
