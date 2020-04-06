package com.beautifulyears.domain;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "justdial_services")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JustDailServices {

    private String id;
    private HashMap<String, Object> serviceInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Object> getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(HashMap<String, Object> serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public JustDailServices() {
    }

}