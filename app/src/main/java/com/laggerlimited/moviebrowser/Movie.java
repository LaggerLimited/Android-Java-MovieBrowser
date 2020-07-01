package com.laggerlimited.moviebrowser;

import java.io.Serializable;

/**
 * This is a movie object that represents one movie entry returned from a search done
 * on the Movie Database API
 */
public class Movie implements Serializable {
    /** Movie Title */
    private String title;
    /** Release Date */
    private String releaseDate;
    /** Movie Poster Path*/
    private String posterPath;
    /** Vote Average */
    private double voteAverage;
    /** Overview */
    private String overview;

    final static String MOVIE_IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    /** Used for serialization */
    public Movie(){

    }
    /**
     * Movie object constructor
     *
     * @param title the name of the movie
     * @param releaseDate the date the movie was first shown
     * @param posterPath the URL location for the movie poster
     * @param voteAverage the average rating given to the movie by users
     * @param overview a summary of the movie
     */
    public Movie(String title, String releaseDate, String posterPath, double voteAverage, String overview) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){ this.title = title; }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate){ this.releaseDate = releaseDate; }

    public String getPosterPath() {
        // Build the poster path by adding it to the URL
        String newPosterPath = MOVIE_IMAGE_URL + posterPath;
        return newPosterPath;
    }

    public void setPosterPath(String posterPath){ this.posterPath = posterPath; }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage){ this.voteAverage = voteAverage; }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview){ this.overview = overview; }
}
