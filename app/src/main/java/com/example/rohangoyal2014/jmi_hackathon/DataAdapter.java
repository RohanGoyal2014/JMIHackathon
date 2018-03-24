package com.example.rohangoyal2014.jmi_hackathon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by RohanGoyal2014 on 3/24/2018.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>{
    private static final String TAG = "DataAdapter";

    final private ListItemClickListener mOnClickListener;
    private ArrayList<Data> arrayList=new ArrayList<>();
    private Context context;


    private int mNumberItems;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public DataAdapter(ArrayList<Data> dataList, ListItemClickListener listener,Context context) {
        Log.e(TAG,"Here in constructor");

        Log.e(TAG,String.valueOf(arrayList.size()));
        arrayList=dataList;
        mOnClickListener = listener;
        this.context=context;
    }


    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.e(TAG,"created");
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        DataViewHolder viewHolder = new DataViewHolder(view);


        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        Log.e(TAG,"Here in onbind");
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView iconView;
        TextView nameView;
        TextView vicinityView;
        TextView ratingView;

        public DataViewHolder(View itemView) {
            super(itemView);

            iconView =  itemView.findViewById(R.id.icon);
            nameView=itemView.findViewById(R.id.name);
            vicinityView=itemView.findViewById(R.id.vicinity);
            ratingView=itemView.findViewById(R.id.stars);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            String iconResource=arrayList.get(listIndex).icon;
            String name=arrayList.get(listIndex).name;
            String vicinity=arrayList.get(listIndex).vicinity;
            String rating=arrayList.get(listIndex).rating;
            Glide.with(context).load(iconResource).into(iconView);
            nameView.setText(name);
            vicinityView.setText(vicinity);
            Log.e(TAG,"Setting"+name);
            ratingView.setText(rating+" stars");
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
