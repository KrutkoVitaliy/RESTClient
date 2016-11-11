package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class VideoMakeupDTO {

    private long id;
    private String title;
    private String preview;
    private String source;
    private String availableDate;
    private long likes;
    private List<String> tags = new ArrayList<>();

    private VideoMakeupDTO() {

    }

    public VideoMakeupDTO(long id, String title, String preview, String source, List<String> tags, long likes, String availableDate) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.source = source;
        this.tags = tags;
        this.likes = likes;
        this.availableDate = availableDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}