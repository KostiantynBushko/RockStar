package com.onquantum.rockstar.dialogs;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 8/4/14.
 */
public class DialogSelectGuitarStyle extends DialogFragment {

    private View view;
    private ListView listView;
    private Button negativeButton;
    ArrayList<HashMap<String,Object>>listObjects = null;

    public static interface OnSelectListener {
        void onChangeGuitarStyle(String style);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = layoutInflater.inflate(R.layout.dialog_select_guitar_style, viewGroup,false);
        listView = (ListView)view.findViewById(R.id.listView);
        negativeButton = (Button)view.findViewById(R.id.button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listObjects = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object>item1 = new HashMap<String, Object>();
        HashMap<String, Object>item2 = new HashMap<String, Object>();
        item1.put("style", Settings.STYLE_CLEAN);
        listObjects.add(item1);
        item2.put("style",Settings.STYLE_DISTORTION);
        listObjects.add(item2);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),listObjects, R.layout.item_pentatonic,
                new String[]{"style"},
                new int[]{R.id.textView1}
        );
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String style = ((TextView)view.findViewById(R.id.textView1)).getText().toString();
                if(getActivity() instanceof OnSelectListener) {
                    ((DialogSelectGuitarStyle.OnSelectListener)getActivity()).onChangeGuitarStyle(style);
                }
                dismiss();
            }
        });
    }
}
