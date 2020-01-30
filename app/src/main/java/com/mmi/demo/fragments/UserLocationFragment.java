package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.layers.UserLocationOverlay;
import com.mmi.layers.location.GpsLocationProvider;
import com.mmi.util.LogUtils;
import com.mmi.util.constants.MapViewConstants;

public class UserLocationFragment extends Fragment implements MapViewConstants {
  private static final String TAG = UserLocationFragment.class.getSimpleName();
  UserLocationOverlay mLocationOverlay;
  MapView mMapView;
  Handler locationFoundHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 1:
          mMapView.setCenter(mLocationOverlay.getMyLocation());
          mMapView.setZoom(mMapView.getMaxZoomLevel());
          break;

      }
    }
  };


  private SharedPreferences mPrefs;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.mapview, container, false);
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();

    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    this.mLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(getActivity()), mMapView);

    mLocationOverlay.enableMyLocation();

    mMapView.getOverlays().add(this.mLocationOverlay);

    mLocationOverlay.runOnFirstFix(new Runnable() {
      @Override
      public void run() {
        locationFoundHandler.sendEmptyMessage(1);
      }
    });


    mMapView.invalidate();
    return view;
  }

  @Override
  public void onPause() {
    final SharedPreferences.Editor edit = mPrefs.edit();

    edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
    edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
    edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());

    edit.commit();

    LogUtils.LOGE(TAG, "onPause");
    LogUtils.LOGE(TAG, mMapView.getScrollX() + "");
    LogUtils.LOGE(TAG, mMapView.getScrollY() + "");
    LogUtils.LOGE(TAG, mMapView.getZoomLevel() + "");


    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 5));
    mMapView.scrollTo(mPrefs.getInt(PREFS_SCROLL_X, 0), mPrefs.getInt(PREFS_SCROLL_Y, 0));

    LogUtils.LOGE(TAG, "onResume");

    LogUtils.LOGE(TAG, mPrefs.getInt(PREFS_SCROLL_X, 0) + "");
    LogUtils.LOGE(TAG, mPrefs.getInt(PREFS_SCROLL_Y, 0) + "");
    LogUtils.LOGE(TAG, mPrefs.getInt(PREFS_ZOOM_LEVEL, 5) + "");

  }


}
