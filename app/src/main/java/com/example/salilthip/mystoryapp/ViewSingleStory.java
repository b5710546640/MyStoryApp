package com.example.salilthip.mystoryapp;

/**
 * Created by Salilthip on 5/18/2017.
 */

public class ViewSingleStory {

    protected String title,detail;
    protected String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ViewSingleStory(String title, String detail){
        this.title=title;
        this.detail=detail;
    }

    public ViewSingleStory(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
