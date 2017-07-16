package com.example.android.findyourbook;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Ikki on 16/07/2017.
 */

public class BookListingLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookListingLoader.class.getName();

    /*Query URL */
    private String mUrl;

    /**
     *
     * @param context of the activity
     * @param url to load the data from
     */
    public BookListingLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground(){
        if (mUrl == null){
            return null;
        }

        //Perform the network request, parse the response, and extract the book list.
        List<Book> bookList = QueryUtils.fetchBookData(mUrl);
        return bookList;
    }


}