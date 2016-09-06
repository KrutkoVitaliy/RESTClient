package appcorp.mmb.dto;

import java.util.ArrayList;
import java.util.List;

public class StylistDTO {

    private long id;
    private String authorName;
    private String authorLastname;
    private String authorPhoto;
    private String authorPhoneNumber;
    private String authorCity;
    private String authorAddress;
    private String gplus;
    private String fb;
    private String vk;
    private String instagram;
    private String ok;

    private StylistDTO() {

    }

    public StylistDTO(long id, String authorName, String authorLastname, String authorPhoto, String authorPhoneNumber, String authorCity, String authorAddress, String gplus, String fb, String vk, String instagram, String ok) {
        this.id = id;
        this.authorName = authorName;
        this.authorLastname = authorLastname;
        this.authorPhoto = authorPhoto;
        this.authorPhoneNumber = authorPhoneNumber;
        this.authorCity = authorCity;
        this.authorAddress = authorAddress;
        this.gplus = gplus;
        this.fb = fb;
        this.vk = vk;
        this.instagram = instagram;
        this.ok = ok;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorLastname() {
        return authorLastname;
    }

    public void setAuthorLastname(String authorLastname) {
        this.authorLastname = authorLastname;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public void setAuthorPhoneNumber(String authorPhoneNumber) {
        this.authorPhoneNumber = authorPhoneNumber;
    }

    public String getAuthorCity() {
        return authorCity;
    }

    public void setAuthorCity(String authorCity) {
        this.authorCity = authorCity;
    }

    public String getAuthorAddress() {
        return authorAddress;
    }

    public void setAuthorAddress(String authorAddress) {
        this.authorAddress = authorAddress;
    }

    public String getGplus() {
        return gplus;
    }

    public void setGplus(String gplus) {
        this.gplus = gplus;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }
}