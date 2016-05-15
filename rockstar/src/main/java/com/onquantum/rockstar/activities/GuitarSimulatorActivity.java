package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.FretsSlider;
import com.onquantum.rockstar.dialogs.DialogSelectPentatonic;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.guitar.GuitarInterface;
import com.onquantum.rockstar.guitar.GuitarAbstractView;
import com.onquantum.rockstar.guitar.GuitarDefaultView;
import com.onquantum.rockstar.guitar.GuitarSlideView;
import com.onquantum.rockstar.sequencer.QSoundPool;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarSimulatorActivity extends Activity implements GuitarInterface, DialogSelectPentatonic.OnPentatonicSelectListener{

    private GuitarAbstractView guitarSurfaceView;
    private ProgressBar progressBar;
    private TextView progressText;
    private RelativeLayout controlPanel;
    private Context context;
    private FretsSlider fretsSlider;
    private Settings settings;
    private boolean isSlide;
    private boolean isFretSlider = false;
    private int fretNumber;
    private boolean isLoaded = false;
    private TextView packageName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        settings = new Settings(context);
        fretNumber = settings.getFretNumbers();
        isSlide = settings.getSlide();
        isFretSlider = settings.isFretsSliderVisible();
    }


    private boolean isPentatonicLoad = false;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null)
            return;

        // Disable show notes
        new Settings(this).setShowNotes(false);

        String fileName = intent.getStringExtra("fileName");
        guitarSurfaceView.LoadTabsFile(fileName);
        isPentatonicLoad = true;
    }

    @Override
    public void onPentatonicSelect(String fileName) {
        guitarSurfaceView.LoadTabsFile(fileName);
    }

    @Override
    public void onPentatonicSuccessLoaded(String name) {
        controlPanel.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textView1)).setText(name);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.alpha_up);
        controlPanel.startAnimation(animation);
    }

    @Override
    public void onResume() {
        if (isPentatonicLoad) {
            super.onResume();
            isPentatonicLoad = false;
            return;
        }

        if(settings.getSlide()) {
            setContentView(R.layout.guitar_surface_slide);
            guitarSurfaceView = (GuitarSlideView)findViewById(R.id.guitarSurfaceView);
        }else{
            setContentView(R.layout.guitar_surface_deffault);
            guitarSurfaceView = (GuitarDefaultView)findViewById(R.id.guitarSurfaceView);
        }
        progressBar = (ProgressBar)findViewById(R.id.loading_spinner);
        progressText = (TextView)findViewById(R.id.progressText);
        packageName = (TextView)findViewById(R.id.soundPackageName);
        packageName.setText(DBGuitarTable.GetCurrentActivePackageName(this) + " guitar");

        QSoundPool.getInstance().setOnProgressUpdate(new QSoundPool.OnProgressUpdate() {
            @Override
            public void progressUpdate(int progress) {
                progressText.setText(new String(Integer.toString(progress) + "%"));
            }
        });
        guitarSurfaceView.setOnSoundLoadedCompleteListener(new GuitarSlideView.OnSoundLoadedCompleteListener() {
            @Override
            public void onSoundLoadedComplete() {
                ((TextView)findViewById(R.id.helpText)).setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                packageName.setVisibility(View.GONE);
                if (new Settings(context).isFretsSliderVisible()) {
                    fretsSlider.setVisible(true);
                    fretsSlider.setVisibility(View.VISIBLE);
                }
            }
        });
        fretsSlider = (FretsSlider)findViewById(R.id.fretsSlide);
        if(settings.isFretsSliderVisible()) {
            int fretSliderWidth = new Settings(context).getFretNumbers();
            if(new Settings(context).getOpenStringStatus())
                fretSliderWidth -= 1;
            fretsSlider.setSliderWidth(fretSliderWidth);
            fretsSlider.setVisibility(View.INVISIBLE);
            fretsSlider.setOnSliderChangeListener(new FretsSlider.SliderChangeListener() {
                @Override
                public void onSlideButtonListener(int slide) {
                    guitarSurfaceView.slideChange(slide);
                }
            });
        }
        guitarSurfaceView.Start();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

        controlPanel = (RelativeLayout)findViewById(R.id.playPentatonicPanel);

        ((ImageButton) this.findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
        ((ImageButton)this.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoadTabActivity.class);
                startActivityForResult(intent, 1);
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
        ((ImageButton)this.findViewById(R.id.button0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FretsSettingsActivity.class);
                startActivity(intent);
            }
        });
        boolean visibility = settings.isFretsSliderVisible();
        if (visibility) {
            fretsSlider.setVisibility(View.VISIBLE);
        } else {
            fretsSlider.setVisibility(View.GONE);
        }
        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        guitarSurfaceView.SetShowTouchesVisible(settings.isTouchesVisible());
        guitarSurfaceView.SetFretsNumberVisible(settings.isFretsNumberVisible());
        super.onResume();
    }


    @Override
    public void onDestroy() {
        //stopWrapProgress = true;
        guitarSurfaceView.Stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
