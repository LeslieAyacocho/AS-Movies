package com.example.movies.ui.roles;

public class Role {

    String name, fname, lname, notes, imgpath;

    public Role(String name, String fname, String lname, String notes, String imgpath) {
        this.name = name;
        this.fname = fname;
        this.lname = lname;
        this.notes = notes;
        this.imgpath = imgpath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
