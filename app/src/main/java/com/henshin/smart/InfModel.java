package com.henshin.smart;

import android.graphics.Bitmap;

public class InfModel {
    private int id;
    private String title;
    private String name;
    private String contect;
    private String picPath;
    private Bitmap picreal;

    public Bitmap getPicreal() {
        return this.picreal;
    }

    public void setPicreal(Bitmap picreal) {
        this.picreal = picreal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContect(String contect) {
        this.contect = contect;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getName() {
        return this.name;
    }

    public String getContect() {
        return this.contect;
    }

    public String getPicPath() {
        return picPath;
    }
}
