package com.onquantum.rockstar;

import android.app.Application;
import android.content.Context;

/**
 * Created by Admin on 7/19/15.
 */
public class RockStarApplication extends Application {

    private static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}