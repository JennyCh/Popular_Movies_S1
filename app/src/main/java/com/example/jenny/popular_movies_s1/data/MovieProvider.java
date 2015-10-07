package com.example.jenny.popular_movies_s1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Jenny on 9/30/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMathcher = buildUriMatcher();
    private MovieDBHelper dbHelper;

    static final int MOVIE =  10;
    static final int REVIEW = 20;
    static final int TRAILER = 30;

    static final int MOVIE_WITH_ID = 11;
    static final int MOVIE_WITH_TYPE = 12;
    static final int MOVIE_WITH_FAVORITE = 13;

    static final int REVIEW_WITH_ID = 21;

    static final int TRAILER_WITH_ID = 31;

    private static final SQLiteQueryBuilder movieByIdQueryBuilder;

    static{
        movieByIdQueryBuilder = new SQLiteQueryBuilder();
        movieByIdQueryBuilder.setTables(
                MovieContract.Movie.TABLE_NAME
        );
    }

    static  UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/", MOVIE_WITH_TYPE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/", MOVIE_WITH_FAVORITE);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDBHelper(getContext());
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /*
        uri - uri to query , full uri is provided by client
        projection - list of columns to put on the cursor, if null all columns are selected
        selection - criteria to apply when filtering the rows
        selectionArgs -
        sort order = how to sort
         */

        Cursor cursor;

        switch (uriMathcher.match(uri)){
            case MOVIE_WITH_ID:{
                return null ;
            }
            case MOVIE:{
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW:{
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Review.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            case TRAILER:{
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Trailer.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.v("URI GET MATCHER", uri.toString());
        final int match = uriMathcher.match(uri);

        switch (match){
            case MOVIE:
            case MOVIE_WITH_FAVORITE:
            case MOVIE_WITH_TYPE:
                return MovieContract.Movie.CONTENT_DIR_TYPE;

            case MOVIE_WITH_ID:
                return MovieContract.Movie.CONTENT_ITEM_TYPE;

            case TRAILER:
            case TRAILER_WITH_ID:
                return MovieContract.Trailer.CONTENT_DIR_TYPE;

            case REVIEW:
            case REVIEW_WITH_ID:
                return MovieContract.Review.CONTENT_DIR_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
