package com.example.jenny.popular_movies_s1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Created by Jenny on 9/29/2015.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor,ContentValues expectedValues){
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues (){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.Movie._ID, 1234);
        movieValues.put(MovieContract.Movie.TITLE, "TITANIC");
        movieValues.put(MovieContract.Movie.OVERVIEW, "Rose frees Jack from his chains with an axe.");
        movieValues.put(MovieContract.Movie.POSTER_PATH, "titanic.image.jpg");
        movieValues.put(MovieContract.Movie.RELEASE_DATE, "Jul 3, 1997");
        movieValues.put(MovieContract.Movie.VOTE_AVERAGE, "10.0");
        movieValues.put(MovieContract.Movie.SORT_TYPE, 1);
        movieValues.put(MovieContract.Movie.FAVORITE, 1);

        return  movieValues;
    }

    static ContentValues createTrailerVelues (long id){
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.Trailer._ID, id);
        trailerValues.put(MovieContract.Trailer.KEY, "ahduHJK08GL8j8");
        trailerValues.put(MovieContract.Trailer.NAME, "Trailer from Hell");
        trailerValues.put(MovieContract.Trailer.TYPE, "Trailer");

        return trailerValues;
    }

    static long insertMovieValues(Context context){
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues movieValues = TestUtilities.createMovieValues();

        long locationRowId;
        Log.v("MOVIE VALUES", movieValues.toString());
        locationRowId = db.insert(MovieContract.Movie.TABLE_NAME, null, movieValues);
        Log.v("ROW ID", String.valueOf(locationRowId));
        assertTrue("Error: Failure to insert Movie Values", locationRowId != -1);

        return locationRowId;
    }

}
