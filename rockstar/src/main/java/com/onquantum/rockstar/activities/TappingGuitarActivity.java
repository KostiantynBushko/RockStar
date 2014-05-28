package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;

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
    /*private void RunDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Setting dialog")
                .setMessage("setting dialog...")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.cancel();
                    }
                }).show();
    }*/
}
