package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.SwitchButton;
import com.onquantum.rockstar.tools.GuitarSimulatorSurfaceActivity;

/**
 * Created by onquantum on 03.03.14.
 */
public class SettingsActivity extends Activity {

    public static final int MAX_FRET = Settings.MAX_FRET;
    private TextView count = null;
    private TextView channelsSound = null;
    private Settings settings;

    private int numberOfCount = 0;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        context = this;

        Typeface typeFace = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeFace);

        //typeFace = Typeface.createFromAsset(getAssets(),"font/bgothm.ttf");
        //((TextView)this.findViewById(R.id.textView2)).setTypeface(typeFace);
        //((TextView)this.findViewById(R.id.textView3)).setTypeface(typeFace);
        //((TextView)this.findViewById(R.id.textView4)).setTypeface(typeFace);
        //((TextView)this.findViewById(R.id.textView2)).setTypeface(typeFace);

        count = (TextView)this.findViewById(R.id.textView5);
        channelsSound = (TextView)this.findViewById(R.id.textView1);

        settings = new Settings(context);
        numberOfCount = settings.getFretNumbers();
        count.setText(Integer.toString(numberOfCount));
        channelsSound.setText(Integer.toString(settings.getSoundChannels()));

        // Frets
        ((Button)this.findViewById(R.id.minusFret)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(count.getText().toString());
                if (number > 5) {
                    number--;
                    count.setText(Integer.toString(number).toString());
                    new Settings(context).setFretNumbers(number);
                }
            }
        });
        ((Button)this.findViewById(R.id.plusFret)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(count.getText().toString());
                if (number < MAX_FRET) {
                    number++;
                    count.setText(Integer.toString(number).toString());
                    new Settings(context).setFretNumbers(number);
                }
            }
        });

        // Channels
        ((Button)this.findViewById(R.id.minus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(channelsSound.getText().toString());
                if(number > 1) {
                    number--;
                    channelsSound.setText(Integer.toString(number).toString());
                    settings.setSoundChannels(number);
                }
            }
        });
        ((Button)this.findViewById(R.id.plus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(channelsSound.getText().toString());
                if(number < 6) {
                    number++;
                    channelsSound.setText(Integer.toString(number).toString());
                    settings.setSoundChannels(number);
                }
            }
        });

        //Start play action
        ((Button)this.findViewById(R.id.startButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context , GuitarSimulatorSurfaceActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Action bar Help
        ((ImageButton)this.findViewById(R.id.help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HelpActivity.class);
                startActivity(intent);
            }
        });

        if(new Settings(context).getSlide())
            ((RelativeLayout)findViewById(R.id.relativeLayout1)).setVisibility(View.GONE);

        SwitchButton slideSwitcher = (SwitchButton)findViewById(R.id.slideSwitcher);
        slideSwitcher.Set(new Settings(context).getSlide());
        slideSwitcher.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                new Settings(context).setSlide(isOn);
                if (isOn)
                    ((RelativeLayout)findViewById(R.id.relativeLayout1)).setVisibility(View.GONE);
                else
                    ((RelativeLayout)findViewById(R.id.relativeLayout1)).setVisibility(View.VISIBLE);
            }
        });

        SwitchButton distortionSwitcher = (SwitchButton)findViewById(R.id.slideSwitcher1);
        distortionSwitcher.Set(new Settings(context).getDistortion());
        distortionSwitcher.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                new Settings(context).setDistortion(isOn);
            }
        });
    }
}
