package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 1/6/15.
 */
public class LoadingPentatonicActivity extends Activity {

    private static final String FILE_NAME = "file_name";
    private ListView listView;
    private ArrayList<HashMap<String,Object>> listObjects = null;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        setContentView(R.layout.loading_pentatonic);
        listView = (ListView)findViewById(R.id.pentatonicList);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        Log.i("info", "OnResume");
        listObjects = new ArrayList<HashMap<String, Object>>();
        String[] list;
        try {
            list = getAssets().list("pentatonic");
            for (String file : list) {
                Log.i("info","file : " + file);
                HashMap<String, Object>item = new HashMap<String, Object>();
                item.put(FILE_NAME,file);
                listObjects.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(this,listObjects, R.layout.item_pentatonic,
                    new String[]{FILE_NAME},
                    new int[]{R.id.textView1}
            );
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = ((TextView)view.findViewById(R.id.textView1)).getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("fileName",name);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }catch (IOException e) {
            e.printStackTrace();
        }
        super.onResume();
    }
}
