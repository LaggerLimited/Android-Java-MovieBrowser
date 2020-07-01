package com.laggerlimited.moviebrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * This class uses a list of Movie objects to populate an array that is then inflated using view
 * recycling. This class also handles the downloading of the images from a URL using an AsyncTask.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    /**
     * Create a new {@link MovieAdapter} object.
     *
     * @param context   is the current context (i.e. Activity) that the adapter is being created in
     * @param movie     is the list of {@link  Movie}s to be displayed
     */
    public MovieAdapter(Context context, List<Movie> movie) {
        super(context, 0, movie);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /** Check if an existing view is being reused, otherwise inflate the view */
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }
        /** Find the movie at the given position in the list of movie */
        Movie currentMovie = getItem(position);
        /** Find the movie_item icon view to display the image */
        ImageView movieIv = gridItemView.findViewById(R.id.icon);
        /** Get the movie image url if one exists */
        if(!currentMovie.getPosterPath().isEmpty()){
            /** Run an AsyncTask to download the image for the movie in the background */
            new DownloadImageTask(movieIv).execute(currentMovie.getPosterPath());
        }else{
            movieIv.setImageResource(R.drawable.no_image_available);
        }
        /** Set the title below each image in the movie_item */
        TextView movieTv = gridItemView.findViewById(R.id.movie_tv);
        movieTv.setText(currentMovie.getTitle());
        /** Return the grid item view that is now showing the appropriate data */
        return gridItemView;
    }

    /**
     * AsyncTask used to download each image associated with the {@link  Movie} object
     * This is needed because each image takes time to download so a place holder image is used
     * in place of the image until it is downloaded
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon;
        }
        protected void onPostExecute(Bitmap result) {
            /** Set the image view with the resulting image */
            bmImage.setImageBitmap(result);
        }
    }
}
