package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.layers.Marker;
import com.mmi.util.GeoPoint;
import com.mmi.util.LogUtils;
import com.mmi.util.constants.MapViewConstants;

import java.util.ArrayList;
import java.util.Random;

public class MarkersTestFragment extends Fragment implements MapViewConstants {

  private static final String TAG = MarkersTestFragment.class.getSimpleName();
  private MapView mMapView;
  private SharedPreferences mPrefs;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.mapview, container, false);
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    // Setup Map
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();

    ArrayList<GeoPoint> points = new ArrayList<>();
    // Dynamically create 100 markers
    Random r = new Random();
    for (int i = 0; i < 100; i++) {
      GeoPoint position = new GeoPoint(new GeoPoint(28.11493876707079f + r.nextFloat() / 100,
        77.3647260069847f + r.nextFloat() / 100));
      addMarker(position);
      points.add(position);

    }
    mMapView.invalidate();

    mMapView.setBounds(points);
    return view;
  }


  void addMarker(GeoPoint point) {


    Marker marker = new Marker(mMapView);
    marker.setTitle("");
    marker.setDescription("");

    marker.setPosition(point);
    marker.setInfoWindow(null);

    mMapView.getOverlays().add(marker);


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
