package com.example.jenny.popular_movies_s1;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.jenny.popular_movies_s1.data.MovieContract;
import com.example.jenny.popular_movies_s1.service.MovieService;
import com.example.jenny.popular_movies_s1.sync.MovieSyncAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;
import java.util.prefs.PreferenceChangeEvent;
import java.lang.Runnable;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;

    private final static String LOG_TAG = "MainActivityFragment";
    private MovieAdapter mMovieAdapter;
    //GridView  gridView;
    GridView  gridView;
    //ImageAdapter imageAdapter;
    public List <Movie> movies;
    int mPosition;

    private String sortTypeSaved;
    private ProgressBar progressBar;
    private View v;
    private View view;
    private SharedPreferences prefs;
    private static final String SELECTED_KEY = "selected_position";
    private int idValue;


    public int getIdValue() {
        return idValue;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "CREATING LOADER");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        CursorLoader cursor;
        String sort;
        if ("popularity".equals(sortType)){
            sort = "1";
            cursor = new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null,MovieContract.Movie.SORT_TYPE + " = ?", new String[]{sort}, null);
        }else if("vote_average".equals(sortType)){
            sort = "2";
            cursor = new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null,MovieContract.Movie.SORT_TYPE + " = ?", new String[]{sort}, null);
        }else{
            sort = "-1";
            cursor = new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null,MovieContract.Movie.FAVORITE + " = ?", new String[]{"1"}, null);
        }


        Log.v(LOG_TAG, "CURSOR onCreateLoader");

        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "CURSOR onLoadFinished " + cursor.getCount());
        if(cursor.getCount() > 0) {
            Log.v(LOG_TAG, "CURSOR POSITION " + cursor.getPosition());
            cursor.moveToNext();
            // for (int i = 0; i <= 1; i++) {
            if (mPosition == 0) {
                int idColumn = cursor.getColumnIndex(MovieContract.Movie._ID);
                this.idValue = cursor.getInt(idColumn);
                setFirstElementID();
            }
            //}
            Log.v(LOG_TAG, "CURSOR " + String.valueOf(idValue));

            //cursor.close();
        }else{
            idValue = 0;
            Log.v(LOG_TAG, "CURSOR " + String.valueOf(idValue));
        }

        mMovieAdapter.swapCursor(cursor);

        if(mPosition != GridView.INVALID_POSITION){
            Log.v(LOG_TAG, "SAVED SETTING POSITION " + mPosition);
            gridView.smoothScrollToPosition(mPosition);
        }
        // mMovieAdapter.swapCursor(cursor);

    }

    private void setFirstElementID (){
        Log.v(LOG_TAG, "TEST setFirstElementID " + String.valueOf(idValue));
        ((Callback) getActivity()).onFirstLoad(idValue);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "CURSOR onLoaderReset");
        mMovieAdapter.swapCursor(null);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList("movies", (ArrayList<Movie>) movies);
        if(mPosition != GridView.INVALID_POSITION){
            Log.v(LOG_TAG,  "SAVING INSTANCE STATE " + mPosition);
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        //  Log.v(LOG_TAG, "CURSOR --- callbck" + String.valueOf(idValue));
        //((Callback) getActivity()).onFirstLoad(Uri.parse("content://com.example.jenny.popular_movies_s1/movie/" + String.valueOf(idValue)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //ListView listView = (ListView) rootView.findViewById(R.id.gridview);
        this.gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mMovieAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    int idColumn = cursor.getColumnIndex(MovieContract.Movie._ID);
                    String idData = cursor.getString(idColumn);
                    Log.v(LOG_TAG, "PASS TO CALLBACK " + MovieContract.Movie.buildMovieID(Integer.valueOf(idData)).toString());
                    ((Callback) getActivity()).onItemSelected(MovieContract.Movie.buildMovieID(Integer.valueOf(idData)));

                }
                Log.v(LOG_TAG, "GLOBAL POSITION " + position);
                mPosition = position;


                // String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));



                  /*  DownloadJsonReviewTask downloadReview = new DownloadJsonReviewTask(getContext());
                    downloadReview.execute(String.valueOf(cursor.getInt(id)));
                    DownloadJsonTrailerTask trailerReview = new DownloadJsonTrailerTask(getContext());
                    trailerReview.execute(String.valueOf(cursor.getInt(id)));*/

                    /*Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.Movie.buildMovieID(cursor.getInt(id)));
                    startActivity(intent);*/

            }
        });

        Log.v(LOG_TAG, "SAVED POSITION 1" + SELECTED_KEY);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            //Log.v(LOG_TAG, "SAVED POSITION 2" + SELECTED_KEY);
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            Log.v(LOG_TAG, "SAVED POSITION 2 " + mPosition);
        }

        return rootView;
    }

    public interface Callback{
        /*
        Callback for when an item has been selected
         */
        public void onItemSelected(Uri movieUri);
        public void onFirstLoad(int id);
    }


    void onSortChange(){
        Log.v(LOG_TAG, "onSortChange");
        update();
        ((Callback) getActivity()).onFirstLoad(0);
        this.mPosition = 0;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void update(){
        Log.v(LOG_TAG, "update");
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        Log.v(LOG_TAG + "UPD", sortType);


        MovieSyncAdapter.syncImmediately(getActivity());


/*if (isNetworkConnected()) {
*//*    Log.v("INTERNET", "CONNECTED");
    DownloadJsonDataTask asyncDownload = new DownloadJsonDataTask(getContext());
    asyncDownload.execute(sortType);*//*
    Intent alarmIntent = new Intent(getActivity(), MovieService.AlarmReceiver.class);
    //alarmIntent.putExtra(MovieService.LOCATION_QUERY_EXTRA, mPosition);
    PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
    AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pi);
    Intent intent = new Intent(getActivity(), MovieService.class);
    intent.putExtra(MovieService.LOCATION_QUERY_EXTRA,sortType);
    getActivity().startService(intent);
}else{
    Log.v("INTERNET", "NOT CONNECTED");
}*/

        //sortTypeSaved = sortType;

    }



/*    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        Log.v(LOG_TAG, sort + " | " + this.sortTypeSaved);
        if(sort != null && !sort.equals(this.sortTypeSaved)){
            Log.v(LOG_TAG, "onSortChange");
            onSortChange();
        }
        this.sortTypeSaved = sort;
                update();
    }*/


}