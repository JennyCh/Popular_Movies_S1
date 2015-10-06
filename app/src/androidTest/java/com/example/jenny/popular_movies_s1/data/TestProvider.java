package com.example.jenny.popular_movies_s1.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Jenny on 10/6/2015.
 */
public class TestProvider extends AndroidTestCase{

    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try{
            ProviderInfo providerInfo = pm.getProviderInfo(componentName,0);
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                        " instead of authority: " + MovieContract.CONTENT_AUTHORITY, providerInfo.authority, MovieContract.CONTENT_AUTHORITY);

        }catch (PackageManager.NameNotFoundException e){
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    public void testGetType(){
        String type = mContext.getContentResolver().getType(MovieContract.Movie.CONTENT_URI);

        assertEquals("Error: the Movie CONTENT_URI should return Movie.CONTENT_DIR_TYPE", MovieContract.Movie.CONTENT_DIR_TYPE, type);

        int testID = 12345;
        type = mContext.getContentResolver().getType(MovieContract.Movie.buildMovieID(testID));
        Log.v("Type", "MovieContract.Movie.CONTENT_ITEM_TYPE");
        Log.v("Type", type.toString());
        assertEquals("Error: the Movie CONTENT_URI with id should return Movie.CONTENT_ITEM_TYPE", MovieContract.Movie.CONTENT_ITEM_TYPE, type);
    }
}
