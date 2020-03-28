package com.mmi.demo.java.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.layers.MapEventsOverlay;
import com.mmi.layers.MapEventsReceiver;
import com.mmi.layers.UserLocationOverlay;
import com.mmi.layers.location.GpsLocationProvider;
import com.mmi.layers.location.IFollowLocationListener;
import com.mmi.layers.location.IMyLocationConsumer;
import com.mmi.layers.location.IMyLocationProvider;
import com.mmi.layers.location.OnLocationClickListener;
import com.mmi.util.GeoPoint;
import com.mmi.util.LogUtils;
import com.mmi.util.constants.MapViewConstants;

public abstract class MapBaseFragment extends Fragment implements MapViewConstants, MapEventsReceiver {

  private static final String TAG = MapBaseFragment.class.getSimpleName();


  protected SharedPreferences mPrefs;
  protected MapView mMapView;


  protected UserLocationOverlay mLocationOverlay;

  public abstract String getSampleTitle();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    MapmyIndiaMapView mapmyIndiaMapView = new MapmyIndiaMapView(inflater.getContext());

    mMapView = mapmyIndiaMapView.getMapView();


    mMapView.setMultiTouchControls(true);


    setHardwareAccelerationOff();

    return mapmyIndiaMapView;
  }


  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setHardwareAccelerationOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
      mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final Context context = this.getActivity();


    mMapView.setMaxZoomLevel(18);
    mMapView.setMinZoomLevel(4);
    GpsLocationProvider source = new GpsLocationProvider(getActivity());
    this.mLocationOverlay = new UserLocationOverlay(source, mMapView);
    mLocationOverlay.setOnLocationClickListener(new OnLocationClickListener() {

      @Override
      public void OnSingleTapConfirmed() {

      }
    });
    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
    mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
    mMapView.getOverlays().add(this.mLocationOverlay);


    mLocationOverlay.enableMyLocation();


    source.setSecondaryConsumer(new IMyLocationConsumer() {
      @Override
      public void onLocationChanged(Location location, IMyLocationProvider source) {
        if (location == null)
          return;
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());


      }
    });


    mLocationOverlay.setFollowLocationListener(new IFollowLocationListener() {
      @Override
      public void followMeEnabled() {

      }

      @Override
      public void followMeDisabled() {

      }
    });

    mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


    this.mLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(context),
      mMapView);


    mMapView.setMultiTouchControls(true);
    mMapView.getOverlays().add(this.mLocationOverlay);

    mLocationOverlay.enableMyLocation();
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

    this.mLocationOverlay.disableMyLocation();


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

  @Override
  public void onDetach() {
    super.onDetach();


    mMapView.getOverlays().clear();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMapView.getOverlays().clear();

    this.mLocationOverlay.disableMyLocation();

  }


  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {
    return true;
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {
    return true;
  }

}