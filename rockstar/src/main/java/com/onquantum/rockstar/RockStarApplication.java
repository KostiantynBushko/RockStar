package com.onquantum.rockstar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.services.UpdateGuitarsService;
import com.onquantum.rockstar.services.UpdatePurchaseTable;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.io.File;
import java.util.List;

/**
 * Created by Admin on 7/19/15.
 */
public class RockStarApplication extends Application {

    private static Context context = null;
    private static boolean bpmPresent = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        FileSystem.GetRootPath();
        FileSystem.GetIconPath();

        startService(new Intent(context, UpdatePurchaseTable.class));
        startService(new Intent(context, UpdateGuitarsService.class));

        QSoundPool.getInstance().setContext(getApplicationContext());
        QSoundPool.getInstance().loadSound();
    }

    public static void SetBPMPresent() {
        bpmPresent = true;
    }
    public static boolean IsBPMPresent() {
        return bpmPresent;
    }

    public static Context getContext() {
        return context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}