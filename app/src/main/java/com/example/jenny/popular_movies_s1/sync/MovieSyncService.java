package com.example.jenny.popular_movies_s1.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

/**
 * Created by Jenny on 11/14/2015.
 */
public class MovieSyncService extends Service{

    private static final Object mSyncAdapterLock = new Object();
    private static MovieSyncAdapter mMovieSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("MovieSyncService", "onCreate - MovieSyncService");
        synchronized (mSyncAdapterLock) {
            if (mMovieSyncAdapter == null) {
                mMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMovieSyncAdapter.getSyncAdapterBinder();
    }
}
