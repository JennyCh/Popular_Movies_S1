package com.example.jenny.popular_movies_s1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

    public List<Movie> movies;
    private boolean twoPane;
    private final String MOVIEFRAGMENT_TAG = "MFTAG";

    private String sortType;

    // public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container, new MainActivityFragment(), MOVIEFRAGMENT_TAG).commit();
        }*/
   /*     if(findViewById(R.id.movie_detail_container) != null){

            twoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailActivityFragment()).commit();
            }
        }else{
            twoPane = false;
        }*/


        //this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

 /*   @Override
    protected void onResume() {
        super.onResume();

        String sortType =
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
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