package com.example.tripbyphoto.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tripbyphoto.R;
import com.example.tripbyphoto.fragment.FragmentOne;
import com.example.tripbyphoto.fragment.FragmentTwo;
import com.example.tripbyphoto.utils.AppConsts;
import com.mapbox.mapboxsdk.BuildConfig;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    static final int NUMBER_OF_PAGES = 2;


//    protected Context mContext;
//    protected RecyclerView.LayoutManager mLayoutManager;
//    protected RecyclerView mRVGrid;
//    private ArrayList<String> mImagePaths;
//    private LayoutInflater mInflater;
//    private RecyclerViewAdapter.OnItemClickListener mListener;

    // data is passed into the constructor
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

//    (Context context, ArrayList<String> imagePaths, RecyclerView rvGrid, RecyclerViewAdapter.OnItemClickListener onItemClickListener) {
//        mContext = context;
//        mListener = onItemClickListener;
//        mImagePaths = imagePaths;
//        mInflater = LayoutInflater.from(mContext);
//        mLayoutManager = new LinearLayoutManager(mContext);
//        mRVGrid = rvGrid;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentOne.newInstance(0);
            case 1:
                return FragmentTwo.newInstance(1);
            default:
                return null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return false;
    }


//    // inflates the row layout from xml when needed
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.layout_recycler_view, parent, false);
//        return new ViewHolder(view);
//    }
//
//    // binds data to the ImageView in each row
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//        if (position == 0) {
//            // inflate RecyclerView with all imagesPaths and show to User, it was in the constructor
//            //holder.recyclerView.Recycler;//set RVG
//        }
//        if (position == 1) {
//            // inflate Layout for FullImage
//            //holder.  .setImageResource(R.drawable.cat);//set Image from click
//        }
//
//        holder.itemView.setOnClickListener((View v) -> {
//            mListener.onItemClick(v, position, "");
//            if (BuildConfig.DEBUG) {
//                Log.d(AppConsts.LOG_POSITION, "ClickPosition: " + position);
//            }
//        });
//    }
//
//    // parent activity will implement this method to respond to click events
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position, String name);
//    }
//
//    // stores and recycles views as they are scrolled off screen
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        RecyclerView recyclerView;
//        ImageView layout;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            recyclerView = itemView.findViewById(R.id.recycler_view_grid);
//            layout = itemView.findViewById(R.id.layout_full_image_rv);
//        }
//    }
}