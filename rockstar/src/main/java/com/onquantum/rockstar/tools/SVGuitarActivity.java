package com.onquantum.rockstar.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.activities.SettingsActivity;
import com.onquantum.rockstar.dialogs.DialogSelectPentatonic;
import com.onquantum.rockstar.guitars.GuitarInterface;

/**
 * Created by Admin on 8/16/14.
 */
public class SVGuitarActivity extends Activity implements GuitarInterface, DialogSelectPentatonic.OnPentatonicSelectListener{

    private GuitarSurfaceView guitarSurfaceView;
    private ProgressBar progressBar;
    private RelativeLayout controlPanel;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.guitar_surface_view);
        context = getApplicationContext();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

        progressBar = (ProgressBar)findViewById(R.id.loading_spinner);
        controlPanel = (RelativeLayout)findViewById(R.id.playPentatonicPanel);

        ((ImageButton) this.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((ImageButton)this.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSelectPentatonic dialog = new DialogSelectPentatonic();
                dialog.show(getFragmentManager(),"DialogSelectPentatonic");
            }
        });
        ((ImageButton)this.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPanel.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.alpha_down);
                controlPanel.startAnimation(animation);
                guitarSurfaceView.ClosePlayPentatonic();
            }
        });

        guitarSurfaceView = (GuitarSurfaceView)findViewById(R.id.guitarSurfaceView);
        guitarSurfaceView.setOnSoundLoadedCompleteListener(new GuitarSurfaceView.OnSoundLoadedCompleteListener() {
            @Override
            public void onSoundLoadedComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public void onPentatonicSelect(String fileName) {
        guitarSurfaceView.LoadPentatonicFile(fileName);
    }

    @Override
    public void onPentatonicSuccessLoaded(String name) {
        controlPanel.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textView1)).setText(name);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_up);
        controlPanel.startAnimation(animation);
    }
}
