package com.example.android.findyourbook;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikki on 15/07/2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed directly from the class
     * name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){
    }


    public static List<Book> fetchBookData(String BOOKS_REQUEST_URL){

        URL url = createUrl(BOOKS_REQUEST_URL);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //If the request was successful (response code 200),
            //then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                //Closing the input stream could throw an IOException, which is why
                //the makeHttpRequest method specifies that an IOException can be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;

    }
    /** Convert the @link InputStream into a String which contains the JSON response from the server.
     *
     */

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**Return a list of @link Book objects that has been built up by parsing the JSON response
     *
     */
    private static List<Book> extractFeatureFromJson(String bookJSON){
        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)){
            return null;
        }
        //Create an empty ArrayList that we can start adding books to.
        List<Book> books = new ArrayList<>();
        //Try to parse the JSON response String. If there's a problem with the way JSON is formatted
        // a JSONException will be thrown. Catch the exception so the app doesn't crash, and print
        // the error message in the logs.
        try{
            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            if (baseJsonResponse.has("items")) {
                //Extract the JSONArray associated with the key called "items", which represents a list of books.
                JSONArray bookArray = baseJsonResponse.getJSONArray("items");

                //For each book in the bookArray, create an @link Book object
                for (int i = 0; i < bookArray.length(); i++) {
                    //Get a single book at the position i within the list of books
                    JSONObject currentBook = bookArray.getJSONObject(i);
                    //For a given book, extract the JSONObject associated with the key called "volumeInfo"
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                    //Extract the value for the key called "title"
                    String title = volumeInfo.getString("title");

                    String subtitle = null;
                    if (volumeInfo.has("subtitle")) {
                        //Extract the value for the key called "subtitle"
                        subtitle = volumeInfo.getString("subtitle");
                    }

                    //We need a String  with all the authors divided with comma
                    StringBuilder authors = new StringBuilder();
                    if (volumeInfo.has("authors")) {
                        //Extract tje value for the key called "authors"
                        JSONArray authorArray = volumeInfo.getJSONArray("authors");


                        //For each author in the authorArray, append its value to authors StringBuilder
                        for (int j = 0; j < authorArray.length(); j++) {
                            authors.append(authorArray.getString(j)).append(", ");
                        }
                        //Remove the comma from the end of the string
                        authors.setLength(authors.length() - 2);
                    }
                    String url = null;
                    if (volumeInfo.has("infoLink")) {
                        //Extract the value for the key called "infoLink"
                        url = volumeInfo.getString("infoLink");
                    }
                    /**Create a new @Link Book object with the title, subtitle, the String with the authors and the url
                     * from the JSON parsing.
                     */

                    Book book = new Book(title, authors.toString(), subtitle, url);
                    //Add the new @link Book to te list of books
                    books.add(book);

                }
            }
        }
     catch (JSONException e){
        //If an error is thrown when executing any of the above statements in the "try" block
        //catch the exception here, so the app doesn't crash. Print a log message
        Log.e("QueryUtils", "Problem parsing the JSON results", e);
    }

    return books;
    }


}
