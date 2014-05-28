package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.onquantum.rockstar.*;


/**
 * Created by saiber on 07.03.14.
 */
public class HelpActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.label)).setTypeface(typeface);
    }
}
