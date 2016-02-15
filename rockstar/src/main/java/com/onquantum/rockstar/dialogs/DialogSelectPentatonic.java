package com.onquantum.rockstar.dialogs;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 6/24/14.
 */
public class DialogSelectPentatonic extends DialogFragment{

    private static final String FILE_NAME = "file_name";
    private View view;
    private ListView listView;
    ArrayList<HashMap<String,Object>>listObjects = null;
    public Button negativeButton = null;

    public static interface OnPentatonicSelectListener {
        void onPentatonicSelect(String fileName);
    }
    public DialogSelectPentatonic() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialog_select_pentatonic,container, false);
        listView = (ListView)view.findViewById(R.id.listView);

        negativeButton = (Button)view.findViewById(R.id.button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("info","Cance");
                dismiss();
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        Log.i("info","OnResume");
        listObjects = new ArrayList<HashMap<String, Object>>();
        String[] list;
        try {
            list = getActivity().getAssets().list("pentatonic");
            for (String file : list) {
                Log.i("info","file : " + file);
                HashMap<String, Object>item = new HashMap<String, Object>();
                item.put(FILE_NAME,file);
                listObjects.add(item);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity(),listObjects, R.layout.item_tabs,
                    new String[]{FILE_NAME},
                    new int[]{R.id.textView1}
            );
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = ((TextView)view.findViewById(R.id.textView1)).getText().toString();
                    if(getActivity() instanceof OnPentatonicSelectListener) {
                        ((OnPentatonicSelectListener)getActivity()).onPentatonicSelect(name);
                        dismiss();
                    }
                }
            });
        }catch (IOException e) {
            e.printStackTrace();
        }

        super.onResume();
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.i("info", "Dialog 1: onDismiss");
        super.onDismiss(dialog);
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.i("info", "Dialog 1: onCancel");
        super.onCancel(dialog);
    }
}
