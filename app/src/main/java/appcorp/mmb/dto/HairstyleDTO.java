package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class HairstyleDTO {

    private long id;
    private String dataType;
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

    private long videoId;
    private String videoTitle;
    private String videoPreview;
    private String videoSource;
    private String videoAvailableDate;
    private long videoLikes;
    private String videoTags;

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

    public HairstyleDTO(long id, String dataType, String availableDate, String authorName, String authorPhoto, String hairstyleType, List<String> images, List<String> hashTags, long likes, String hlenght, String htype, String hfor, long videoId, String videoTitle, String videoPreview, String videoSource, String videoTags, long videoLikes, String videoAvailableDate) {
        this.id = id;
        this.dataType = dataType;
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