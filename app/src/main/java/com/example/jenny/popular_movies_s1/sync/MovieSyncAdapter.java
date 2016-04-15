package com.example.jenny.popular_movies_s1.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.jenny.popular_movies_s1.DownloadJsonReviewTask;
import com.example.jenny.popular_movies_s1.DownloadJsonTrailerTask;
import com.example.jenny.popular_movies_s1.R;
import com.example.jenny.popular_movies_s1.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jenny on 11/14/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in milliseconds.
// 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 1/2;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private Cursor getMovieIDsPresentInDBcursor = null;
    private List<Integer> existingMovieIDList;
    String sortType;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //Log.d(LOG_TAG, "onPerformSync Called.");



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.sortType = prefs.getString(getContext().getResources().getString(R.string.pref_sort_key), getContext().getResources().getString(R.string.pref_sort_default));


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/discover/movie?sort_by=");
        stringBuilder.append(sortType);
        stringBuilder.append(".desc&api_key=");
        stringBuilder.append(getContext().getResources().getString(R.string.API_KEY));

       // Log.v(LOG_TAG, "onHandleIntent " + stringBuilder.toString());

        try{
            URL url = new URL(stringBuilder.toString());

            urlConnection  =  (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect(); // cannot be done on the main thread

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if(buffer.length() == 0){
                return;
            }
            movieJsonStr = buffer.toString();

            //Log.v(LOG_TAG, "BEGIN INSERT");
           // Log.v(LOG_TAG, movieJsonStr);
            getMovieDataFromJson(movieJsonStr);

        }catch(IOException e){
            Log.e(LOG_TAG, "Error", e);
            return;
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
            e.printStackTrace();
        }
        finally {
            if(urlConnection  != null){
                urlConnection.disconnect();
            }
            if(reader  != null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private void getMovieDataFromJson (String movieJsonString) throws JSONException{

        final String RESULTS = "results";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OBJECT = "object";
        final String PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String VOTE_COUNT = "vote_count";

        boolean exists = false;
        Integer id = null;

        /*
        get all the movie ids from database
         */
        setArrayListWithExistingMovieIDs();


        try {
            JSONObject moviesObj = new JSONObject(movieJsonString);
            JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);

            Vector<ContentValues> movieVector = new Vector<>(jsonArray.length());



            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);
                id = object.getInt(ID);

                if (!existingMovieIDList.contains(id)) {
                    String title = object.getString(ORIGINAL_TITLE);
                    String path = object.getString(PATH);
                    String overview = object.getString(OVERVIEW);
                    double vote = object.getDouble(VOTE_AVERAGE);
                    String releaseDate = object.getString(RELEASE_DATE);
                    int voteCount = object.getInt(VOTE_COUNT);
                    int sort;

                    sort = getMovieSortType(sortType);

                    ContentValues movieValues = createContentValues_Movie_ForInsert(id, title, path, overview, vote, releaseDate, voteCount, sort);
                    movieVector.add(movieValues);
                   // Log.v(LOG_TAG, "GETTING TRAILER AND MOVIE FOR ID " + String.valueOf(id));
                    DownloadJsonTrailerTask trailerReview = new DownloadJsonTrailerTask(getContext());
                    trailerReview.execute(String.valueOf(id));
                    DownloadJsonReviewTask downloadReview = new DownloadJsonReviewTask(getContext());
                    downloadReview.execute(String.valueOf(id));



                   // Log.v(LOG_TAG, movieValues.toString());
                }

            }


            //Log.v(LOG_TAG, "BEFORE INSERT");
          //  Log.v(LOG_TAG, "VECTOR SIZE " + String.valueOf(movieVector.size()));
            int inserted = 0;
            if (movieVector.size() > 0) {
                ContentValues[] movieArray = new ContentValues[movieVector.size()];
                movieVector.toArray(movieArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, movieArray);
            }
            //Log.v(LOG_TAG, "Complete " + inserted + " inserted");



        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private int getMovieSortType(String sortType){
        if ("popularity".equals(sortType)) {
            return 1;
        } else if ("vote_average".equals(sortType)) {
            return 2;
        }

        return -1;
    }

    private void setArrayListWithExistingMovieIDs(){
        try {
            getMovieIDsPresentInDBcursor = getContext().getContentResolver().query(
                    MovieContract.Movie.CONTENT_URI,
                    new String[]{MovieContract.Movie._ID},
                    null,
                    null,
                    MovieContract.Movie._ID
            );

            //Log.v(LOG_TAG, "Number of IDs returned " + String.valueOf(getMovieIDsPresentInDBcursor.getCount()));
            int idColumn = getMovieIDsPresentInDBcursor.getColumnIndex(MovieContract.Movie._ID);
            existingMovieIDList = new ArrayList<>();


            while (getMovieIDsPresentInDBcursor.moveToNext()) {
                existingMovieIDList.add(getMovieIDsPresentInDBcursor.getInt(idColumn));
                //Log.v(LOG_TAG,"movies in DB " + existingMovieIDList.size() + "  " + String.valueOf(getMovieIDsPresentInDBcursor.getInt(idColumn)));
            }
        }finally {
            // getMovieIDsPresentInDBcursor.close();
        }
    }

    private ContentValues createContentValues_Movie_ForInsert(int id,String title,String path,String overview,double vote,String releaseDate, int voteCount, int sort){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.Movie._ID, id);
        movieValues.put(MovieContract.Movie.TITLE, title);
        movieValues.put(MovieContract.Movie.POSTER_PATH, path);
        movieValues.put(MovieContract.Movie.OVERVIEW, overview);
        movieValues.put(MovieContract.Movie.VOTE_AVERAGE, vote);
        movieValues.put(MovieContract.Movie.RELEASE_DATE, releaseDate);
        movieValues.put(MovieContract.Movie.VOTE_COUNT, voteCount);
        movieValues.put(MovieContract.Movie.SORT_TYPE, sort);

        return movieValues;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
