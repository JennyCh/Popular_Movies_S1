package com.example.jenny.popular_movies_s1;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
    ListView  gridView;
    //ImageAdapter imageAdapter;
    public List <Movie> movies;

    private String sortTypeSaved;
    private ProgressBar progressBar;
    private View v;
    private View view;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "CREATING LOADER");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        String sort;
        if ("popularity".equals(sortType)){
            sort = "1";
        }else if("vote_average".equals(sortType)){
            sort = "2";
        }else{
            sort = "-1";
        }
        Log.v(LOG_TAG, sort);
        return new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null,MovieContract.Movie.SORT_TYPE + " = ?", new String[]{sort}, MovieContract.Movie.TITLE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public MainActivityFragment() {
        this.sortTypeSaved = "";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList<Movie>) movies);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container,false);
        ListView listView = (ListView) rootView.findViewById(R.id.gridview);
        listView.setAdapter(mMovieAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null){
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

                    int id = cursor.getColumnIndex(MovieContract.Movie._ID);

                  /*  DownloadJsonReviewTask downloadReview = new DownloadJsonReviewTask(getContext());
                    downloadReview.execute(String.valueOf(cursor.getInt(id)));

                    DownloadJsonTrailerTask trailerReview = new DownloadJsonTrailerTask(getContext());
                    trailerReview.execute(String.valueOf(cursor.getInt(id)));*/

                    Intent intent = new Intent(getActivity(), DetailActivity.class )
                            .setData(MovieContract.Movie.buildMovieID(cursor.getInt(id)));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    void onSortChange(){
        //update();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void update(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
Log.v(LOG_TAG, sortType);

        if (!sortTypeSaved.equals(sortType)){
            onSortChange();
            sortTypeSaved = sortType;
        }

        DownloadJsonDataTask asyncDownload = new DownloadJsonDataTask(getContext());
        asyncDownload.execute(sortType);

        //sortTypeSaved = sortType;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
                update();
    }


}