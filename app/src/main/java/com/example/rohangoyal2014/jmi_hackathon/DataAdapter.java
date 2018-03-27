package com.example.rohangoyal2014.jmi_hackathon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RohanGoyal2014 on 3/24/2018.
 */

public class DataAdapter extends BaseAdapter{
    Context context;
    List<Data> list;
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi=View.inflate(context,R.layout.item,null);
        ImageView imageView=vi.findViewById(R.id.icon_image);
        TextView nameView=vi.findViewById(R.id.name);
        TextView vicinityView=vi.findViewById(R.id.vicinity);
        TextView ratingView=vi.findViewById(R.id.stars);
        Glide.with(context).load(list.get(i).icon).into(imageView);
        nameView.setText(list.get(i).name);
        vicinityView.setText(list.get(i).vicinity);
        ratingView.setText(list.get(i).rating);
        return vi;
    }

    public DataAdapter(Context context, List<Data> list) {
        this.context = context;
        this.list = list;
    }
}
