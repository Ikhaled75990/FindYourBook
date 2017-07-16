package com.example.android.findyourbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.view.View.GONE;


public class BookListingActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    /**
     * View that is displayed when the list is empty
     */
    private TextView mTitleEmptyStateView;

    private static String mUrl = "";

    /**
     * Constant value for the BookListing loader ID.
     */
    private static final int BOOKLISTING_LOADER_ID = 1;

    private static final String LOG_TAG = BookListingActivity.class.getName();

    /**URL for google books from the Google API
     */
    private static final String BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=" + mUrl + "&maxResults=10";



    /**
     *Adapter for the list of books
     */
    private BookListingAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        //Find the list view in the layout
        final ListView bookListView = (ListView) findViewById(R.id.list);
        //Create a new adapter that takes an empty list of books as input
        mAdapter = new BookListingAdapter(this, new ArrayList<Book>());
        //Set the adapter on the (@link ListView)
        // so the list can be populated in the UI
        bookListView.setAdapter(mAdapter);

        final EditText searchEditTextView = (EditText) findViewById(R.id.search_bar);

        //Set onclick listener on the search button
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mAdapter.clear();
                String userQuery = searchEditTextView.getText().toString();
                mUrl = userQuery;
                getLoaderManager().restartLoader(1, null, BookListingActivity.this);

            }
        });

        //Set an onItemClickListener on the ListView which will open a web browser and direct
        //to the website containing more information about the book via an Intent.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                //Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);
                //Convert the String URL into URI object
                Uri bookUri = Uri.parse(currentBook.getmUrl());
                //Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                //Send the intent to launch a new activity
                startActivity(websiteIntent);
            }

        });

        //Get a reference to the ConnectivityManager to check the network
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //Get details on the currently active default data network.
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()){
            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOKLISTING_LOADER_ID, null, this);
        } else {
            mTitleEmptyStateView.setVisibility(View.GONE);

            //Update empty state with no internet connection error message
            mTitleEmptyStateView.setText("No internet connection.");
        }

    }
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle){
        //Create a new loader for the given URL
        return new BookListingLoader(this, BOOKS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books){
        //Hide the empty view because the data has been loaded
        View emptyView = findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);

        //Clear the adapter of previous book list data.
        mAdapter.clear();

        //If there is a valid list of {@link BookListing) then add them
        // to the adapter's data set. This will trigger the ListView update
        if (books != null && !books.isEmpty()){
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader){
        //Reset the loader, so that we can clear out existing data.
        mAdapter.clear();
    }

}
