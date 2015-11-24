package com.example.jenny.popular_movies_s1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.example.jenny.popular_movies_s1.Movie;

/**
 * Created by Jenny on 9/30/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG =  "MovieProvider";

    private static final UriMatcher uriMathcher = buildUriMatcher();
    private MovieDBHelper dbHelper;

    static final int MOVIE =  10;
    static final int MOVIE_WITH_ID = 11;

    static final int REVIEW = 20;
    static final int REVIEW_WITH_ID = 21;

    static final int TRAILER = 30;
    static final int TRAILER_WITH_ID = 31;

    static  UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_ID);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMathcher.match(uri);

        switch (match){
            case MOVIE:
                return MovieContract.Movie.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.Movie.CONTENT_ITEM_TYPE;


            case REVIEW:
                return MovieContract.Review.CONTENT_DIR_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.Review.CONTENT_DIR_TYPE;


            case TRAILER:
                return MovieContract.Trailer.CONTENT_DIR_TYPE;
            case TRAILER_WITH_ID:
                return MovieContract.Trailer.CONTENT_DIR_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        Cursor cursor;
        //Log.v("QUERY MovieProvider", uri.toString());
        switch (uriMathcher.match(uri)){
            case MOVIE_WITH_ID:{
                //Log.v("MovieProvider QUERY", "MOVIE WITH ID");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME ,null, MovieContract.Movie._ID  + "= ?", selectionArgs,null,null,sortOrder);
               // Log.v(LOG_TAG, "RETURNING " + String.valueOf(cursor.getCount()));
                break;
            }
            case MOVIE:{
              //  Log.v("MovieProvider QUERY", "MOVIE");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case REVIEW_WITH_ID:{
                //Log.v("MovieProvider QUERY", "REVIEW WITH ID");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Review.TABLE_NAME,null, MovieContract.Review._ID + " = ?",selectionArgs, null,null,sortOrder);
                break;
            }
            case REVIEW:{
               // Log.v("MovieProvider QUERY", "REVIEW");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Review.TABLE_NAME,null, selection,selectionArgs, null,null,sortOrder);
                break;
            }
            case TRAILER_WITH_ID:{
                //Log.v("MovieProvider QUERY", "TRAILER WITH ID");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Trailer.TABLE_NAME,null, MovieContract.Trailer._ID + " = ?",selectionArgs, null,null,sortOrder);
                break;
            }
            case TRAILER:{
                //Log.v("MovieProvider QUERY", "TRAILER");
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Trailer.TABLE_NAME,null, selection,selectionArgs, null,null,sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMathcher.match(uri);

        Uri returnUri = MovieContract.Movie.CONTENT_URI;
        Log.d("INSERT MOVIE PROVIDER", "HERE");
        Log.d("INSERT MOVIE PROVIDER", String.valueOf(match));


        switch(match){
            case MOVIE:{
                Log.d("INSERT MOVIE PROVIDER", "MOVIE");
                long _id = db.insert(MovieContract.Movie.TABLE_NAME, null,values);
                if (_id < 0) {
                    throw new android.database.SQLException("Failed to insert into " + uri);
                }else{
                    returnUri = MovieContract.Movie.buildMovieID((int) _id);
                }
                break;
            }
            case TRAILER:{
                long _id = db.insert(MovieContract.Trailer.TABLE_NAME,null,values);
                if (_id < 0){
                    throw new android.database.SQLException("Failed to insert into " + uri);
                }else{
                    returnUri = MovieContract.Movie.buildMovieID((int) _id);
                }
                break;
            }
            case REVIEW:{
                long _id = db.insert(MovieContract.Review.TABLE_NAME,null,values);
                if (_id < 0){
                    throw new android.database.SQLException("Failed to insert into " + uri);
                }else{
                    returnUri = MovieContract.Movie.buildMovieID((int) _id);
                }
                    break;
            }
           /* default:
                Log.v("INSERT MOVIE PROVIDER", "DEFAULT");
                throw new UnsupportedOperationException("Unknown uri: " + uri);*/
        }

        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    /*
    Only delete those records that were not set FAVORITE
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMathcher.match(uri);
        int rowDeleted;

/*
Temp solution to test insert
 */
        switch (match){
            case MOVIE:
                rowDeleted = db.delete(MovieContract.Movie.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowDeleted = db.delete(MovieContract.Review.TABLE_NAME, selection,selectionArgs);
                break;
            case TRAILER:
                rowDeleted = db.delete(MovieContract.Trailer.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //if (null == selection) selection = "1";
  /*      switch (match){
            case MOVIE:
                rowDeleted = db.delete(MovieContract.Movie.TABLE_NAME, MovieContract.Movie._ID + " != ?", selectionArgs);
                break;
            case REVIEW:
                rowDeleted = db.delete(MovieContract.Review.TABLE_NAME, MovieContract.Review._ID + " != ?",selectionArgs);
                break;
            case TRAILER:
                rowDeleted = db.delete(MovieContract.Trailer.TABLE_NAME,MovieContract.Trailer._ID + " != ?",selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }*/

        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;

    }

    /*
    I will only ever need an update for the movie with ID if it ever selected favorite
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMathcher.match(uri);
        int rowUpdated;

        switch (match){
            case MOVIE_WITH_ID:{
                rowUpdated = db.update(MovieContract.Movie.TABLE_NAME,values,MovieContract.Movie._ID + " = ?",selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMathcher.match(uri);

        switch (match){
            case MOVIE:
                db.beginTransaction();
                int movieRowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        //Log.v(LOG_TAG, "VALUE INSERTED " + value.toString());
                        long result = db.insert(MovieContract.Movie.TABLE_NAME, null, value);
                        if (result != -1) {
                            movieRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri,null);
                return movieRowsInserted;
            case TRAILER:
                db.beginTransaction();
                int trailerRowsInserted = 0;
                try{
                    for(ContentValues value: values){
                        long result = db.insert(MovieContract.Trailer.TABLE_NAME, null, value);
                        if (result != -1){
                            trailerRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return trailerRowsInserted;
            case REVIEW:
                db.beginTransaction();
                int reviewRowsInserted = 0;
                try{
                    for (ContentValues value: values){
                        long result = db.insert(MovieContract.Review.TABLE_NAME, null, value);
                        if (result != -1){
                            reviewRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return reviewRowsInserted;
            default:{

            }
        }
        return super.bulkInsert(uri, values);
    }
}
