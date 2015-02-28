package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.SwitchButton;

/**
 * Created by Admin on 12/25/14.
 */
public class FretsSettingsActivity extends Activity {

    private Settings settings;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        setContentView(R.layout.frets_settings_layout);
        settings = new Settings(context);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(typeface);

        SwitchButton button1 = (SwitchButton)findViewById(R.id.showFretNumber);
        button1.Set(new Settings(context).isFretsNumberVisible());
        button1.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                settings.setFretNumberVisibility(isOn);
            }
        });

        SwitchButton button2 = (SwitchButton)findViewById(R.id.showNeckSlider);
        button2.Set(new Settings(context).isFretsSliderVisible());
        button2.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(final boolean isOn) {
                settings.setFretsSliderVisibility(isOn);
            }
        });

        SwitchButton button3 = (SwitchButton)findViewById(R.id.showTouches);
        button3.Set(new Settings(context).isTouchesVisible());
        button3.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                settings.setTouchesVisibility(isOn);
            }
        });
    }
}
