package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.guitars.GuitarView;


/**
 * Created by saiber on 22.02.14.
 */
public class TappingGuitarActivity extends Activity {
    private Context context;
    GuitarView guitarView;
    Handler handler;
    RelativeLayout relativeLayout;
    private View decorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        //decorView = getWindow().getDecorView();

        setContentView(R.layout.guitar_layout);
        relativeLayout = (RelativeLayout)findViewById(R.id.container);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg){
                guitarView = new GuitarView(context);
                relativeLayout.addView(guitarView);
            };
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                handler.sendEmptyMessage(0);
            }
        }).start();


        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView)).setTypeface(typeface);

        ((ImageButton)this.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
