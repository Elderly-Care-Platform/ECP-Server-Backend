package com.beautifulyears.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "justdial_services_logs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JustDialServicesLogs {
    private String id;
    private Date executionStart;
    private Date executionEnd;
    private ServicesStatus status;
    private int recordsAdded;
    private int recordsUpdated;
    private String error;
    private Date createdAt = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExecutionStart() {
        return executionStart;
    }

    public void setExecutionStart(Date executionStart) {
        this.executionStart = executionStart;
    }

    public Date getExecutionEnd() {
        return executionEnd;
    }

    public void setExecutionEnd(Date executionEnd) {
        this.executionEnd = executionEnd;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getRecordsAdded() {
        return recordsAdded;
    }

    public void setRecordsAdded(int recordsAdded) {
        this.recordsAdded = recordsAdded;
    }

    public int getRecordsUpdated() {
        return recordsUpdated;
    }

    public void setRecordsUpdated(int recordsUpdated) {
        this.recordsUpdated = recordsUpdated;
    }

    public ServicesStatus getStatus() {
        return status;
    }

    public void setStatus(ServicesStatus status) {
        this.status = status;
    }
}