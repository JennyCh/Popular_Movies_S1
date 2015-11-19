package com.example.jenny.popular_movies_s1;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import java.security.Provider;

public class DetailActivity extends ActionBarActivity implements DetailActivityFragment.Callback {

    private Intent mShareIntent;
    private ShareActionProvider shareActionProvider;
    private String shareLink;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "#CodeAndroid");

        if(savedInstanceState == null){

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        //ShareActionProvider provider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        shareActionProvider = new ShareActionProvider(this);
        MenuItemCompat.setActionProvider(menuItem, shareActionProvider);

        if (shareActionProvider != null){
            onShareAction();
        }

       /* MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider actionProvider = (ShareActionProvider) item.getActionProvider();
       // String shareText = URL_TO_SHARE;
        Intent shareIntent = ShareCompat.IntentBuilder.from(this).setType("text/plain").setText("#CodeAndroid").getIntent();
        actionProvider.setShareIntent(shareIntent);*/

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
            //Log.v("HERE ", "launching Settings 2");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id == R.id.menu_item_share){
            onShareAction();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onShareAction(){
        // Create the share Intent
        //String playStoreLink = "https://play.google.com/store/apps/details?id=" + getPackageName();

        Intent shareIntent = new Intent();//ShareCompat.IntentBuilder.from(this).setType("text/plain").setText("").getIntent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //  Log.v("DetailActivity", this.shareLink);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
        shareIntent.setType("text/plain");

        // Set the share Intent

        Log.v("DetailActivity" , "onShareAction");
        if (shareActionProvider != null) {
            Log.v("DetailActivity" , "onShareAction NOT NULL");
            shareActionProvider.setShareIntent(shareIntent);
        }
        Log.v("DetailActivity" , "onShareAction POST EXEC");
    }

    @Override
    public void shareData(String str) {
        this.shareLink = str;

        Log.v("DetailActivity", str);
    }
}