package com.beautifulyears.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "justdail_settings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JustDailSetting {
    private String id;
    private Integer limit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public JustDailSetting(String id, Integer limit) {
        this.id = id;
        this.limit = limit;
    }
}