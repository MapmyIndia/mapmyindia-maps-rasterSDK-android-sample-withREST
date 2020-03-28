package com.mmi.demo.java.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.events.MapListener;
import com.mmi.events.ScrollEvent;
import com.mmi.events.ZoomEvent;
import com.mmi.layers.MapEventsOverlay;
import com.mmi.layers.MapEventsReceiver;
import com.mmi.util.GeoPoint;
import com.mmi.util.constants.MapViewConstants;

import java.util.Locale;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class MapEventFragment extends Fragment implements MapEventsReceiver, MapViewConstants {

  MapView mMapView = null;
  private TextView zoomLevelTextView;
  private TextView mapCenterTextView;
  private TextView topLeftTextView;
  private TextView topRightTextView;
  private TextView bottomLeftTextView;
  private TextView bottomRightTextView;
  private SharedPreferences mPrefs;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_map_events, container, false);
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();
    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
    mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
    mMapView.setMultiTouchControls(true);
    setupUI(view);

    mMapView.setMapListener(new MapListener() {
      @Override
      public boolean onScroll(ScrollEvent event) {
        setMapData(mMapView);
        return false;
      }

      @Override
      public boolean onZoom(ZoomEvent event) {
        setMapData(mMapView);
        return false;
      }
    });

    return view;
  }

  public void setMapData(MapView mapView) {

    if (mapView != null) {

      zoomLevelTextView.setText(String.format(Locale.getDefault(), "%d", mapView.getZoomLevel()));
      mapCenterTextView.setText(String.format("%s, %s", mapView.getMapCenter().getLatitudeE6() / 1E6, mapView.getMapCenter().getLongitudeE6() / 1E6));
      topLeftTextView.setText(String.format("%s, %s", mapView.getBoundingBox().getLatNorthE6() / 1E6, mapView.getBoundingBox().getLonEastE6() / 1E6));
      topRightTextView.setText(String.format("%s, %s", mapView.getBoundingBox().getLatNorthE6() / 1E6, mapView.getBoundingBox().getLonWestE6() / 1E6));
      bottomLeftTextView.setText(String.format("%s, %s", mapView.getBoundingBox().getLatSouthE6() / 1E6, mapView.getBoundingBox().getLonEastE6() / 1E6));
      bottomRightTextView.setText(String.format("%s, %s", mapView.getBoundingBox().getLatSouthE6() / 1E6, mapView.getBoundingBox().getLonWestE6() / 1E6));

    }
  }

  void setupUI(View view) {
    zoomLevelTextView = view.findViewById(R.id.zoom_level_text_view);
    mapCenterTextView = view.findViewById(R.id.map_center_text_view);
    topLeftTextView = view.findViewById(R.id.top_left_text_view);
    topRightTextView = view.findViewById(R.id.top_right_text_view);
    bottomLeftTextView = view.findViewById(R.id.bottom_left_text_view);
    bottomRightTextView = view.findViewById(R.id.bottom_right_text_view);
  }

  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {
    return false;
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {
    return false;
  }

  @Override
  public void onPause() {
    final SharedPreferences.Editor edit = mPrefs.edit();
    edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
    edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
    edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
    edit.apply();

    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, 5));
    mMapView.scrollTo(mPrefs.getInt(PREFS_SCROLL_X, 0), mPrefs.getInt(PREFS_SCROLL_Y, 0));
  }
}
