package com.onquantum.rockstar.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;
import com.onquantum.rockstar.list_adapter.SoundPackListItemAdapter;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.services.DownloadSoundPackage;
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

    private static int PICK_SOUND_PACK_ACTIVITY = 1;

    private ListView soundPackList = null;
    private int listViewCurrentPosition = 0;
    private ArrayList<HashMap<String, Object>>listObjects = null;
    private Context context;

    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver completeDownloadSoundPackReceiver;
    private int currentSelected = -1;
    private SoundPackListItemAdapter simpleAdapter = null;
    private MediaPlayer mediaPlayer = null;

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
                String iconFile = intent.getStringExtra(DBGuitarTable.ICON);
                int id = intent.getIntExtra(DBGuitarTable.ID, 0) - 1;
                updateIcon(id,iconFile);
            }
        };
        //IntentFilter intentFilter = new IntentFilter(UpdateGuitarsIconService.BROADCAST_COMPLETE_DOWNLOAD_ICON_FILE_ACTION);
        //registerReceiver(broadcastReceiver, intentFilter);

        completeDownloadSoundPackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int id = intent.getIntExtra(DBGuitarTable.ID, -1);
                //Log.i("info"," Complete download  id : " + id + " article : " + intent.getStringExtra(DBGuitarTable.ARTICLE));
                for (int i = 0; i < listObjects.size(); i++) {
                    long objectID = (Long)listObjects.get(i).get(DBGuitarTable.ID);
                    if (objectID == id) {
                        listObjects.get(i).put(DBGuitarTable.SOUND_PACK_AVAILABLE, new Boolean(true));
                        simpleAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        };
        IntentFilter completeDownloadIntentFilter = new IntentFilter(DownloadSoundPackage.BROADCAST_COMPLETE_DOWNLOAD_SOUND_PACKAGE_ACTION);
        registerReceiver(completeDownloadSoundPackReceiver, completeDownloadIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(broadcastReceiver);
        unregisterReceiver(completeDownloadSoundPackReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listObjects = null;
        if(listObjects == null) {
            listObjects = new ArrayList<HashMap<String, Object>>();
            List<GuitarEntity>guitarEntities = DBGuitarTable.GetAllGuitarsEntity(context);
            int count = 0;
            for (GuitarEntity guitarEntity : guitarEntities) {
                HashMap<String, Object>guitarItem = new HashMap<String, Object>();
                guitarItem.put(DBGuitarTable.ICON,soundPackIcon(guitarEntity.id, guitarEntity.icon));
                guitarItem.put(DBGuitarTable.NAME,guitarEntity.name);
                guitarItem.put(DBGuitarTable.ID, new Long(guitarEntity.id));
                guitarItem.put(DBGuitarTable.IS_ACTIVE, new Boolean(guitarEntity.is_active));
                guitarItem.put("index", new Integer(count));
                if (guitarEntity.isSoundPackAvailable()) {
                    guitarItem.put(DBGuitarTable.SOUND_PACK_AVAILABLE, new Boolean(true));
                } else {
                    guitarItem.put(DBGuitarTable.SOUND_PACK_AVAILABLE, new Boolean(false));
                }
                listObjects.add(guitarItem);
                if (guitarEntity.is_active) {
                    currentSelected = count;
                }
                count ++;
            }
            simpleAdapter = new SoundPackListItemAdapter(this, R.layout.item_sond_pack, listObjects, new SoundPackListItemAdapter.SoundPackItemInterface() {
                @Override
                public void OnSwitchSoundPack(int position) {
                    listObjects.get(currentSelected).put(DBGuitarTable.IS_ACTIVE,new Boolean(false));
                    listObjects.get(position).put(DBGuitarTable.IS_ACTIVE, new Boolean(true));

                    currentSelected = position;
                    simpleAdapter.notifyDataSetChanged();
                    if (mediaPlayer != null){
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    mediaPlayer = MediaPlayer.create(SoundPacksListActivity.this, R.raw.foot_switch);
                    mediaPlayer.start();


                    DBGuitarTable.SetActiveGuitar(getApplicationContext(), (Long) listObjects.get(currentSelected).get(DBGuitarTable.ID));
                    QSoundPool.getInstance().releaseSoundPool();
                    QSoundPool.getInstance().loadSound();
                }
            });

            soundPackList.setAdapter(simpleAdapter);
            soundPackList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            soundPackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.i("info"," position = " + position + " id = " + id);
                    HashMap<String, Object>item = listObjects.get(position);
                    Intent intent = new Intent(context,SoundPackActivity.class);
                    intent.putExtra(DBGuitarTable.ID, (Long)item.get(DBGuitarTable.ID));
                    intent.putExtra("position",position);
                    startActivityForResult(intent,PICK_SOUND_PACK_ACTIVITY);
                    listViewCurrentPosition = position;
                }
            });
        }

        if (simpleAdapter != null)
            simpleAdapter.notifyDataSetChanged();

        soundPackList.setSelection(listViewCurrentPosition);

        ((ImageButton)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_SOUND_PACK_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                boolean is_active = intent.getBooleanExtra(DBGuitarTable.IS_ACTIVE, false);
                int id = intent.getIntExtra(DBGuitarTable.ID, -1);
                int position = intent.getIntExtra("position",-1);
                if (id > 0 && is_active == true) {
                    listObjects.get(currentSelected).put(DBGuitarTable.IS_ACTIVE,!is_active);
                    listObjects.get(position).put(DBGuitarTable.IS_ACTIVE, is_active);
                    currentSelected = position;
                }
            }
        }
    }

    private Bitmap soundPackIcon(int id, String iconFileName) {
        /*File iconFile = new File(FileSystem.GetIconPath(), iconFileName);
        Bitmap bitmap = null;
        if(iconFile.exists()) {
            bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
        } else {
            //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            Intent intent = new Intent(this, UpdateGuitarsIconService.class);
            intent.putExtra(DBGuitarTable.ICON,iconFileName);
            intent.putExtra(DBGuitarTable.ID, id);
            startService(intent);
        }
        return bitmap;*/

        return BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_guitar_white);
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
