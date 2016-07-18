package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchDTO {

    private long id;
    private long sid;
    private String title;
    private String author;
    private String availableDate;
    private String colors;
    private String eye_color;
    private String occasion;
    private String difficult;
    private String authorPhoto;
    private long likes;
    private List<String> images = new ArrayList<>();
    private List<String> hashTags = new ArrayList<>();

    public SearchDTO() {

    }

    public SearchDTO(String title) {
        this.title = title;
    }

    public SearchDTO(long id, long sid, String title, String availableDate, String author, String authorPhoto, List<String> images, String colors, String eye_color, String occasion, String difficult, List<String> hashTags, long likes) {
        this.id = id;
        this.sid = sid;
        this.title = title;
        this.availableDate = availableDate;
        this.author = author;
        this.images = images;
        this.colors = colors;
        this.eye_color = eye_color;
        this.occasion = occasion;
        this.difficult = difficult;
        this.hashTags = hashTags;
        this.likes = likes;
        this.authorPhoto = authorPhoto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getEye_color() {
        return eye_color;
    }

    public void setEye_color(String eye_color) {
        this.eye_color = eye_color;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }
}