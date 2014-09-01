package com.onquantum.rockstar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.onquantum.rockstar.activities.*;
import com.onquantum.rockstar.common.SwitchButton;
import com.onquantum.rockstar.tools.Editor;
import com.onquantum.rockstar.activities.GuitarSimulatorActivity;

public class MainActivity extends Activity {
    private Context context;
    private Button selectStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        context = this;

        ((Button)findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context , GuitarSimulatorActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.settingButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        SwitchButton distortionSwitcher = (SwitchButton)findViewById(R.id.switchButton);
        distortionSwitcher.Set(new Settings(context).getDistortion());
        distortionSwitcher.setOnSwitchListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitchChange(boolean isOn) {
                new Settings(context).setDistortion(isOn);
            }
        });


        Typeface titleFont = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.textView0)).setTypeface(titleFont);


        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Editor.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GuitarSimulatorActivity.class);
                intent.putExtra("TEST",true);
                startActivity(intent);
            }
        });
    }

}
