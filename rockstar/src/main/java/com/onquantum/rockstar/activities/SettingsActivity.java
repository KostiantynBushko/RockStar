package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;

/**
 * Created by saiber on 03.03.14.
 */
public class SettingsActivity extends Activity {

    public static final int FRET_COUNT = 9;
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
        ((TextView) this.findViewById(R.id.textView)).setTypeface(typeFace);

        count = (TextView)this.findViewById(R.id.textView2);
        channelsSound = (TextView)this.findViewById(R.id.textView5);

        settings = new Settings(context);
        numberOfCount = settings.getFretNumbers();
        count.setText(Integer.toString(numberOfCount));
        channelsSound.setText(Integer.toString(settings.getSoundChannels()));

        ((Button)this.findViewById(R.id.minus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(count.getText().toString());
                if (number > 3) {
                    number--;
                    count.setText(Integer.toString(number).toString());
                    new Settings(context).setFretNumbers(number);
                }
            }
        });
        ((Button)this.findViewById(R.id.plus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(count.getText().toString());
                if (number < FRET_COUNT) {
                    number++;
                    count.setText(Integer.toString(number).toString());
                    new Settings(context).setFretNumbers(number);
                }
            }
        });
        ((Button)this.findViewById(R.id.minusChannels)).setOnClickListener(new View.OnClickListener() {
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
        ((Button)this.findViewById(R.id.plusChannels)).setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(context,TappingGuitarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    }
}
