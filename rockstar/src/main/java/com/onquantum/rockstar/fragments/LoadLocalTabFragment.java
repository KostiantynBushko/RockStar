package com.onquantum.rockstar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.list_adapter.TabItemAdapter;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 4/3/16.
 */
public class LoadLocalTabFragment extends Fragment {

    private ListView listView;
    private List<HashMap<String, Object>> listItems = new ArrayList<>();
    private TextView textLabel = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("info"," - Fragment LoadLocalTabFragment : onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        //Log.i("info"," - Fragment LoadLocalTabFragment : onCreateView");
        View view = layoutInflater.inflate(R.layout.local_tab_layout, viewGroup, false);
        this.listView = (ListView)view.findViewById(R.id.pentatonicList);
        this.textLabel = (TextView)view.findViewById(R.id.textView17);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.i("info"," - Fragment LoadLocalTabFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onStart");
    }

    @Override
    public void onResume() {
        //Log.i("info"," - Fragment LoadLocalTabFragment : onPause");
        super.onResume();
        updateTabList();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.i("info"," - Fragment LoadLocalTabFragment : onDetach");
    }

    private void updateTabList() {
        listItems = new ArrayList<>();
        File file = new File(FileSystem.GetTabsFilesPath());
        if(!file.exists())
            return;
        if(file.list().length == 0) {
            return;
        }

        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            Log.i("info",list[i]);
            HashMap<String,Object>item = new HashMap<>();
            item.put(TabItemAdapter.NAME,list[i]);
            item.put(TabItemAdapter.SHORT_NAME,list[i].subSequence(0,list[i].lastIndexOf(".")));
            item.put(TabItemAdapter.AUTHOR, SimpleTab.GetTabAuthor(FileSystem.GetTabsFilesPath() + "/" + list[i]));
            listItems.add(item);
        }


        if(this.listItems.size() > 0) {
            this.textLabel.setVisibility(View.GONE);
            this.listView.setVisibility(View.VISIBLE);
            TabItemAdapter simpleAdapter = new TabItemAdapter(getActivity(), R.layout.item_tabs, listItems, new TabItemAdapter.TabItemAdapterInterface() {
                @Override
                public void OnClickMoreButton(int position) {}
                @Override
                public void OnClickDeleteButton(final int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            updateTabList();
                        }
                    });
                    builder.create().show();
                }

                @Override
                public void OnClickEditButton(int position) {
                    String name = (String)listItems.get(position).get(TabItemAdapter.SHORT_NAME);
                    Intent intent = new Intent();
                    intent.putExtra("fileName",name);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
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
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
        } else {
            this.textLabel.setVisibility(View.VISIBLE);
            this.listView.setVisibility(View.GONE);
        }
    }

}
