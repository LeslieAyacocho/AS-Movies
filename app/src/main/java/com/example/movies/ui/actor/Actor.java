package com.example.movies.ui.actor;


public class Actor {
    String fname, lname, note, id, imgpath;

    public Actor(String fname, String lname, String note, String id, String imgpath) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.note = note;
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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
