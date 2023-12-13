package com.example.myapp.dto;

import java.io.Serializable;
import java.util.Date;

public class HaniItem implements Serializable {
    long id;
    String title;
    String link;
    Date pubDate;
    String category;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "HaniItem{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", pubDate=" + pubDate +
                ", category=" + category +
                '}';
    }
}
