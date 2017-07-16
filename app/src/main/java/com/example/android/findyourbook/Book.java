package com.example.android.findyourbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Book {

    private String mTitle;
    private String mSubtitle;
    private String mAuthor;
    private String mUrl;


    public Book(String title, String subtitle, String author, String url) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthor = author;
        mUrl = url;
    }


    public String getmTitle() {
        return mTitle;
    }

    public String getmSubtitle(){
        return mSubtitle;
    }

    public String getmAuthor(){
        return mAuthor;
    }

    public String getmUrl(){
        return mUrl;
    }
}

