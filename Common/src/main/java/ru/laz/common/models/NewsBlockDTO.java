package ru.laz.common.models;

import java.io.Serializable;

public class NewsBlockDTO implements Serializable {
    private String date = "";
    private String title = "";
    private String url = "";
    private String body = "";

    public NewsBlockDTO()  {

    }

    public NewsBlockEntity getEntity() {
        return new NewsBlockEntity();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
