package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 3/21/16.
 */
public class LoadingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading_activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String className = intent.getStringExtra("activity");
        Log.i("info","Class name = " + className);
        startActivity(new Intent(this, PentatonicEditorActivity.class));
        finish();
    }
}
