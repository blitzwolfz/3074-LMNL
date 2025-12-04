package com.example.lmnl.post;

public class Post {
    private long id;
    private String username;
    private String content;
    private String createdAt;

    public Post(long id, String username, String content, String createdAt) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}