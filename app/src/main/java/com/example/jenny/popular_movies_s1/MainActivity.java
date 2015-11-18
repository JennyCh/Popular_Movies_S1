package com.example.jenny.popular_movies_s1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jenny.popular_movies_s1.sync.MovieSyncAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback, DetailActivityFragment.Callback {

    //public List<Movie> movies;
   // private boolean twoPane;
   // Context context;

    private static final String LOG_TAG = "MainActivity";
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private SharedPreferences prefs;
    private String sortType;
    private boolean mTwoPane;
    private String shareMessage;
    MainActivityFragment mainActivityFragment;
    private ShareActionProvider shareActionProvider;
    private int id;
    private DetailActivityFragment detailActivityFragment;

    private static final String DETAIL_ID = "detailid";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "SAVING STATE RESTORE " + String.valueOf(id));

        outState.putInt(DETAIL_ID, id);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "BEFORE RESTORED ID " + this.id);
        if(savedInstanceState != null){
            Log.v(LOG_TAG,  "RESTORED ID " + this.id);
            this.id = savedInstanceState.getInt(DETAIL_ID);
            Log.v(LOG_TAG,  "RESTORED ID " + this.id);
        }
        Log.v(LOG_TAG,  "AFTER RESTORED ID " + this.id);
        Log.v("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
       // this.mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
        if(findViewById(R.id.movie_detail_container) != null){
            Log.v(LOG_TAG, "TWO PANE");
            mTwoPane = true;
           // DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
           // Bundle bundle = new Bundle();
//
           // Log.v(LOG_TAG, "CURSOR-" + String.valueOf(mainActivityFragment.getIdValue()));
          ///  bundle.putString(DetailActivityFragment.DETAIL_URI, "content://com.example.jenny.popular_movies_s1/movie/" + String.valueOf(mainActivityFragment.getIdValue()));
          //  detailActivityFragment.setArguments(bundle);
            //In case that the device was simply rotated, we do not want to recreate the fragemnt
            if (savedInstanceState == null){
                Log.v(LOG_TAG, "SAVED INSTANCE STATE");
                //WE  ADD A TAG, SO LATER IN THE ONRESUME METHOD WE CAN EXTRACT THAT SAME FRAGMENT BY TAG

                this.detailActivityFragment = new DetailActivityFragment();


                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, this.detailActivityFragment, MOVIEFRAGMENT_TAG).commit();

            }
            Log.v(LOG_TAG, "PASSED INSTANCE STATE");

        }else{
            Log.v(LOG_TAG, "ONE PANE");
            mTwoPane = false;
        }



        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        /*this.sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        */

        Log.v(LOG_TAG, "MovieSyncAdapter.initializeSyncAdapter(this)");
        MovieSyncAdapter.initializeSyncAdapter(this);


    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.v("MainActivity", "CURSOR onResume");
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        //int id= mainActivityFragment.getIdValue();
        if (this.sortType == null){
            this.sortType = sort;
        }


        //Log.v(LOG_TAG, "cursor +++ " + String.valueOf(id));
       // Log.v("MainActivity", sort + " | " + sortType);
        Log.v("MainActivity", "SORT " + sort + " this.sortType " + this.sortType);
        if(sort != null && !sort.equals(this.sortType)){


          //  Log.v("MainActivity", "CURSOR sort != null && !sort.equals(this.sortType)");
            this.mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if(null != mainActivityFragment){
                Log.v("MainActivity", "CURSOR null != mainActivityFragment ");

                mainActivityFragment.onSortChange();
            }


            //NOT NEEDED HERE, WE DON'T NEED TO UPDATE THE DETAIL JUST BASED ON THE SORT CHANGE

            this.detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if(null != detailActivityFragment){
                //Log.v(LOG_TAG, "CURSOR ON RESUME detailActivityFragment" );
                 //TODO: CHANGE THIS ID TO THE ONE THAT'S ONCLICK
                //Log.v(LOG_TAG, "CURSOR onSortChange " + " ID VALUE " + String.valueOf(id));

                //Log.v("MainActivity", "CURSOR !detailActivityFragment.getLoaderManager().hasRunningLoaders()" + String.valueOf(id));
                //Cursor cursor =

                //Log.v("MainActivity", "CURSOR String.valueOf(id) " + String.valueOf(id));

                Log.v("MainActivity",  "TEST onResume " + String.valueOf(id));
                detailActivityFragment.onIDChange(id);
            }
        }
        this.sortType = sort;
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void onShareAction(){
        // Create the share Intent
        //String playStoreLink = "https://play.google.com/store/apps/details?id=" + getPackageName();

        Intent shareIntent = new Intent();//ShareCompat.IntentBuilder.from(this).setType("text/plain").setText("").getIntent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        shareIntent.setType("text/plain");

        // Set the share Intent

        Log.v("MainActivity" , "onShareAction");
        if (shareActionProvider != null) {
            Log.v("MainActivity" , "onShareAction NOT NULL");
            shareActionProvider.setShareIntent(shareIntent);
        }
        Log.v("MainActivity", "onShareAction POST EXEC");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mTwoPane){
            getMenuInflater().inflate(R.menu.detail, menu);
            MenuItem menuItem = menu.findItem(R.id.menu_item_share);
            shareActionProvider = new ShareActionProvider(this);
            MenuItemCompat.setActionProvider(menuItem, shareActionProvider);

            if (shareActionProvider != null){
                onShareAction();
            }
        }else {
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mTwoPane){
                 //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings){
                //Log.v("HERE ", "launching Settings 2");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            if(id == R.id.menu_item_share){
                onShareAction();
            }
        }else {

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                // Log.v("HERE ", "launching Settings 1");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {

        Log.v(LOG_TAG, "Callback onItemSelected " + movieUri.toString());
        if (mTwoPane) {
            Log.v(LOG_TAG, "Callback onItemSelected " + "TWO PANE");
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, movieUri);
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);
            Log.v(LOG_TAG, "TWO PANE " + DetailActivityFragment.DETAIL_URI + " " + movieUri);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment, MOVIEFRAGMENT_TAG).commit();
            this.id = Integer.valueOf(movieUri.getPathSegments().get(1));
        }else{
            Log.v(LOG_TAG, "Callback onItemSelected " + "ONE PANE " + movieUri.toString());
            Intent intent = new Intent(this, DetailActivity.class).setData(movieUri);
            startActivity(intent);
        }
    }

    @Override
    public void onFirstLoad(int id) {
        this.id = id;
        DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        if(null != detailActivityFragment){
            //Log.v(LOG_TAG, "CURSOR ON RESUME detailActivityFragment" );
            //TODO: CHANGE THIS ID TO THE ONE THAT'S ONCLICK
            //Log.v(LOG_TAG, "CURSOR onSortChange " + " ID VALUE " + String.valueOf(id));

            //Log.v("MainActivity", "CURSOR !detailActivityFragment.getLoaderManager().hasRunningLoaders()" + String.valueOf(id));
            //Cursor cursor =

            //Log.v("MainActivity", "CURSOR String.valueOf(id) " + String.valueOf(id));

            Log.v("MainActivity", "TEST onFirstLoad " + String.valueOf(id));
            detailActivityFragment.onIDChange(id);
        }else{
            Log.v("MainActivity", "TEST onFirstLoad ELSE" + String.valueOf(id));
        }

    }

    @Override
    public void shareData(String str) {
        this.shareMessage = str;
        Log.v("MainActivity SHARE ", shareMessage);
    }
/*@Override
    public void onFirstLoad(Uri movieUri) {

        Log.v(LOG_TAG, "Callback onFirstLoad " + movieUri.toString());
        if(mTwoPane){
            Log.v(LOG_TAG, "Callback onFirstLoad " + "TWO PANE");
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
    }*/
}