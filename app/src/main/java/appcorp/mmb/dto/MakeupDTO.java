package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class MakeupDTO {

    private long id;
    private String authorName;
    private String authorPhoto;
    private String availableDate;
    private String colors;
    private String shape;
    private String design;
    private long likes;
    private List<String> images = new ArrayList<>();
    private List<String> hashTags = new ArrayList<>();

    private MakeupDTO() {

    }

    public MakeupDTO(long id, String availableDate, String authorName, String authorPhoto, String shape, String design, List<String> images, String colors, List<String> hashTags, long likes) {
        this.id = id;
        this.availableDate = availableDate;
        this.images = images;
        this.colors = colors;
        this.hashTags = hashTags;
        this.likes = likes;
        this.authorPhoto = authorPhoto;
        this.authorName = authorName;
        this.shape = shape;
        this.design = design;
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

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
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

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }
}