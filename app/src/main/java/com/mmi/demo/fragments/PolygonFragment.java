package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.demo.model.MarkerModel;
import com.mmi.events.MapListener;
import com.mmi.events.ScrollEvent;
import com.mmi.events.ZoomEvent;
import com.mmi.layers.MapEventsOverlay;
import com.mmi.layers.MapEventsReceiver;
import com.mmi.layers.Polygon;
import com.mmi.util.GeoPoint;
import com.mmi.util.constants.MapViewConstants;

import java.util.ArrayList;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class PolygonFragment extends Fragment implements MapEventsReceiver, MapViewConstants {
  protected SharedPreferences mPrefs;
  MapView mMapView;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_polygon, container, false);
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();
    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
    mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
    mMapView.setMultiTouchControls(true);


    ArrayList<MarkerModel> markerModels = new ArrayList<>();
    markerModels.add(new MarkerModel(28.549356 + "", 77.26780099999999 + "", "Mapmyindia Head Office New Delhi,68,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.549356, 77.26780099999999)));
    markerModels.add(new MarkerModel(28.551844 + "", 77.26749 + "", "Juice Shop,261,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.551844, 77.26749)));
    markerModels.add(new MarkerModel(28.554454 + "", 77.265473 + "", "Modi Mill Bus Stop,Bhakti Vedant Swami Marg,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.554454, 77.265473)));
    markerModels.add(new MarkerModel(28.549637999999998 + "", 77.262909 + "", "Dda Parking,Kalkaji Mandir Flyover U Turn,Okhla Industrial Estate Phase 3, New Delhi,Delhi\n", null, new GeoPoint(28.549637999999998, 77.262909)));
    markerModels.add(new MarkerModel(28.555245 + "", 77.266117 + "", "Modi Flour Mills,Okhla Flyover,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.555245, 77.266117)));
    markerModels.add(new MarkerModel(28.558149 + "", 77.269787 + "", "Taxi Stand,Okhla Flyover,Ishwar Nagar, New Delhi,Delhi", null, new GeoPoint(28.558149, 77.269787)));
    markerModels.add(new MarkerModel(28.555369 + "", 77.271042 + "", "Basil,83,Sukhdev Vihar, New Delhi,Delhi", null, new GeoPoint(28.555369, 77.271042)));
    markerModels.add(new MarkerModel(28.544428 + "", 77.279057 + "", "Harkesh Nagar Bus Stop,Mathura Road/Nh 2,Jasola Vihar, New Delhi,Delhi", null, new GeoPoint(28.544428, 77.279057)));
    markerModels.add(new MarkerModel(28.538275 + "", 77.283821 + "", "Jasola Apollo Metro Station,Sarita Vihar Flyover/Nh 2,Jasola District Centre, New Delhi,Delhi", null, new GeoPoint(28.538275, 77.283821)));
    markerModels.add(new MarkerModel(28.536604999999998 + "", 77.2872 + "", "Sarita Vihar Bus Stop,Gd Birla Marg,Jasola District Centre, New Delhi,Delhi", null, new GeoPoint(28.536604999999998, 77.2872)));
    markerModels.add(new MarkerModel(28.538442999999997 + "", 77.291921 + "", "Sarita Vihar Pocket K Bus Stop,Gd Birla Marg,Jasola Vihar, New Delhi,Delhi", null, new GeoPoint(28.538442999999997, 77.291921)));
    markerModels.add(new MarkerModel(28.542326 + "", 77.30133 + "", "Addidas,Addidas,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, new GeoPoint(28.542326, 77.30133)));
    markerModels.add(new MarkerModel(28.542609 + "", 77.30211299999999 + "", "Central Bank Of India,G32/2,Abul Fazal Enclave Part 2 (Block G), New Delhi,Delhi", null, new GeoPoint(28.542609, 77.30211299999999)));
    markerModels.add(new MarkerModel(28.543042999999997 + "", 77.302843 + "", "Shaheen Bagh Bus Stop,Redtape,Maulana Azad Road,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, new GeoPoint(28.543042999999997, 77.302843)));

    ArrayList<GeoPoint> points = new ArrayList<>();
    for (MarkerModel markerModel : markerModels) {
      points.add(markerModel.getGeoPoint());
    }

    Polygon polygon = new Polygon(getActivity());

    polygon.setPoints(points);

    polygon.setFillColor(Color.BLUE);
    mMapView.setMapListener(new MapListener() {
      @Override
      public boolean onScroll(ScrollEvent event) {
        return false;
      }

      @Override
      public boolean onZoom(ZoomEvent event) {
        return false;
      }
    });
    mMapView.getOverlays().add(polygon);
    mMapView.setBounds(points);

    return view;
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

  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {
    return false;
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {
    return false;
  }

}