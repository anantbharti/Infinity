package com.example.infinity.models;

public class AboutUs {
    String text;
    String updateLink;
    String version;

    public AboutUs() {
    }

    public AboutUs(String text, String updateLink, String version) {
        this.text = text;
        this.updateLink = updateLink;
        this.version = version;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUpdateLink() {
        return updateLink;
    }

    public void setUpdateLink(String updateLink) {
        this.updateLink = updateLink;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
