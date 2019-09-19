package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "service_review")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceReview {

    @Id
    private String id;
    private String serviceId;
    private Float rating;
    private String review;
    private Integer likeCount;
    private Integer unLikeCount;
    private int status;
    private String userName;
    private String parentReviewId;
    private final Date createdAt = new Date();
    private Date lastModifiedAt = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ServiceReview(String serviceId, Float rating, String review, Integer likeCount, Integer unLikeCount,
            int status, String userName, String parentReviewId) {
        this.serviceId = serviceId;
        this.rating = rating;
        this.review = review;
        this.likeCount = likeCount;
        this.unLikeCount = unLikeCount;
        this.status = status;
        this.userName = userName;
        this.parentReviewId = parentReviewId;
        this.lastModifiedAt = new Date();
    }

    public ServiceReview() {
    }

}
