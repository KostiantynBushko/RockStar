package com.onquantum.rockstar;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.onquantum.rockstar.file_system.FileSystem;

import java.io.File;

/**
 * Created by Admin on 7/19/15.
 */
public class RockStarApplication extends Application {

    private static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        FileSystem.GetRootPath();
        FileSystem.GetIconPath();
    }

    public static Context getContext() {
        return context;
    }
}