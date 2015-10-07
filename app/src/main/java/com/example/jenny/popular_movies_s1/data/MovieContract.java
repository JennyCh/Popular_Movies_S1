package com.example.jenny.popular_movies_s1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Jenny on 9/28/2015.
 * Defines tables and column names for the Popular Movies database
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.jenny.popular_movies_s1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class Movie implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String _ID = "id";
        public static final String TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String POSTER_PATH = "poster_path";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String SORT_TYPE = "sort_type";
        public static final String FAVORITE = "favorite";

        public static Uri buildMovieID(int id){
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            Log.v("BUILD MOVIE ID", uri.toString());
            return uri;
        }

      /*  public static Uri buildMovie


      ID(int id){
            //return ContentUris.withAppendedId(CONTENT_URI, id);
            Uri uri = CONTENT_URI.buildUpon().appendQueryParameter(_ID, String.valueOf(id)).build();
            Log.v("BUILD MOVIE ID", uri.toString());
            return uri;
        }*/

        public static Uri buildMovieType (int type){
            return CONTENT_URI.buildUpon().appendQueryParameter(SORT_TYPE, String.valueOf(type)).build();
        }

        public static Uri buildMovieFavorite (){
            return CONTENT_URI.buildUpon().appendQueryParameter(FAVORITE, "1").build();
        }
    }

    public static final class Review implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";

        public static Uri buildReviewID(int id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class Trailer implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String KEY = "key";
        public static final String NAME = "name";
        public static final String TYPE = "type";

        public static Uri buildTrailerID(int id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
