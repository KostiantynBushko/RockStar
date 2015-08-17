package com.onquantum.rockstar.pentatonic_editor;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onquantum.rockstar.R;

/**
 * Created by Admin on 8/10/15.
 */
public class PentatonicEditorActivity extends Activity{

    NotePanelSurfaceView notePanelSurfaceView;
    PentatonicEditorSurfaceView pentatonicEditorSurfaceView;
    BarSelectView barSelectView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pentatonic_editor);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(typeface);


        notePanelSurfaceView = (NotePanelSurfaceView)findViewById(R.id.noteSurfaceView);
        pentatonicEditorSurfaceView = (PentatonicEditorSurfaceView)findViewById(R.id.chordBookSurfaceView);
        barSelectView = (BarSelectView)findViewById(R.id.barSelect);
        barSelectView.SetOnBarSelectListener(new BarSelectView.OnBarSelectListener() {
            @Override
            public void onBarSelect(int barSelected) {
                Log.i("info"," BAR SELECTTED : " + barSelected);
            }
        });
    }
}
