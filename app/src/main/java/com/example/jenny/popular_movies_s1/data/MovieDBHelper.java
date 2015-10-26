package com.example.jenny.popular_movies_s1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jenny.popular_movies_s1.Review;

/**
 * Created by Jenny on 9/28/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    static final String DATABASE_NAME = "movie.db";

    //Log.v

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.Review.TABLE_NAME + " (" +
                MovieContract.Review._ID + " INTEGER NOT NULL, " +
                MovieContract.Review.UNIQUE_REVIEW_ID + " TEXT PRIMARY KEY, " +
                MovieContract.Review.AUTHOR +  " TEXT NOT NULL, " +
                MovieContract.Review.CONTENT + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + MovieContract.Review._ID + ") REFERENCES " +
                MovieContract.Review.TABLE_NAME + " (" + MovieContract.Movie._ID + ")" +
                ");";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.Trailer.TABLE_NAME + " (" +
                MovieContract.Trailer._ID + " INTEGER NOT NULL, " +
                MovieContract.Trailer.UNIQUE_TRAILER_ID + " TEXT PRIMARY KEY, " +
                MovieContract.Trailer.KEY + " TEXT NOT NULL, " +
                MovieContract.Trailer.NAME + " TEXT NOT NULL, " +
                MovieContract.Trailer.TYPE + " TEXT NOT NULL, " +
                MovieContract.Trailer.SIZE + " INTEGER, " +


                " FOREIGN KEY (" + MovieContract.Trailer._ID + ") REFERENCES " +
                MovieContract.Trailer.TABLE_NAME + " (" + MovieContract.Movie._ID + ")" +
                ");";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.Movie.TABLE_NAME + " (" +
                MovieContract.Movie._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.Movie.TITLE + " TEXT NOT NULL, " +
                MovieContract.Movie.OVERVIEW + " TEXT DEFAULT 'NO OVERVIEW', " +
                MovieContract.Movie.POSTER_PATH + " TEXT DEFAULT 'NO POSTER', " +
                MovieContract.Movie.RELEASE_DATE + " TEXT DEFAULT 'NO DATE AVAILABLE'," +
                MovieContract.Movie.VOTE_AVERAGE + " TEXT DEFAULT 'NO VOTES YET'," +
                MovieContract.Movie.SORT_TYPE + " INTEGER NOT NULL, " +
                MovieContract.Movie.FAVORITE + " INTEGER DEFAULT 0, " +
                MovieContract.Movie.VOTE_COUNT + " INTEGER DEFAULT 0" +
                " );";
        Log.v("MovieDBHelper", SQL_CREATE_MOVIE_TABLE);
        Log.v("MovieDBHelper", SQL_CREATE_REVIEW_TABLE);
        Log.v("MovieDBHelper", SQL_CREATE_TRAILER_TABLE);


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*
        Needs something that will wipe all but favorites from all 3 tables
         */

        Log.v("MovieDBHelper", "DELETING TABLES");

        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Trailer.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Review.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        onCreate(db);
    }
}
