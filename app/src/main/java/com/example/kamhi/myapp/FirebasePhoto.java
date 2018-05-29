package com.example.kamhi.myapp;

/**
 * Created by Kamhi on 17/1/2018.
 */

public class FirebasePhoto {
//photo class, shows details of photo
    String title;
    String description;
    String image;
    String username;
    String date;
    String uid;
    String parent;

    public FirebasePhoto() {
    }

    public FirebasePhoto(String title, String description, String image, String username, String date, String uid, String parent) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.username = username;
        this.date = date;
        this.uid = uid;
        this.parent = parent;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
