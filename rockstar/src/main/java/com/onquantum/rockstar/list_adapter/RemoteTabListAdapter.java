package com.onquantum.rockstar.list_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 5/8/16.
 */
public class RemoteTabListAdapter extends ArrayAdapter<HashMap<String,Object>> {

    public static String NAME = "name";
    public static String ID = "id";
    public static String FILE_ID = "file_id";

    private int layout;
    private List<? extends Map<String, ?>>data;

    public RemoteTabListAdapter(Context context, int resource, List<HashMap<String, Object>> objects) {
        super(context, resource, objects);
        this.layout = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layout, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.tabName)).setText((String)this.data.get(position).get(NAME));

        return convertView;
    }
}
