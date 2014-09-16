package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * Created by saiber on 07.03.14.
 */
public class HelpActivity extends Activity {

    private static final String ICON = "icon";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/BaroqueScript.ttf");
        ((TextView)this.findViewById(R.id.label)).setTypeface(typeface);

        listView = (ListView)findViewById(R.id.listView);
        listView.setDividerHeight(10);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<HashMap<String, Object>>listItems = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object>item = new HashMap<String, Object>();

        item.put(ICON,R.drawable.glissando);
        item.put(TITLE,getResources().getString(R.string.glissando));
        item.put(TEXT,getResources().getString(R.string.glissando_help));
        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put(ICON,R.drawable.sound);
        item.put(TITLE,getResources().getString(R.string.sound_channels));
        item.put(TEXT,getResources().getString(R.string.sound_channels_help));
        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put(ICON,R.drawable.neck);
        item.put(TITLE,getResources().getString(R.string.number_of_bar));
        item.put(TEXT,getResources().getString(R.string.number_of_bar_help));
        listItems.add(item);

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                listItems,
                R.layout.item_help,
                new String[]{ICON,TITLE,TEXT},
                new int[]{R.id.imageView,R.id.textView1,R.id.textView2}
        );

        listView.setAdapter(simpleAdapter);

    }
}
