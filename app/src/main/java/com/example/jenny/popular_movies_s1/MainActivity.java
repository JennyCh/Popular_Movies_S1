package com.example.jenny.popular_movies_s1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class MainActivity extends ActionBarActivity {

    //public List<Movie> movies;
   // private boolean twoPane;
   // Context context;
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private SharedPreferences prefs;
    private String sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MainActivityFragment(), MOVIEFRAGMENT_TAG).commit();
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        /*this.sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        */




    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("MainActivity", "onResume");
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        Log.v("MainActivity", sort + " | " + sortType);
        if(sort != null && !sort.equals(this.sortType)){
            MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if(null != mainActivityFragment){
                Log.v("MainActivity", "onSortChange");
                mainActivityFragment.onSortChange();
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
}