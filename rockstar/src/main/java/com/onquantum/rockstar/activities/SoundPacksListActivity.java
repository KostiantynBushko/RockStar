package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;
import com.onquantum.rockstar.list_adapter.SoundPackListItemAdapter;
import com.onquantum.rockstar.services.UpdateGuitarsIconService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Admin on 7/7/15.
 */
public class SoundPacksListActivity extends Activity {

    private ListView soundPackList = null;
    private ArrayList<HashMap<String, Object>>listObjects = null;
    private Context context;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sound_packs_list_layout);
        context = this;

        soundPackList = (ListView)findViewById(R.id.soundPackList);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("info","SoundPacksListActivity BROADCAST RECEIVER : icon = " + intent.getStringExtra(DBGuitarTable.ICON) + " ID = " + intent.getIntExtra(DBGuitarTable.ID, 0));
                String iconFile = intent.getStringExtra(DBGuitarTable.ICON);
                int id = intent.getIntExtra(DBGuitarTable.ID, 0) - 1;
                updateIcon(id,iconFile);
            }
        };
        IntentFilter intentFilter = new IntentFilter(UpdateGuitarsIconService.BROADCAST_COMPLETE_DOWNLOAD_ICON_FILE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listObjects = new ArrayList<HashMap<String, Object>>();
        List<GuitarEntity>guitarEntities = DBGuitarTable.GetAllGuitarsEntity(context);
        for (GuitarEntity guitarEntity : guitarEntities) {
            HashMap<String, Object>guitarItem = new HashMap<String, Object>();
            guitarItem.put(DBGuitarTable.ICON,soundPackIcon(guitarEntity.icon));
            guitarItem.put(DBGuitarTable.NAME,guitarEntity.name);
            guitarItem.put(DBGuitarTable.ID, new Integer(guitarEntity.id));
            listObjects.add(guitarItem);
        }
        SoundPackListItemAdapter simpleAdapter = new SoundPackListItemAdapter(this, listObjects, R.layout.item_sond_pack,
                new String[]{DBGuitarTable.NAME,DBGuitarTable.ICON}, new int[] {R.id.soundPackageName, R.id.soundPackIcon} );
        soundPackList.setAdapter(simpleAdapter);
        soundPackList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        soundPackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("info"," position = " + position + " id = " + id);
                HashMap<String, Object>item = listObjects.get(position);
                Intent intent = new Intent(context,SoundPackActivity.class);
                intent.putExtra(DBGuitarTable.ID, (Integer)item.get(DBGuitarTable.ID));
                context.startActivity(intent);
            }
        });

    }

    private Bitmap soundPackIcon(String iconFileName) {
        File iconFile = new File(FileSystem.GetIconPath(), iconFileName);
        Bitmap bitmap = null;
        if(iconFile.exists()) {
            bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }
        return bitmap;
    }

    private void updateIcon(int itemId, String iconFileName) {
        if(soundPackList == null || itemId < 0)
            return;
        File iconFile = new File(FileSystem.GetIconPath(), iconFileName);
        Bitmap bitmap = null;
        if(iconFile.exists()) {
            bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
            HashMap<String, Object> guitarItem = listObjects.get(itemId);
            guitarItem.put(DBGuitarTable.ICON, bitmap);

            View view = soundPackList.getChildAt(itemId - soundPackList.getFirstVisiblePosition());
            if(view != null) {
                if(iconFile.exists()) {
                    ImageView imageView = (ImageView)view.findViewById(R.id.soundPackIcon);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
