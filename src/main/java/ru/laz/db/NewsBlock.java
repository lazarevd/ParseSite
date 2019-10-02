package ru.laz.db;


import javax.persistence.*;

@Entity
@Table(name = "news_blocks")
public class NewsBlock {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
    @Column(name = "date")
        private String date = "";//for unique constaint in sqlite
    @Column(name = "title")
        private String title = "";
    @Column(name = "url")
        private String url = "";
    @Column(name = "body")
        private String body = "";


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NewsBlock)) {
            return false;
        }
        NewsBlock nb = (NewsBlock) o;
        return this.title.equals(nb.getTitle()) && this.body.equals(nb.getBody());
    }


    @Override
    public String toString() {
        return "date: " + date + " title: "+title + " url: " + url + " text: " + body;
    }
}

