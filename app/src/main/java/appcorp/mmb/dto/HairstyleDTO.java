package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class HairstyleDTO {

    private long id;
    private String authorName;
    private String authorPhoto;
    private String availableDate;
    private String hairstyleType;
    private String hlenght;
    private String htype;
    private String hfor;

    private long likes;
    private List<String> images = new ArrayList<>();
    private List<String> hashTags = new ArrayList<>();

    private HairstyleDTO() {

    }

    public HairstyleDTO(long id, String availableDate, String authorName, String authorPhoto, String hairstyleType, List<String> images, List<String> hashTags, long likes, String hlenght, String htype, String hfor) {
        this.id = id;
        this.availableDate = availableDate;
        this.images = images;
        this.hashTags = hashTags;
        this.likes = likes;
        this.authorPhoto = authorPhoto;
        this.authorName = authorName;
        this.hairstyleType = hairstyleType;
        this.hlenght = hlenght;
        this.htype = htype;
        this.hfor = hfor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> imageURL) {
        this.images = imageURL;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getHairstyleType() {
        return hairstyleType;
    }

    public void setHairstyleType(String hairstyleType) {
        this.hairstyleType = hairstyleType;
    }

    public String getHlenght() {
        return hlenght;
    }

    public void setHlenght(String hlenght) {
        this.hlenght = hlenght;
    }

    public String getHtype() {
        return htype;
    }

    public void setHtype(String htype) {
        this.htype = htype;
    }

    public String getHfor() {
        return hfor;
    }

    public void setHfor(String hfor) {
        this.hfor = hfor;
    }
}