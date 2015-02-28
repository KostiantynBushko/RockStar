package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.CounterView;
import com.onquantum.rockstar.common.SwitchButton;

/**
 * Created by onquantum on 03.03.14.
 */
public class SettingsActivity extends Activity {

    public static final int MAX_FRET = Settings.MAX_FRET;
    private Settings settings;
    private int channels = 0;
    private int frets = 0;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_layout);
        context = this;

        Typeface typeFace = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeFace);

        settings = new Settings(context);
        if(settings.getSlide())
            ((RelativeLayout)findViewById(R.id.relativeLayout3)).setVisibility(View.GONE);

        SwitchButton slideSwitcher = (SwitchButton)findViewById(R.id.btnGlissando);
        slideSwitcher.Set(new Settings(context).getSlide());
        slideSwitcher.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                new Settings(context).setSlide(isOn);
                if (isOn)
                    ((RelativeLayout)findViewById(R.id.relativeLayout3)).setVisibility(View.GONE);
                else
                    ((RelativeLayout)findViewById(R.id.relativeLayout3)).setVisibility(View.VISIBLE);
            }
        });

        CounterView chanelCounter = (CounterView)findViewById(R.id.btnChannels);
        chanelCounter.setMaxMinValue(1, 6);
        chanelCounter.setCountValue(settings.getSoundChannels());
        chanelCounter.setOnCountChangeValue(new CounterView.OnCountChangeValue() {
            @Override
            public void onCountChangeValue(int countValue) {
                settings.setSoundChannels(countValue);
            }
        });

        CounterView fretsCounter = (CounterView)findViewById(R.id.btnFrets);
        fretsCounter.setMaxMinValue(5,13);
        fretsCounter.setCountValue(settings.getFretNumbers());
        fretsCounter.setOnCountChangeValue(new CounterView.OnCountChangeValue() {
            @Override
            public void onCountChangeValue(int countValue) {
                settings.setFretNumbers(countValue);
            }
        });
    }
}
