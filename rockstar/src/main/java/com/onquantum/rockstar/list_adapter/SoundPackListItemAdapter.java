package com.onquantum.rockstar.list_adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.onquantum.rockstar.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Admin on 7/21/15.
 */
public class SoundPackListItemAdapter extends SimpleAdapter {

    private final Activity context;
    private List<? extends Map<String, ?>>data;
    private String[] from;
    private int[] to;
    private int resource;

    public SoundPackListItemAdapter(Activity context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data =  data;
        this.resource = resource;
        this.from = from;
        this.to = to;
    }

    public View getView(int position, View vew, ViewGroup parentView) {

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
    }

}
