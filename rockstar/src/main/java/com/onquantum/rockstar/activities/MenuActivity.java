package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import java.io.File;

/**
 * Created by saiber on 17.03.14.
 */
public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        Typeface titleFont = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.label)).setTypeface(titleFont);
    }

    private String get_cache_path(){
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),getApplication().getPackageName());
        }else {
            cacheDir = getCacheDir();
        }

        if(!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }
}
