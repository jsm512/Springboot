package com.example.jsondemo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "tag", "username" })
public class UserDTO {
    private String username;
    private String tag;

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return tag;
    }

    public UserDTO(String username, String tag) {
        this.username = username;
        this.tag = tag;
    }
}
