package com.example.tripbyphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class FragmentRecyclerView extends Fragment {
    private static final String MY_NUM_KEY = "numOfPage";
    protected ViewPagerAdapter mAdapter;
    protected ViewPager mPager;
    protected ImageView scaleImage;
    protected CallBackClass callBackClass = new CallBackClass();
    protected HelpCallClass helpCallClass = new HelpCallClass();
    protected Context mContext;
    protected RecyclerViewGridAdapter mGridAdapter;
    protected RecyclerView mImageGrid;
    protected View view;
    private GetImages getImages;
    private ArrayList<Double> mLocationLatitude = new ArrayList<>();
    private ArrayList<Double> mLocationLongitude = new ArrayList<>();
    private ArrayList<String> mImagePaths = new ArrayList<>();
    RecyclerViewGridAdapter.OnItemClickListener onItemClickListenerGrid = (View view, int position, String name) -> {
        if (!ConnectionHelper.isOnline(mContext)) {
            Toast.makeText(Mapbox.getApplicationContext(), R.string.internet_warning, Toast.LENGTH_LONG).show();
            return;
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(AppConsts.LOG_CHECK + "FR1", "Item clicked successfully! On position " + position);
            }

            String path = mImagePaths.get(position);
            String latitude = String.valueOf(mLocationLatitude.get(position));
            String longitude = String.valueOf(mLocationLongitude.get(position));

            Log.d(AppConsts.LOG_CHECK + "FR1", "path " + path + ", latitude " + latitude + ", longitude " + longitude);

            callBackClass.registerCallBack(helpCallClass);
            helpCallClass.callBackCall(path, latitude, longitude);
            helpCallClass.callBackToggle(getActivity().findViewById(R.id.viewPager), getActivity().getApplicationContext());

            FragmentFullImage fragment = FragmentFullImage.newInstance(1, path, latitude, longitude);
            getFragmentManager().beginTransaction().replace(R.id.layout_full_image, fragment).commit();

            scaleImage.setLayoutParams(new ConstraintLayout.LayoutParams(
                    mImageGrid.getLayoutManager().findViewByPosition(position).getWidth(), mImageGrid.getLayoutManager().findViewByPosition(position).getHeight()));
            view.buildDrawingCache();
            scaleImage.setImageBitmap(view.getDrawingCache());
            scaleImage.setX(mImageGrid.getChildAt(position).getX() + 20);
            scaleImage.setY(mImageGrid.getChildAt(position).getY() + 20);

            Log.i(AppConsts.LOG_CHECK, "x- " + mImageGrid.getChildAt(position).getX() + ", y- " + mImageGrid.getChildAt(position).getY() + ", view - " + mImageGrid.getChildAt(position));
        }
    };

    public static FragmentRecyclerView newInstance(int numOfPage) {
        FragmentRecyclerView fragment = new FragmentRecyclerView();
        Bundle args = new Bundle();
        args.putInt(MY_NUM_KEY, numOfPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.layout_recycler_view, container, false);

        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mPager = view.findViewById(R.id.viewPager);
        mImageGrid = view.findViewById(R.id.recycler_view_grid);
        scaleImage = view.findViewById(R.id.iv_scale_image);

        getImages = new GetImages(mContext);
        mImagePaths = getImages.getImagesPaths(false);
        mLocationLatitude = getImages.getImagesLatitude(false);
        mLocationLongitude = getImages.getImagesLongitude(true);

        mImageGrid.setVerticalScrollBarEnabled(true);
        mImageGrid.setHorizontalScrollBarEnabled(false);
        mImageGrid.setLayoutManager(new GridLayoutManager(mContext, 2));
        mGridAdapter = new RecyclerViewGridAdapter(getActivity(), mImagePaths, mLocationLatitude, mLocationLongitude, onItemClickListenerGrid);
        mImageGrid.setAdapter(mGridAdapter);
        if (BuildConfig.DEBUG) {
            Log.d(AppConsts.LOG_CHECK + "FR1", "onCreateView, Item count is - " + mGridAdapter.getItemCount());
        }
        return view;
    }
}