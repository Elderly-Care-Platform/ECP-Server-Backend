package com.beautifulyears.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "report_events")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportEvent {

    @Id
    private String id;
    private String eventId;
    private String userId;
    private String comment;
    private final Date createdAt = new Date();
    private Date lastModifiedAt = new Date();


    public ReportEvent(String eventId,String userId, String comment) {
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.lastModifiedAt = new Date();
    }

    public ReportEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
