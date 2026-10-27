package com.fashionshop.backend.model;

/**
 * 返回给客户端的用户信息，不包含密码。
 */
public class User {

    private long id;
    private String username;

    public User() {
    }

    public User(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
