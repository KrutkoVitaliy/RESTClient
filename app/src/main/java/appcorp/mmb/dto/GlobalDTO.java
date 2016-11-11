package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class GlobalDTO {

    private long id;
    private String dataType;
    private String authorName;
    private String authorPhoto;
    private String availableDate;

    private String hlength;
    private String htype;
    private String hfor;

    private String colors;
    private String eye_color;
    private String occasion;
    private String difficult;

    private String shape;
    private String design;

    private List<String> images = new ArrayList<>();
    private List<String> hashTags = new ArrayList<>();
    private long likes;

    private long videoId;
    private String videoTitle;
    private String videoPreview;
    private String videoSource;
    private String videoAvailableDate;
    private long videoLikes;
    private String videoTags;

    private GlobalDTO() {
    }

    public GlobalDTO(long id, String dataType, String authorName, String authorPhoto, String availableDate, String hlength, String htype, String hfor, String colors, String eye_color, String occasion, String difficult, String shape, String design, List<String> images, List<String> hashTags, long likes, long videoId, String videoTitle, String videoPreview, String videoSource, String videoTags, long videoLikes, String videoAvailableDate) {
        this.id = id;
        this.dataType = dataType;
        this.authorName = authorName;
        this.authorPhoto = authorPhoto;
        this.availableDate = availableDate;
        this.hlength = hlength;
        this.htype = htype;
        this.hfor = hfor;
        this.colors = colors;
        this.eye_color = eye_color;
        this.occasion = occasion;
        this.difficult = difficult;
        this.shape = shape;
        this.design = design;
        this.images = images;
        this.hashTags = hashTags;
        this.likes = likes;
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoPreview = videoPreview;
        this.videoSource = videoSource;
        this.videoTags = videoTags;
        this.videoLikes = videoLikes;
        this.videoAvailableDate = videoAvailableDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getHlength() {
        return hlength;
    }

    public void setHlength(String hlength) {
        this.hlength = hlength;
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

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoPreview() {
        return videoPreview;
    }

    public void setVideoPreview(String videoPreview) {
        this.videoPreview = videoPreview;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getVideoAvailableDate() {
        return videoAvailableDate;
    }

    public void setVideoAvailableDate(String videoAvailableDate) {
        this.videoAvailableDate = videoAvailableDate;
    }

    public long getVideoLikes() {
        return videoLikes;
    }

    public void setVideoLikes(long videoLikes) {
        this.videoLikes = videoLikes;
    }

    public String getVideoTags() {
        return videoTags;
    }

    public void setVideoTags(String videoTags) {
        this.videoTags = videoTags;
    }
}
