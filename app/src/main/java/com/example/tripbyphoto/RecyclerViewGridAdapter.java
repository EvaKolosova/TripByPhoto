package com.example.tripbyphoto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.ViewHolder> {

    protected Context mContext;
    private ArrayList<String> f;
    protected ArrayList<Double> locationLatitude;
    protected ArrayList<Double> locationLongitude;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    // data is passed into the constructor
    RecyclerViewGridAdapter(Context context, ArrayList<String> f, ArrayList<Double> locLatitude, ArrayList<Double> locLongitude, OnItemClickListener onItemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.f = f;
        this.locationLatitude = locLatitude;
        this.locationLongitude = locLongitude;
        mContext = context;
        mListener = onItemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the ImageView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String itemData = f.get(position);
        Glide
                .with(mContext)
                .load(itemData)
                .into(holder.imageview);

        holder.imageview.setOnClickListener((View v) -> {
            mListener.onItemClick(v, position, "myImageView");
            Log.d("kolosova_imagePosition", "ImageClickPosition " + position);
        });
    }

    // parent activity will implement this method to respond to click events
    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name);
    }

    @Override
    public int getItemCount() {
        return f.size();
    }

    public long getItemId(int position) { return position;}

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageview;

        ViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageOne);
        }
    }
}