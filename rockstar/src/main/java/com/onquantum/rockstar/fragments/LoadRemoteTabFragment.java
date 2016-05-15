package com.onquantum.rockstar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.RockStarApplication;
import com.onquantum.rockstar.list_adapter.RemoteTabListAdapter;
import com.onquantum.rockstar.services.DownloadTabFile;
import com.onquantum.rockstar.services.GetTablaturesList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 4/3/16.
 */
public class LoadRemoteTabFragment extends Fragment {

    private BroadcastReceiver broadcastReceiver = null;
    private BroadcastReceiver downloadFileBroadcastReceiver = null;

    private ListView listView = null;
    private TextView textLabel = null;

    private JSONArray tablatureJsonList = null;
    private List<HashMap<String, Object>> listItems = null;

    private ProgressDialog getTabListProgressDialog = null;
    private ProgressDialog downloadFileProgressDialog = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onCreateView");
        View view = layoutInflater.inflate(R.layout.remote_tab_layout, viewGroup, false);
        this.listView = (ListView)view.findViewById(R.id.listView);
        this.textLabel = (TextView)view.findViewById(R.id.textView17);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("info"," - Fragment LoadRemoteTabFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onStart");
    }

    @Override
    public void onResume() {
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onPause");
        super.onResume();

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    HideGetTabListProgressDialog();
                    tablatureJsonList = new JSONArray(intent.getStringExtra("tablature_list"));
                    updateTabList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        this.downloadFileBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HideDownloadProgressDialog();
                String fileName = intent.getStringExtra(DownloadTabFile.FILE_NAME);
                fileName = (String) fileName.subSequence(0,fileName.lastIndexOf("."));
                Intent resultIntent = new Intent();
                resultIntent.putExtra("fileName",fileName);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }
        };

        IntentFilter intentFilter = new IntentFilter(GetTablaturesList.BROADCAST_COMPLETE_OBTAIN_TABLATURE_LIST_ACTION);
        getActivity().registerReceiver(this.broadcastReceiver, intentFilter);

        IntentFilter downloadFileIntentFilter = new IntentFilter(DownloadTabFile.BROADCAST_COMPLETE_DOWNLOAD_TAB_FILE_ACTION);
        getActivity().registerReceiver(this.downloadFileBroadcastReceiver, downloadFileIntentFilter);

        if(!((RockStarApplication)getActivity().getApplication()).isNetworkConnected()) {
            this.textLabel.setText(getActivity().getResources().getString(R.string.no_internet_connection));
        } else {
            this.ShowGetTabListProgressDialog();
            Intent intent = new Intent(getActivity(), GetTablaturesList.class);
            intent.putExtra("from",0);
            intent.putExtra("limit",0);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onStop");
        getActivity().unregisterReceiver(this.broadcastReceiver);
        getActivity().unregisterReceiver(this.downloadFileBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.i("info"," - Fragment LoadRemoteTabFragment : onDetach");
    }

    private void ShowGetTabListProgressDialog() {
        if(this.getTabListProgressDialog != null) {
            this.getTabListProgressDialog.dismiss();
        }
        this.getTabListProgressDialog = new ProgressDialog(getActivity());
        this.getTabListProgressDialog.setCanceledOnTouchOutside(false);
        this.getTabListProgressDialog.setMessage("Get tablature list... please wait!");
        this.getTabListProgressDialog.show();
    }

    private void HideGetTabListProgressDialog() {
        if(this.getTabListProgressDialog != null) {
            this.getTabListProgressDialog.dismiss();
        }
        this.getTabListProgressDialog = null;
    }

    private void ShowDownloadFileProgressDialog(String tablatureName) {
        if (this.downloadFileProgressDialog != null) {
            this.downloadFileProgressDialog.dismiss();
        }
        this.downloadFileProgressDialog = new ProgressDialog(getActivity());
        this.downloadFileProgressDialog.setCanceledOnTouchOutside(false);
        this.downloadFileProgressDialog.setMessage("Download " + tablatureName);
        this.downloadFileProgressDialog.show();
    }

    private void HideDownloadProgressDialog() {
        if (this.downloadFileProgressDialog != null) {
            this.downloadFileProgressDialog.dismiss();
        }
        this.downloadFileProgressDialog = null;
    }

    private void updateTabList() {
        if(this.listView != null) {
            listItems = new ArrayList<>();
            for (int i = 0; i < this.tablatureJsonList.length(); i++) {
                HashMap<String,Object>item = new HashMap<>();
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) this.tablatureJsonList.getJSONObject(i);
                    item.put(RemoteTabListAdapter.ID, jsonObject.getInt("id"));
                    item.put(RemoteTabListAdapter.NAME,jsonObject.getString("name"));
                    item.put(RemoteTabListAdapter.FILE_ID,jsonObject.getInt("file_id"));
                    this.listItems.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(this.listItems.size() > 0) {
                this.textLabel.setVisibility(View.GONE);
                this.listView.setVisibility(View.VISIBLE);
                RemoteTabListAdapter remoteTabListAdapter = new RemoteTabListAdapter(getActivity(),R.layout.item_remote_tab,this.listItems);
                this.listView.setAdapter(remoteTabListAdapter);
                this.listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), DownloadTabFile.class);
                        intent.putExtra(RemoteTabListAdapter.FILE_ID,(int)listItems.get(position).get(RemoteTabListAdapter.FILE_ID));
                        getActivity().startService(intent);
                        ShowDownloadFileProgressDialog((String)listItems.get(position).get(RemoteTabListAdapter.NAME));
                    }
                });
            } else {
                this.textLabel.setVisibility(View.VISIBLE);
                this.listView.setVisibility(View.GONE);
            }
        }
    }
}
