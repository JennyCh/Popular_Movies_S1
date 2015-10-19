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

    private static final UriMatcher uriMathcher = buildUriMatcher();
    private MovieDBHelper dbHelper;

    static final int MOVIE =  10;
    static final int REVIEW = 20;
    static final int TRAILER = 30;

    static final int MOVIE_WITH_ID = 11;
    //static final int MOVIE_WITH_TYPE = 12;
    //static final int MOVIE_WITH_FAVORITE = 13;

    static final int REVIEW_WITH_ID = 21;

    static final int TRAILER_WITH_ID = 31;



    static  UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE + "/type/*", MOVIE_WITH_TYPE);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorite/*", MOVIE_WITH_FAVORITE);

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

     /*
        uri - uri to query , full uri is provided by client
        projection - list of columns to put on the cursor, if null all columns are selected
        selection - criteria to apply when filtering the rows
        selectionArgs -
        sort order = how to sort
         */


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        Log.v("QUERY MovieProvider", uri.toString());
        switch (uriMathcher.match(uri)){
            case MOVIE_WITH_ID:{
                Log.v("MovieProvider QUERY", "MOVIE WITH ID");
                //cursor = getMovieWithId(uri);
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME ,null, "MovieContract.Movie._ID =", selectionArgs,null,null,sortOrder);
            }
            break;

            case MOVIE:{
                Log.v("MovieProvider QUERY", "MOVIE");
                //Log.v("MovieProvider QUERY", selection);
                //Log.v("MovieProvider QUERY", selectionArgs[0]);
                cursor = dbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME, null,selection, selectionArgs, null, null, sortOrder);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
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
                Log.v("GET TYPE", "MOVIE");
                return MovieContract.Movie.CONTENT_DIR_TYPE;

            case REVIEW:
                Log.v("GET TYPE", "REVIEW");
                return MovieContract.Review.CONTENT_DIR_TYPE;

            case TRAILER:
                Log.v("GET TYPE", "TRAILER");
                return MovieContract.Trailer.CONTENT_DIR_TYPE;

            case MOVIE_WITH_ID:
                Log.v("GET TYPE", "MOVIE_WITH_ID");
                return MovieContract.Movie.CONTENT_ITEM_TYPE;

            case REVIEW_WITH_ID:
                Log.v("GET TYPE", "REVIEW_WITH_ID");
                return MovieContract.Review.CONTENT_DIR_TYPE;

            case TRAILER_WITH_ID:
                Log.v("GET TYPE", "TRAILER_WITH_ID");
                return MovieContract.Trailer.CONTENT_DIR_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMathcher.match(uri);
        int rowDeleted;

        if (null == selection) selection = "1";
        switch (match){
            case MOVIE:
                rowDeleted = db.delete(MovieContract.Movie.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
    private static final SQLiteQueryBuilder reviewByMovieQuery;
    static{
        reviewByMovieQuery = new SQLiteQueryBuilder();
        reviewByMovieQuery.setTables(MovieContract.Movie.TABLE_NAME + " INNER JOIN " +
            MovieContract.Review.TABLE_NAME + " ON " +
                        MovieContract.Movie.TABLE_NAME +"." + MovieContract.Movie._ID + " = " +
                        MovieContract.Review.TABLE_NAME +"."+ MovieContract.Review._ID
        );
    }

    private static final SQLiteQueryBuilder trailerByMovieQuery;
    static{
        trailerByMovieQuery = new SQLiteQueryBuilder();
        trailerByMovieQuery.setTables(MovieContract.Movie.TABLE_NAME + " INNER JOIN " +
                        MovieContract.Trailer.TABLE_NAME + " ON " +
                        MovieContract.Movie.TABLE_NAME +"." + MovieContract.Movie._ID + " = " +
                        MovieContract.Trailer.TABLE_NAME +"."+ MovieContract.Trailer._ID
        );
    }





}
