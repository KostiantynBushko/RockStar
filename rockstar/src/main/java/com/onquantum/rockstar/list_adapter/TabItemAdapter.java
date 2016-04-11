package com.onquantum.rockstar.list_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.onquantum.rockstar.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 1/3/16.
 */
public class TabItemAdapter extends ArrayAdapter<HashMap<String,Object>> {
    public static String NAME = "name";
    public static String SHORT_NAME = "short_name";
    public static String AUTHOR = "author";
    private int layout;
    private List<? extends Map<String, ?>>data;

    private ViewHolder lastSelected;

    public interface TabItemAdapterInterface {
        void OnClickMoreButton(int position);
        void OnClickDeleteButton(int position);
        void OnClickEditButton(int position);
        void OnClickShareButton(int position);
    }

    private TabItemAdapterInterface tabItemAdapterInterface;

    public TabItemAdapter(Context context, int resource, List<HashMap<String, Object>> objects, TabItemAdapterInterface tabItemAdapterInterface) {
        super(context, resource, objects);
        this.tabItemAdapterInterface = tabItemAdapterInterface;
        data = objects;
        layout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewHolder = null;
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layout, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.contextMenuLayout = (LinearLayout)convertView.findViewById(R.id.contextMenuLayout);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.tabImage);
            viewHolder.shortName = (TextView)convertView.findViewById(R.id.tabName);
            viewHolder.author = (TextView)convertView.findViewById(R.id.tabsAuthor);
            viewHolder.more = (ImageButton)convertView.findViewById(R.id.moreButton);
            viewHolder.delete = (ImageButton)convertView.findViewById(R.id.deleteTabButton);
            viewHolder.edit = (ImageButton)convertView.findViewById(R.id.editTabButton);
            viewHolder.share = (ImageButton)convertView.findViewById(R.id.shareButton);

            viewHolder.position = position;
            viewHolder.shortName.setText((String)data.get(position).get(SHORT_NAME));
            viewHolder.name = (String)data.get(position).get(NAME);
            viewHolder.author.setText((String)data.get(position).get(AUTHOR));
            viewHolder.contextMenuLayout.setVisibility(View.GONE);
            convertView.setTag(viewHolder);

            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabItemAdapterInterface.OnClickMoreButton(viewHolder.position);
                    if(viewHolder.contextMenuLayout.getVisibility() == View.GONE) {
                        if(lastSelected != null) {
                            lastSelected.contextMenuLayout.setVisibility(View.GONE);
                        }
                        lastSelected = viewHolder;
                        viewHolder.contextMenuLayout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.contextMenuLayout.setVisibility(View.GONE);
                    }
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabItemAdapterInterface.OnClickDeleteButton(viewHolder.position);
                }
            });
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabItemAdapterInterface.OnClickEditButton(viewHolder.position);
                }
            });
            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabItemAdapterInterface.OnClickShareButton(viewHolder.position);
                }
            });

        } else {
            mainViewHolder = (ViewHolder)convertView.getTag();
            mainViewHolder.position = position;
            mainViewHolder.shortName.setText((String)data.get(position).get(SHORT_NAME));
            mainViewHolder.name = (String)data.get(position).get(NAME);
            mainViewHolder.author.setText((String)data.get(position).get(AUTHOR));
            mainViewHolder.contextMenuLayout.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        int position = -1;
        LinearLayout contextMenuLayout;
        ImageView imageView;
        TextView shortName;
        String name;
        TextView author;
        ImageButton more;
        ImageButton delete;
        ImageButton edit;
        ImageButton share;
    }
}
