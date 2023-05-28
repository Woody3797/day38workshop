package ibf2022.csf.day38workshop.server.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Post {
    
    String comments;
    Integer likes;
    Integer dislikes;
    
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public Integer getLikes() {
        return likes;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    public Integer getDislikes() {
        return dislikes;
    }
    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Post() {
    }

    public Post(String comments, Integer likes, Integer dislikes, String image64) {
        this.comments = comments;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
        .add("comments", comments)
        .add("likes", likes)
        .add("dislikes", dislikes)
        .build();
    }
    
    
}
