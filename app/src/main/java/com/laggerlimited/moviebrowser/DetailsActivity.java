package com.laggerlimited.moviebrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Details Activity is launched with Movie data populated in the UI.
 * If an error occurs the activity is closed and a toast informs the user.
 *
 */
public class DetailsActivity extends AppCompatActivity {
    /** String that represents the serialized movie object passed to this activity */
    private static final String MOVIE_OBJECT = "Movie_Object";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /** Cast each view into a variable for UI population */
        ImageView posterIv = findViewById(R.id.poster_iv);
        TextView voteTv = findViewById(R.id.vote_tv);
        TextView dateTv = findViewById(R.id.date_tv);
        TextView overviewTv = findViewById(R.id.overview_tv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        /** Get the selected movie object */
        Movie movieObject = (Movie) intent.getSerializableExtra(MOVIE_OBJECT);
        /**
         * If a valid movie object was passed to the activity then display its
         * information in the UI. An invalid movie object will close the activity
         */
        if (movieObject != null){
            String title = movieObject.getTitle();
            String releaseDate = movieObject.getReleaseDate();
            String posterPath = movieObject.getPosterPath();
            double voteAverage = movieObject.getVoteAverage();
            String overview = movieObject.getOverview();
            /** Set the title of the activity to the title of the movie selected */
            setTitle(title);
            /**
             * If a poster exists for the movie selected then display it in the UI
             * using the picasso library
             */
            Picasso.get().load(posterPath)
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available)
                    .into(posterIv);

            voteTv.setText(String.valueOf(voteAverage));
            dateTv.setText(releaseDate);
            overviewTv.setText(overview);
        }
        else{
            closeOnError();
        }
    }

    /** This method is called when an error occurs. It closes the activity and informs the user */
    private void closeOnError() {
        finish();
        Toast.makeText(this, "No Data Available", Toast.LENGTH_SHORT).show();
    }
}