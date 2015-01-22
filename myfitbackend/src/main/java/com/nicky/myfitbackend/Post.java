package com.nicky.myfitbackend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Comparator;

/**
 * Created by nicholas on 11/13/14.
 */
@Entity
public class Post {

    @Id
    Long id;

    @Index
    String userEmail;

    String servingUrl;

    String blobKey;

    String jsonClothingItems;

    @Index
    Long timeInMillis;

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getServingUrl() {
        return servingUrl;
    }

    public void setServingUrl(String servingUrl) {
        this.servingUrl = servingUrl;
    }

    public String getBlobKey() {
        return blobKey;
    }

    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }

    public Long getPostTime() {
        return timeInMillis;
    }

    public void setPostTime(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getJsonClothingItems() {
        return jsonClothingItems;
    }

    public void setJsonClothingItems(String jsonClothingItems) {
        this.jsonClothingItems = jsonClothingItems;
    }

    public static Comparator<Post> descendingTime = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o2.getPostTime().compareTo(o1.getPostTime());
        }
    };
}
