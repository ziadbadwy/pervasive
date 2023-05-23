package com.example.newsapp.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username, email, password;
    List<Post> likedPosts;

    public User()
    {
        this.username = "";
        this.email = "";
        this.password = "";
        likedPosts=new ArrayList<>();
    }
    public User(String username,String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        likedPosts=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
