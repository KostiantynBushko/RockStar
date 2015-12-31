package com.onquantum.rockstar.activities;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 12/29/15.
 */
public class LoadTabActivity extends Activity {

    private ListView listView;
    private List<HashMap<String, Object>> listItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading_pentatonic);

        listView = (ListView)findViewById(R.id.pentatonicList);
        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(FileSystem.GetTabsFilesPath());
        if(!file.exists())
            return;
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            Log.i("info",list[i]);
            HashMap<String,Object>item = new HashMap<>();
            item.put("file_name",list[i]);
            listItems.add(item);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,listItems,
                R.layout.item_pentatonic,
                new String[]{"file_name"},
                new int[]{R.id.textView1}
        );
        listView.setAdapter(simpleAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView)view.findViewById(R.id.textView1)).getText().toString();
                Intent intent = new Intent();
                intent.putExtra("fileName",name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
