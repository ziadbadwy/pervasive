package com.example.newsapp.Models;

public class Comment {

    private String account;
    private String comment;

    public Comment(String account, String comment) {
        this.account = account;
        this.comment = comment;
    }
    public Comment() {
        this.account = "";
        this.comment = "";
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
