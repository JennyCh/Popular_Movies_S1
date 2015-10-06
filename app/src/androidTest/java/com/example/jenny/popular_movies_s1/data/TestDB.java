package com.example.jenny.popular_movies_s1.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by Jenny on 9/29/2015.
 */
public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    void deleteTheDatabase(){
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setUp(){
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable{
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.Movie.TABLE_NAME);
        tableNameHashSet.add(MovieContract.Review.TABLE_NAME);
        tableNameHashSet.add(MovieContract.Trailer.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do{
            tableNameHashSet.remove(c.getString(0));
        }while (c.moveToNext());


        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.Movie.TABLE_NAME + ")",null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.Movie._ID);
        movieColumnHashSet.add(MovieContract.Movie.TITLE);
        movieColumnHashSet.add(MovieContract.Movie.OVERVIEW);
        movieColumnHashSet.add(MovieContract.Movie.POSTER_PATH);
        movieColumnHashSet.add(MovieContract.Movie.RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.Movie.VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.Movie.SORT_TYPE);
        movieColumnHashSet.add(MovieContract.Movie.FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");

        do{
            String columnName =  c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required columns",
                movieColumnHashSet.isEmpty());
        db.close();

    }

    public void testMovieTable(){
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();

        long locationRowId;
        locationRowId = db.insert(MovieContract.Movie.TABLE_NAME, null, movieValues);
        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(
                MovieContract.Movie.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, movieValues);
        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());
        cursor.close();
        db.close();
    }

}
