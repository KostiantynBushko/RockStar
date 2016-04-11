package com.onquantum.rockstar.list_adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Created by Admin on 7/21/15.
 */
public class SoundPackListItemAdapter extends ArrayAdapter<HashMap<String,Object>> {

    //private final Activity context;
    /*private List<? extends Map<String, ?>>data;
    private String[] from;
    private int[] to;
    private int resource;*/


    private int layout;
    private List<? extends Map<String, ?>>data;

    private SPViewHolder lastSelected;

    public interface SoundPackItemInterface {
        public void OnSwitchSoundPack(int position);
    }

    private SoundPackItemInterface soundPackItemInterface;

    /*public SoundPackListItemAdapter(Activity context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data =  data;
        this.resource = resource;
        this.from = from;
        this.to = to;
    }*/

    public SoundPackListItemAdapter(Context context, int resource, List<HashMap<String, Object>> objects, SoundPackItemInterface soundPackItemInterface) {
        super(context, resource, objects);
        //this.context = context;
        //this.tabItemAdapterInterface = tabItemAdapterInterface;
        //data = objects;
        //layout = resource;
        this.soundPackItemInterface = soundPackItemInterface;
        this.data = objects;
        this.layout = resource;
    }

    /*public View getView(int position, View vew, ViewGroup parentView) {

        Typeface typeFaceCapture = Typeface.createFromAsset(context.getAssets(), "font/Capture_it.ttf");
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View viewItem = layoutInflater.inflate(this.resource, null, true);
        TextView textView = (TextView)viewItem.findViewById(to[0]);
        ImageView imageView = (ImageView)viewItem.findViewById(to[1]);

        Map contentItem = data.get(position);
        textView.setText((String)contentItem.get(from[0]));
        textView.setTypeface(typeFaceCapture);
        imageView.setImageBitmap((Bitmap)contentItem.get(from[1]));

        return viewItem;
    }*/

    public View getView(final int position, View convertView, ViewGroup parentView) {
        SPViewHolder mainViewHolder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layout, parentView, false);

            Typeface typeFaceCapture = Typeface.createFromAsset(getContext().getAssets(), "font/Capture_it.ttf");

            final SPViewHolder viewHolder = new SPViewHolder();
            viewHolder.image = (ImageView)convertView.findViewById(R.id.soundPackIcon);
            viewHolder.text = (TextView)convertView.findViewById(R.id.soundPackageName);
            viewHolder.text.setTypeface(typeFaceCapture);
            viewHolder.button = (ImageButton)convertView.findViewById(R.id.imageButton);
            viewHolder.position = position;

            viewHolder.is_active = (Boolean)data.get(position).get(DBGuitarTable.IS_ACTIVE);
            viewHolder.image.setImageBitmap((Bitmap)data.get(position).get(DBGuitarTable.ICON));
            viewHolder.text.setText((String)data.get(position).get(DBGuitarTable.NAME));
            viewHolder.ID = (Long) data.get(position).get(DBGuitarTable.ID);
            viewHolder.sound_pack_available = (Boolean)data.get(position).get(DBGuitarTable.SOUND_PACK_AVAILABLE);
            if (viewHolder.sound_pack_available) {
                viewHolder.button.setVisibility(View.VISIBLE);
            } else {
                viewHolder.button.setVisibility(View.INVISIBLE);
            }
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    soundPackItemInterface.OnSwitchSoundPack(viewHolder.position);
                }
            });
            if (viewHolder.is_active) {
                viewHolder.button.setSelected(true);
            } else {
                viewHolder.button.setSelected(false);
            }
            convertView.setTag(viewHolder);

        } else {
            mainViewHolder = (SPViewHolder)convertView.getTag();
            mainViewHolder.position = position;
            mainViewHolder.image.setImageBitmap((Bitmap)data.get(position).get(DBGuitarTable.ICON));
            mainViewHolder.text.setText((String)data.get(position).get(DBGuitarTable.NAME));
            mainViewHolder.ID = (Long) data.get(position).get(DBGuitarTable.ID);
            mainViewHolder.is_active = (Boolean)data.get(position).get(DBGuitarTable.IS_ACTIVE);
            mainViewHolder.sound_pack_available = (Boolean)data.get(position).get(DBGuitarTable.SOUND_PACK_AVAILABLE);
            if (mainViewHolder.sound_pack_available) {
                mainViewHolder.button.setVisibility(View.VISIBLE);
            } else {
                mainViewHolder.button.setVisibility(View.INVISIBLE);
            }
            if (mainViewHolder.is_active) {
                mainViewHolder.button.setSelected(true);
            } else {
                mainViewHolder.button.setSelected(false);
            }
        }
        return convertView;
    }

    private class SPViewHolder {
        int position = -1;
        int index = 0;
        public long ID = -1;
        public ImageView image;
        public TextView text;
        public ImageButton button;
        public boolean is_active = false;
        public boolean sound_pack_available = false;
    }
}
