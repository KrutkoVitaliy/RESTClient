package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class StylistDTO {

    private String id;
    private String sid;
    private String authorName;
    private String authorPhoto;
    private String authorLocation;
    private String likes;
    private String followers;

    public StylistDTO() {

    }

    public StylistDTO(String authorName) {
        this.authorName = authorName;
    }

    public StylistDTO(String id, String sid, String likes, String followers, String authorName, String authorPhoto, String authorLocation) {
        this.id = id;
        this.sid = sid;
        this.likes = likes;
        this.followers = followers;
        this.authorName = authorName;
        this.authorPhoto = authorPhoto;
        this.authorLocation = authorLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public String getAuthorLocation() {
        return authorLocation;
    }

    public void setAuthorLocation(String authorLocation) {
        this.authorLocation = authorLocation;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }
}