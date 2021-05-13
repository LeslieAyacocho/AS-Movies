package com.example.movies.ui.movie;

public class Movie {
    String title,release,description, genre, producer, id, imgpath;

    public Movie(String title, String release, String description, String genre, String producer, String id, String imgpath) {
        this.title = title;
        this.release = release;
        this.description = description;
        this.genre = genre;
        this.producer = producer;
        this.id = id;
        this.imgpath = imgpath;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", release='" + release + '\'' +
                ", description='" + description + '\'' +
//                ", genre_id='" + genre_id + '\'' +
//                ", producer_id='" + producer_id + '\'' +
                '}';
    }
}
