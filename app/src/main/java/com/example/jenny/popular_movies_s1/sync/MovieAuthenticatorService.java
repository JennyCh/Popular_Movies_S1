package com.example.jenny.popular_movies_s1.sync;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Jenny on 11/14/2015.
 */
public class MovieAuthenticatorService extends Service {

    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        movieAuthenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return movieAuthenticator.getIBinder();
    }
}
