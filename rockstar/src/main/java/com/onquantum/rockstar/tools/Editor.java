package com.onquantum.rockstar.tools;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 6/28/14.
 */
public class Editor extends Activity {

    СomposerSurfaceView composerSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chord_book_layout);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

        composerSurfaceView = (СomposerSurfaceView)findViewById(R.id.chordBookSurfaceView);
    }
}
