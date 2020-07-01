package com.laggerlimited.moviebrowser;

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
 * Helper methods related to requesting and receiving movie data from the Movie Database API
 */
public class QueryUtils {
    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response
     */
    final static String MOVIE_RESULTS = "results"; // array (20items)
    final static String MOVIE_TITLE = "title"; // String
    final static String MOVIE_IMAGE = "poster_path"; // String
    final static String MOVIE_RELEASE = "release_date"; // String
    final static String MOVIE_RATING = "vote_average"; // double
    final static String MOVIE_OVERVIEW = "overview"; // String

    final static String LOG_TAG = "JSON Error: ";

    public static List<Movie> extractFeatureFromJson(String movieJSON) {
        /** If the JSON string is empty or null, then return early */
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        /** Create an empty ArrayList that we can start adding movies to */
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        /** Try to parse the json response */
        try {
            /** Build up a list of Movie objects with the corresponding data */
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray;
            /** Try to parse the baseJsonResponse and get the JSONArray with the key "results" */
            if (baseJsonResponse.has(MOVIE_RESULTS)) {
                /** Parse the results field */
                movieArray = baseJsonResponse.getJSONArray(MOVIE_RESULTS);
            } else {
                /** Unable to get the array with the key "results" return a null List<Movie> object */
                return null;
            }

            /** Loop through the movie array */
            for (int i = 0; i < movieArray.length(); i++){
                JSONObject movieJSONObject = movieArray.getJSONObject(i);
                /** Get the JSON string for the title */
                String title = movieJSONObject.getString(MOVIE_TITLE);
                /** Get the JSON string for the poster path */
                String posterPath = movieJSONObject.getString(MOVIE_IMAGE);
                /** Get the JSON string for the release date */
                String releaseDate = movieJSONObject.getString(MOVIE_RELEASE);
                /** Get the JSON double for the vote average */
                double voteAverage = movieJSONObject.getDouble(MOVIE_RATING);
                /** Get the JSON string for the overview */
                String overview = movieJSONObject.getString(MOVIE_OVERVIEW);

                /**
                 * Create a new Movie object and then add the object to the ArrayList
                 */
                Movie movie = new Movie(title, releaseDate, posterPath, voteAverage, overview);
                movieArrayList.add(movie);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        /** Return the list of movies */
        return movieArrayList;
    }

    /**
     * Query the Movie Database data set and return a list of {@link Movie} objects
     */
    public static List<Movie> fetchMovieData(String requestUrl) {
        /** Create URL object */
        URL url = createUrl(requestUrl);
        /** Perform HTTP request to the URL and receive a JSON response back */
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        /** Extract relevant fields from the JSON response and create a list of {@link Movie}s */
        List<Movie> movies = extractFeatureFromJson(jsonResponse);
        /** Return the list of {@link Movie}s */
        return movies;
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        /** If the URL is null, then return early */
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            /**
             * If the request was successful (response code 200)
             * Then read the input stream and parse the response
             */
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                /** Closing the input stream could throw an IOException */
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
