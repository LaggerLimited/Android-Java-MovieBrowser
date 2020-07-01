package com.laggerlimited.moviebrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity includes a grid view and a spinner that is populated with movies using the
 * Movie Database API when the application is started. The activity uses a grid view of movie
 * posters with the movie title beneath it in-case the poster is unavailable. The grid view
 * is updated with the appropriate movies when the user selects a different option with the spinner
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /** String that contains the API key */
    private String keyAPI = "";

    /** Adapter for the list of movies */
    private MovieAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** View that is displayed when the list is loading */
    View loadingIndicator;

    /** Object that contains the network information of the device */
    NetworkInfo networkInfo;

    /** URL for movie data from the Movie Database data set */
    private String MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=" +
                    keyAPI +
                    "&language=en-US&page=1";

    /** Integer represents spinner selected position */
    int iCurrentSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        /** Get the String that contains the API key */
        keyAPI = getString(R.string.API_key);
        /** Find a reference to the empty text view */
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        /** Find a reference to the loading indicator view */
        loadingIndicator = findViewById(R.id.loading_indicator);
        /** Find a reference to the {@link GridView} in the layout */
        GridView movieGridView = (GridView) findViewById(R.id.movie_list);
        /** Create a new adapter for the list of movies */
        mAdapter = new MovieAdapter(this, new ArrayList<Movie>());
        /**
         * Set the adapter on the {@link GridView so the grid can be populated
         * in the user interface
         */
        movieGridView.setAdapter(mAdapter);
        /** Click Listener for when the user clicks on a Movie object */
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /** Find the current movie that was clicked on */
                Movie currentMovie = mAdapter.getItem(position);
                /** Launch the details activity
                 * @param currentMovie the movie object selected from the grid view
                 */
                launchDetailActivity(currentMovie);
            }
        });

        /** Clear data from the grid view adapter */
        mAdapter.clear();
        /** Get a reference to the ConnectivityManager to check state of network connectivity */
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        /** Get details on the currently active default data network */
        networkInfo = connMgr.getActiveNetworkInfo();
        /** Remove the text from the empty_view */
        mEmptyStateTextView.setText("");
        /** Make the loading indicator visible */
        loadingIndicator.setVisibility(View.VISIBLE);
        /** If there is a network connection then fetch the data */
        if (networkInfo != null && networkInfo.isConnected()) {
            /** Set the API key to the URL */
            MOVIE_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" +
                    keyAPI +
                    "&language=en-US&page=1";
            /** Get the movie data from the Movie Database using Async Task */
            MovieAsyncTask task = new MovieAsyncTask();
            task.execute(MOVIE_REQUEST_URL);
        }else{
            /** Remove the loading indicator and notify the user of the error */
            loadingIndicator.setVisibility(View.GONE);
            /** Update empty state with no connection error message */
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        /** Setup the spinner */
        Spinner spinner = findViewById(R.id.choice_spinner);
        iCurrentSelection = spinner.getSelectedItemPosition();
        /** Setup the spinner listener */
        spinner.setOnItemSelectedListener(this);
        /** Create an ArrayAdapter using the string array and a default spinner layout */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        /** Specify the layout to use when the list of choices appears */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /** Apply the adapter to the spinner */
        spinner.setAdapter(adapter);
    }

    /** This method handles starting the details activity and attaching the extras */
    private void launchDetailActivity(Movie movie) {
        /** Use explicit intent to start the Details Activity with the current Movie object
         * passed as a serializable extra */
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("Movie_Object", movie);
        startActivity(intent);
    }

    /** This method handles the spinner item selection */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        /** Get the current position selected on the spinner */
        iCurrentSelection = adapterView.getSelectedItemPosition();
        /** Check which spinner choice the user selected */
        if (iCurrentSelection == 0){
            /** Assign the popular movie URL to be queried */
            MOVIE_REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" +
                    keyAPI +
                    "&language=en-US&page=1";
            /** Get the popular movie data using an async task */
            MovieAsyncTask task = new MovieAsyncTask();
            task.execute(MOVIE_REQUEST_URL);
        }else{
            /** Assign the top rated movie URL to be queried */
            MOVIE_REQUEST_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" +
                    keyAPI +
                    "&language=en-US&page=1";
            /** Get the top rated movie data using an async task */
            MovieAsyncTask task = new MovieAsyncTask();
            task.execute(MOVIE_REQUEST_URL);
        }
    }

    /** Required method for item selection handler */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This method extends AsyncTask to get movie data from the Movie Database in a
     * background thread and returns a List of Movie objects
     */
    private class MovieAsyncTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... urls) {
            /** Don't perform the request if there are no URLs, or the first URL is null */
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Movie> result = QueryUtils.fetchMovieData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Movie> data) {
            /** Hide loading indicator because the data has been loaded */
            loadingIndicator.setVisibility(View.GONE);
            /** Clear the adapter of previous movie data */
            mAdapter.clear();
            /**
             * If there is a valid list of {@link Movie}s, then add them to the adapter's
             * data set. This will trigger the GridView to update
             */
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }else{
                /** There is not data so inform the user there are no results */
                mEmptyStateTextView.setText(R.string.no_results);
            }
        }
    }
}