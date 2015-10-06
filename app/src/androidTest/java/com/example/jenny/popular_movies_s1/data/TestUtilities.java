package com.example.jenny.popular_movies_s1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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
        movieValues.put(MovieContract.Movie.OVERVIEW, "Rose frees Jack from his chains with an axe. They both manage to find a boat but only Rose is allowed on, with persuasion from Jack and Cal. However, she can't leave Jack and jumps off the boat back to Titanic. Jack and Rose are then both chased by Cal shooting at them with his manservants gun. They barely escape. After a few narrow escapes from the water they get back to the top deck and find their way to the stern of the ship, where they ride the ship down. They both manage to fight the ships suction and get to the surface. Jack finds a wooden door and knows it is Rose's best chance. He stays in the water whilst she is on the door making her promise to never let go, to survive. She promises. One boat turns back out of the twenty nearby and rescues Rose as Jack has now frozen to death. Rose whispers that she will never let go as she lets him sink. The story then ends with the hunters saying there were never records of Jack. Rose then reveals to the audience that she had the diamond all along as it had been in the pocket of a coat Cal (who had shot himself in 1929, 17 years later) had put on her. She throws it overboard to the Titanic and Jack. She then dies in her bed, surrounded with pictures of her wonderful life, including riding horseback and riding on the rollercoaster, like Jack had told her. We are left with an image of her rejoining Jack and all the others who died on the deck of the Titanic.");
        movieValues.put(MovieContract.Movie.POSTER_PATH, "titanic.image.jpg");
        movieValues.put(MovieContract.Movie.RELEASE_DATE, "Jul 3, 1997");
        movieValues.put(MovieContract.Movie.VOTE_AVERAGE, "10.0");
        movieValues.put(MovieContract.Movie.SORT_TYPE, 1);
        movieValues.put(MovieContract.Movie.FAVORITE, 1);

        return  movieValues;
    }

    static ContentValues createTrailerVelues (){
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.Trailer._ID, 1234);
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
        locationRowId = db.insert(MovieContract.Movie.TABLE_NAME, null, movieValues);
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

}
