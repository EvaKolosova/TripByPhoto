package com.example.tripbyphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tripbyphoto.BuildConfig;
import com.example.tripbyphoto.CallBackClass;
import com.example.tripbyphoto.HelpCallClass;
import com.example.tripbyphoto.R;
import com.example.tripbyphoto.adapter.RecyclerViewGridAdapter;
import com.example.tripbyphoto.adapter.ViewPagerAdapter;
import com.example.tripbyphoto.utils.AppConsts;
import com.example.tripbyphoto.utils.ConnectionHelper;
import com.example.tripbyphoto.utils.GetImages;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.ArrayList;

public class FragmentOne extends Fragment {
    protected ViewPagerAdapter mAdapter;
    protected ViewPager mPager;
    private static final String MY_NUM_KEY = "numOfPage";
    protected CallBackClass callBackClass = new CallBackClass();
    protected HelpCallClass helpCallClass = new HelpCallClass();
    protected Context mContext;
    protected RecyclerViewGridAdapter mGridAdapter;
    protected RecyclerView mImageGrid;
    private GetImages getImages;
    protected View view;
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListenerGrid = (View view, int position, String name) -> {
        if (!ConnectionHelper.isOnline(mContext)) {
            Toast.makeText(Mapbox.getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.d(AppConsts.LOG_CHECK + "FR1", "Item clicked successfully! On position " + position);

            String path = mImagePaths.get(position);
            String latitude = String.valueOf(mLocationLatitude.get(position));
            String longitude = String.valueOf(mLocationLongitude.get(position));

            Log.d(AppConsts.LOG_CHECK + "FR1", "path " + path + ", latitude " + latitude + ", longitude " + longitude);

            callBackClass.registerCallBack(helpCallClass);
            helpCallClass.callBackCall(path, latitude, longitude);
            helpCallClass.callBackToggle(getActivity().findViewById(R.id.viewPager));

            FragmentTwo fragment = FragmentTwo.newInstance(1, path, latitude, longitude);
            getFragmentManager().beginTransaction().replace(R.id.layout_full_image, fragment).commit();

        }
    };

    public static FragmentOne newInstance(int numOfPage) {
        FragmentOne f = new FragmentOne();
        Bundle args = new Bundle();
        args.putInt(MY_NUM_KEY, numOfPage);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.layout_recycler_view, container, false);

        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mPager = view.findViewById(R.id.viewPager);

        Log.d(AppConsts.LOG_CHECK + "FR1", "onCreateView");
        getImages = new GetImages(mContext);
        mImagePaths = getImages.getImagesPaths(false);
        mLocationLatitude = getImages.getImagesLatitude(false);
        mLocationLongitude = getImages.getImagesLongitude(true);

        mImageGrid = view.findViewById(R.id.recycler_view_grid);

        mImageGrid.setVerticalScrollBarEnabled(true);
        mImageGrid.setHorizontalScrollBarEnabled(false);
        mImageGrid.setLayoutManager(new GridLayoutManager(mContext, 2));
        mGridAdapter = new RecyclerViewGridAdapter(getActivity(), mImagePaths, mLocationLatitude, mLocationLongitude, onItemClickListenerGrid);
        mImageGrid.setAdapter(mGridAdapter);
        if (BuildConfig.DEBUG) {
            Log.d(AppConsts.LOG_CHECK + "FR1", "Item count is - " + mGridAdapter.getItemCount());
        }
        return view;
    }
}