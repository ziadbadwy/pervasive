package com.example.newsapp.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Post {

    private int id;
    private int likes;
    private String title, description, imageUrl;
    private float temperature;
    private String location;
    private Map<String,String> Comments;

    public Post(String title, String description, String imageUrl, float temperature, String location) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.temperature = temperature;
        this.location = location;
        Comments = new HashMap<String,String>();
    }
    public Post(){
        this.title = "";
        this.description = "";
        this.imageUrl = "";
        this.temperature =0.0f;
        this.location = "";
        Comments = new HashMap<String,String>();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, String> getComments() {
        return Comments;
    }

    public void addComment(String email,String comment){
        Comments.put(email,comment);
    }

}
