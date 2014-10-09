package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.onquantum.rockstar.guitars.GuitarInterface;
import com.onquantum.rockstar.guitars.GuitarAbstract;
import com.onquantum.rockstar.guitars.GuitarViewDefault;
import com.onquantum.rockstar.guitars.GuitarViewSlide;

import fragments.FragmentListener;
import fragments.SettingsFragment;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarSimulatorActivity extends Activity implements GuitarInterface, DialogSelectPentatonic.OnPentatonicSelectListener{

    private GuitarAbstract guitarSurfaceView;
    private ProgressBar progressBar;
    private RelativeLayout controlPanel;
    private Context context;
    private FretsSlider fretsSlider;
    private boolean isFragmentLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        if(new Settings(context).getSlide()) {
            setContentView(R.layout.guitar_surface_slide);
            guitarSurfaceView = (GuitarViewSlide)findViewById(R.id.guitarSurfaceView);
        }else{
            setContentView(R.layout.guitar_surface_deffault);
            guitarSurfaceView = (GuitarViewDefault)findViewById(R.id.guitarSurfaceView);
        }
        guitarSurfaceView.setOnSoundLoadedCompleteListener(new GuitarViewSlide.OnSoundLoadedCompleteListener() {
            @Override
            public void onSoundLoadedComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (new Settings(context).isFretsSliderVisible())
                            fretsSlider.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        fretsSlider = (FretsSlider)findViewById(R.id.fretsSlide);
        fretsSlider.setSliderWidth(new Settings(context).getFretNumbers());
        if (new Settings(context).isFretsSliderVisible())
            fretsSlider.setVisibility(View.INVISIBLE);
        fretsSlider.setOnSliderChangeListener(new FretsSlider.SliderChangeListener() {
            @Override
            public void onSlideButtonListener(int slide) {
                guitarSurfaceView.slideChange(slide);
            }
        });


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
        ((ImageButton)this.findViewById(R.id.button0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragmentLoaded)
                    return;
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.frameLayout,settingsFragment, SettingsFragment.SETTINGS_FRAGMENT);
                transaction.commit();

                if (settingsFragment != null){
                    Settings settings = new Settings(context);
                    settingsFragment.setSettingsInstance(settings);
                    guitarSurfaceView.setTouchEnable(false);
                    isFragmentLoaded = true;
                    settingsFragment.setFragmentListener(new FragmentListener() {
                        @Override
                        public void close() {
                            guitarSurfaceView.setTouchEnable(true);
                            isFragmentLoaded = false;
                        }
                    });
                    settings.setOnFretsNumberVisibleListener(new Settings.FretsNumberVisible() {
                        @Override
                        public void isFretsNumberVisible(boolean visibility) {
                            guitarSurfaceView.SetFretsNumberVisible(visibility);
                        }
                    });
                    settings.setShowTouchesListener(new Settings.ShowTouchesListener() {
                        @Override
                        public void showTouches(boolean visibility) {
                            guitarSurfaceView.SetShowTouchesVisible(visibility);
                        }
                    });
                    settings.setOnFretsSliderListener(new Settings.FretsSliderListener() {
                        @Override
                        public void showFretsSlider(boolean visibility) {
                            if (visibility)
                                fretsSlider.setVisibility(View.VISIBLE);
                            else
                                fretsSlider.setVisibility(View.GONE);
                            fretsSlider.invalidate();
                            guitarSurfaceView.SetFretsSliderVisible(visibility);
                        }
                    });
                }
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
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.alpha_up);
        controlPanel.startAnimation(animation);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        guitarSurfaceView.Stop();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
