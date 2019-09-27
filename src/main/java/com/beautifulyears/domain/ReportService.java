package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "report_services")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportService {

    @Id
    private String id;
    private String serviceId;
    private String userId;
    private String cause;
    private String comment;
    private final Date createdAt = new Date();
    private Date lastModifiedAt = new Date();


    public ReportService(String serviceId,String userId,String cause,String comment) {
        this.serviceId = serviceId;
        this.userId = userId;
        this.cause = cause;
        this.comment = comment;
        this.lastModifiedAt = new Date();
    }

    public ReportService() {
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

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

}
