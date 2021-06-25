package com.example.moviebrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_FILE_NAME = "movie_database";

    public DataBase(@Nullable Context context) {
        super(context, DATABASE_FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                MovieModel.TABLE_NAME, MovieModel.COLUMN_FAVOURITE_MOVIE_ID, MovieModel.COLUMN_TMDB_ID, MovieModel.COLUMN_POSTER_PATH
        );
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS $s", MovieModel.TABLE_NAME);
        db.execSQL(query);
        onCreate(db);
    }

    // add a new movie
    public void addMovieToFavourites(String tmdb_id, String poster_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues(); // Bundle like

        cv.put(MovieModel.COLUMN_TMDB_ID, tmdb_id);
        cv.put(MovieModel.COLUMN_POSTER_PATH, poster_path);

        db.insert(MovieModel.TABLE_NAME, null, cv);
    }

    // delete a movie
    public int deleteMovieFromFavourites(String tmdb_id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(MovieModel.TABLE_NAME, MovieModel.COLUMN_TMDB_ID + "=?", new String[]{String.valueOf(tmdb_id)});
    }

    // get movie by tmdb_id
    public boolean getMovieById (String tmdb_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT * FROM db WHERE movie_id = ?
        String query = String.format("SELECT * FROM %s WHERE %s = ?", MovieModel.TABLE_NAME, MovieModel.COLUMN_TMDB_ID);

        Cursor result = db.rawQuery(query, new String[] {String.valueOf(tmdb_id)});

        // check if result is empty - if it's not empty, then move to the first result in array
        if (result.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    // get all favourites
    public List<MovieModel> getAllFavouriteMovies() {
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT * FROM fav.
        String query = String.format("SELECT * FROM %s", MovieModel.TABLE_NAME);
        Cursor result = db.rawQuery(query, null);
        result.moveToFirst();

        List<MovieModel> mList = new ArrayList<>(result.getCount());

        while (!result.isAfterLast()) {
            String tmdb_id = result.getString(result.getColumnIndex(MovieModel.COLUMN_TMDB_ID));
            String poster_path = result.getString(result.getColumnIndex(MovieModel.COLUMN_POSTER_PATH));

            mList.add(new MovieModel(poster_path, tmdb_id));
            result.moveToNext();
        }
        // return the whole list of fav. movies
        return mList;
    }
}
