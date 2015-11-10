package com.example.jenny.popular_movies_s1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    //public List<Movie> movies;
   // private boolean twoPane;
   // Context context;

    private static final String LOG_TAG = "MainActivity";
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private SharedPreferences prefs;
    private String sortType;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container) != null){
            Log.v(LOG_TAG, "TWO PANE");
            mTwoPane = true;

            //In case that the device was simply rotated, we do not want to recreate the fragemnt
            if (savedInstanceState == null){
                Log.v(LOG_TAG, "SAVED INSTANCE STATE");
                //WE  ADD A TAG, SO LATER IN THE ONRESUME METHOD WE CAN EXTRACT THAT SAME FRAGMENT BY TAG
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailActivityFragment(), MOVIEFRAGMENT_TAG).commit();
            }
            Log.v(LOG_TAG, "PASSED INSTANCE STATE");

        }else{
            Log.v(LOG_TAG, "ONE PANE");
            mTwoPane = false;
        }



        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        /*this.sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        */




    }

    @Override
    protected void onResume() {
        super.onResume();
       // Log.v("MainActivity", "onResume");
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

       // Log.v("MainActivity", sort + " | " + sortType);
        if(sort != null && !sort.equals(this.sortType)){
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if(null != mainActivityFragment){
                Log.v(LOG_TAG, "onSortChange");
                mainActivityFragment.onSortChange();
            }
            //NOT NEEDED HERE, WE DON'T NEED TO UPDATE THE DETAIL JUST BASED ON THE SORT CHANGE
            DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if(null != detailActivityFragment){
                Log.v(LOG_TAG, "ON RESUME detailActivityFragment" );
                int id= 76341; //TODO CHANGE THIS ID TO THE ONE THAT'S ONCLICK
                detailActivityFragment.onIDChange(id);
            }
        }
        this.sortType = sort;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            // Log.v("HERE ", "launching Settings 1");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {

        Log.v(LOG_TAG, "Callback onItemSelected " + movieUri.toString());
        if(mTwoPane){
            Log.v(LOG_TAG, "Callback onItemSelected " + "TWO PANE");
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, movieUri);
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);
            Log.v(LOG_TAG, "TWO PANE " + DetailActivityFragment.DETAIL_URI + " " + movieUri);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment, MOVIEFRAGMENT_TAG).commit();
        }else{
            Log.v(LOG_TAG, "Callback onItemSelected " + "ONE PANE");
            Intent intent = new Intent(this, DetailActivity.class).setData(movieUri);
            startActivity(intent);
        }
    }
}