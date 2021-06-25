package com.example.moviebrowser;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieModel {

    public static final String TABLE_NAME = "favourite_movies";

    public static final String COLUMN_FAVOURITE_MOVIE_ID = "favourite_movie_id";
    public static final String COLUMN_TMDB_ID = "tmdb_id";
    public static final String COLUMN_POSTER_PATH = "poster_path";

    private String title, poster_path, tmdb_id, imdb_id, synopsis, release_date, backdrop_path;
    private double score;
    private String genres;

    public MovieModel(String title, String poster_path, String tmdb_id, String imdb_id, String synopsis, String release_date, double score, String genres, String backdrop_path) {
        this.title = title;
        this.poster_path = poster_path;
        this.tmdb_id = tmdb_id;
        this.imdb_id = imdb_id;
        this.synopsis = synopsis;
        this.release_date = release_date;
        this.score = score;
        this.genres = genres;
        this.backdrop_path = backdrop_path;
    }

    public MovieModel(String poster_path, String tmdb_id) {
        this.poster_path = poster_path;
        this.tmdb_id = tmdb_id;
    }

    public MovieModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTmdb_id() {
        return tmdb_id;
    }

    public void setTmdb_id(String tmdb_id) {
        this.tmdb_id = tmdb_id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public static MovieModel parseJSONObject (JSONObject object) {
        MovieModel movie = new MovieModel();

        try {
            if (object.has("title")) {
                movie.setTitle(object.getString("title"));
            }
            if (object.has("poster_path")) {
                movie.setPoster_path(object.getString("poster_path"));
            }
            if (object.has("id")) {
                movie.setTmdb_id(String.valueOf(object.getInt("id")));
            }
            if (object.has("imdb_id")) {
                movie.setImdb_id(object.getString("imdb_id"));
            }
            if (object.has("overview")) {
                movie.setSynopsis(object.getString("overview"));
            }
            if (object.has("release_date")) {
                movie.setRelease_date(object.getString("release_date"));
            }
            if (object.has("vote_average")) {
                movie.setScore(object.getDouble("vote_average"));
            }
            if (object.has("genres")) {
                JSONArray jsonArray = object.getJSONArray("genres");
                String genres = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    genres += jsonObject.getString("name") + ", ";
                }
                genres = genres.replaceFirst(",\\s$","");
                movie.setGenres(genres);
            }
            if (object.has("backdrop_path")) {
//                post.setTitle(object.getJSONObject("title").getString("rendered"));
                movie.setBackdrop_path(object.getString("backdrop_path"));
            }
        }
        catch (Exception e) {
            //catch
        }
        return movie;
    }

}
