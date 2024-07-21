package com.example.jsondemo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private int id;
    private String username;
    private String tag;
    private long win;
    private long lose;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return tag;
    }

    public long getWin() {
        return win;
    }

    public long getLose() {
        return lose;
    }

    @JsonCreator
    public User(
            @JsonProperty("id") int id,
            @JsonProperty("username") String username,
            @JsonProperty("tag") String tag,
            @JsonProperty("win") long win,
            @JsonProperty("lose") long lose) {
        this.id = id;
        this.username = username;
        this.tag = tag;
        this.win = win;
        this.lose = lose;
    }
}
