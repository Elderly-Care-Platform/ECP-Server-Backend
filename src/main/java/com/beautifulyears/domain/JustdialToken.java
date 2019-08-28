package com.beautifulyears.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "justdial_token")
public class JustdialToken {
    private String id;
    private String token;
    private Long expires;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }
}