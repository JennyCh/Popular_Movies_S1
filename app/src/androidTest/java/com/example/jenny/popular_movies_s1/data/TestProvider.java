package com.example.jenny.popular_movies_s1.data;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Jenny on 10/6/2015.
 */
public class TestProvider extends AndroidTestCase{

    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try{
            ProviderInfo providerInfo = pm.getProviderInfo(componentName,0);
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                        " instead of authority: " + MovieContract.CONTENT_AUTHORITY, providerInfo.authority, MovieContract.CONTENT_AUTHORITY);

        }catch (PackageManager.NameNotFoundException e){
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    public void testGetType(){
        String type;


      //PASSED
      type = mContext.getContentResolver().getType(MovieContract.Movie.CONTENT_URI);
        assertEquals("Error: the Movie CONTENT_URI should return Movie.CONTENT_DIR_TYPE", MovieContract.Movie.CONTENT_DIR_TYPE, type);

       //PASSED
       int testID = 12345;
        type = mContext.getContentResolver().getType(MovieContract.Movie.buildMovieID(testID));
        Log.v("Type", "MovieContract.Movie.CONTENT_ITEM_TYPE");
        //Log.v("Type", type.toString());
        assertEquals("Error: the Movie CONTENT_URI with id should return Movie.CONTENT_ITEM_TYPE", MovieContract.Movie.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.Review.CONTENT_URI);
        assertEquals("Error: the Review CONTENT_URI should return Review.CONTENT_DIR_TYPE", MovieContract.Review.CONTENT_DIR_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.Review.buildReviewID(testID));
        assertEquals("Error: the Review CONTENT_URI should return Review.CONTENT_DIR_TYPE", MovieContract.Review.CONTENT_DIR_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.Trailer.CONTENT_URI);
        assertEquals("Error: the Review CONTENT_URI should return Review.CONTENT_DIR_TYPE", MovieContract.Trailer.CONTENT_DIR_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.Trailer.buildTrailerID(testID));
        assertEquals("Error: the Review CONTENT_URI should return Review.CONTENT_DIR_TYPE", MovieContract.Trailer.CONTENT_DIR_TYPE, type);
    }

    public void testBasicMovieQuery(){
/*        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        long recordNum = db.insert(MovieContract.Movie.TABLE_NAME, null,movieValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", recordNum != -1);
        db.close();*/
        ContentValues movieValues = TestUtilities.createMovieValues();
        String selection = "MovieContract.Movie.favorite = ?";
        String [] selectionArgs = new String [] {"'1'"};
        String sortOrder = "MovieContract.Movie.title";



        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.Movie.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder
        );
        TestUtilities.validateCursor("testBasicWeatherQuery", movieCursor, movieValues);

        movieCursor.close();
    }
}
