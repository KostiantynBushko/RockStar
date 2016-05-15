package com.onquantum.rockstar.activities;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.fragments.LoadLocalTabFragment;
import com.onquantum.rockstar.fragments.LoadRemoteTabFragment;
import com.onquantum.rockstar.list_adapter.TabItemAdapter;
import com.onquantum.rockstar.services.GetTablaturesList;
import com.onquantum.rockstar.tabulature.SimpleTab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

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

    // fragment implementation
    private ImageButton imageButtonHome = null;
    private ImageButton imageButtonRemote = null;
    private LoadLocalTabFragment loadLocalTabFragment = null;
    private LoadRemoteTabFragment loadRemoteTabFragment = null;
    private RelativeLayout fragmentContainer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.load_tabulature);
        setContentView(R.layout.load_tabulature_layout);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/BaroqueScript.ttf");
        ((TextView) this.findViewById(R.id.textView0)).setTypeface(typeface);

        //listView = (ListView)findViewById(R.id.pentatonicList);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // fragment implementation
        if ((this.fragmentContainer = (RelativeLayout) this.findViewById(R.id.fragment_container)) != null) {
            this.imageButtonHome = (ImageButton)this.findViewById(R.id.button);
            this.imageButtonRemote = (ImageButton)this.findViewById(R.id.button2);

            this.loadLocalTabFragment = new LoadLocalTabFragment();
            this.loadRemoteTabFragment = new LoadRemoteTabFragment();

            this.imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadTabActivity.this.imageButtonRemote.setSelected(false);
                    v.setSelected(true);
                    FragmentTransaction fragmentTransaction = LoadTabActivity.this.getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, LoadTabActivity.this.loadLocalTabFragment);
                    fragmentTransaction.commit();
                }
            });
            this.imageButtonRemote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadTabActivity.this.imageButtonHome.setSelected(false);
                    v.setSelected(true);
                    FragmentTransaction fragmentTransaction = LoadTabActivity.this.getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, LoadTabActivity.this.loadRemoteTabFragment);
                    fragmentTransaction.commit();

                }
            });
            // default
            this.imageButtonHome.setSelected(true);
            this.imageButtonRemote.setSelected(false);
            FragmentTransaction fragmentTransaction = LoadTabActivity.this.getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, LoadTabActivity.this.loadLocalTabFragment);
            fragmentTransaction.commit();
        }
    }


    /*@Override
    public void onResume() {
        super.onResume();
        listItems = new ArrayList<>();
        File file = new File(FileSystem.GetTabsFilesPath());
        if(!file.exists())
            return;
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            Log.i("info",list[i]);
            HashMap<String,Object>item = new HashMap<>();
            item.put(TabItemAdapter.NAME,list[i]);
            item.put(TabItemAdapter.SHORT_NAME,list[i].subSequence(0,list[i].lastIndexOf(".")));
            item.put(TabItemAdapter.AUTHOR, SimpleTab.GetTabAuthor(FileSystem.GetTabsFilesPath() + "/" + list[i]));
            listItems.add(item);
        }

        TabItemAdapter simpleAdapter = new TabItemAdapter(this, R.layout.item_tabs, listItems, new TabItemAdapter.TabItemAdapterInterface() {
            @Override
            public void OnClickMoreButton(int position) {
                Log.i("info","MORE BUTTON POSITION = " + position);
            }
            @Override
            public void OnClickDeleteButton(final int position) {
                Log.i("info","MORE BUTTON DELETE = " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoadTabActivity.this);
                builder.setIcon(R.drawable.ic_delete_white_48dp);
                builder.setTitle("Delete tab");
                builder.setMessage("Do you want to delete this tab");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileSystem.DeleteTabFile(listItems.get(position).get(TabItemAdapter.NAME).toString());
                        listItems.remove(position);
                        listView.invalidateViews();
                    }
                });
                builder.create().show();
            }

            @Override
            public void OnClickEditButton(int position) {
                String name = (String)listItems.get(position).get(TabItemAdapter.SHORT_NAME);
                Intent intent = new Intent();
                intent.putExtra("fileName",name);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void OnClickShareButton(int position) {
                File file = FileSystem.CacheFile(new File(FileSystem.GetTabsFilesPath()+ "/" + listItems.get(position).get(TabItemAdapter.NAME)));
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("text/*");
                startActivity(Intent.createChooser(intent,"Share Tabs"));
            }
        });
        listView.setAdapter(simpleAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView)view.findViewById(R.id.tabName)).getText().toString();
                Intent intent = new Intent();
                intent.putExtra("fileName",name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }*/
}
