package com.beautifulyears.domain;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "service_ratings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRatings {
    @Id
    private String id;
    
    private String serviceId;
    private String userId;
    private Float rating;
    private final Date createdAt = new Date();
    private Date lastModifiedAt = new Date();
    
    public ServiceRatings() {

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Date lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public ServiceRatings(String serviceId, String userId, Float rating) {
        this.serviceId = serviceId;
        this.userId = userId;
        this.rating = rating;
        this.lastModifiedAt = new Date();
    }

}