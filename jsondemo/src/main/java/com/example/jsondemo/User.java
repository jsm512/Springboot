package com.example.jsondemo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private long user_id;

    private String username;
    private int post_count;

    public long getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public int getPost_count() {
        return post_count;
    }

    @JsonCreator
    public User(
            @JsonProperty("user_id") long user_id,
            @JsonProperty("username") String username,
            @JsonProperty("post_count") int post_count) {
        this.user_id = user_id;
        this.username = username;
        this.post_count = post_count;
    }
}
