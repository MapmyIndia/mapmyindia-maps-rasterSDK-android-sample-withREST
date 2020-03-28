package com.mmi.demo.java.fragments;

import android.os.Bundle;

import com.mmi.demo.R;
import com.mmi.demo.java.model.MarkerModel;
import com.mmi.demo.java.util.Data;
import com.mmi.demo.java.util.Utils;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class MarkersInfoWindowFragment extends MapBaseFragment {


  private static final String TAG = MarkersInfoWindowFragment.class.getSimpleName();

  @Override
  public String getSampleTitle() {
    return "Maps Marker";
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ArrayList<MarkerModel> markerModels = new ArrayList<>();
    markerModels.add(new MarkerModel("Mapmyindia Head Office New Delhi", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "68,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.549356, 77.26780099999999)));
    markerModels.add(new MarkerModel("Juice Shop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "261,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.551844, 77.26749)));
    markerModels.add(new MarkerModel("Modi Mill Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Bhakti Vedant Swami Marg,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.554454, 77.265473)));
    markerModels.add(new MarkerModel("Dda Parking", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Kalkaji Mandir Flyover U Turn,Okhla Industrial Estate Phase 3, New Delhi,Delhi\n", null, new GeoPoint(28.549637999999998, 77.262909)));
    markerModels.add(new MarkerModel("Modi Flour Mills", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Okhla Flyover,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, new GeoPoint(28.555245, 77.266117)));
    markerModels.add(new MarkerModel("Taxi Stand", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Okhla Flyover,Ishwar Nagar, New Delhi,Delhi", null, new GeoPoint(28.558149, 77.269787)));
    markerModels.add(new MarkerModel("Basil", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "83,Sukhdev Vihar, New Delhi,Delhi", null, new GeoPoint(28.555369, 77.271042)));
    markerModels.add(new MarkerModel("Harkesh Nagar Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Mathura Road/Nh 2,Jasola Vihar, New Delhi,Delhi", null, new GeoPoint(28.544428, 77.279057)));
    markerModels.add(new MarkerModel("Jasola Apollo Metro Station", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Sarita Vihar Flyover/Nh 2,Jasola District Centre, New Delhi,Delhi", null, new GeoPoint(28.538275, 77.283821)));
    markerModels.add(new MarkerModel("Sarita Vihar Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Gd Birla Marg,Jasola District Centre, New Delhi,Delhi", null, new GeoPoint(28.536604999999998, 77.2872)));
    markerModels.add(new MarkerModel("Sarita Vihar Pocket K Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Gd Birla Marg,Jasola Vihar, New Delhi,Delhi", null, new GeoPoint(28.538442999999997, 77.291921)));
    markerModels.add(new MarkerModel("Addidas", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Abul Fazal Enclave Part 2, New Delhi,Delhi", null, new GeoPoint(28.542326, 77.30133)));
    markerModels.add(new MarkerModel("Central Bank Of India", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "G32/2,Abul Fazal Enclave Part 2 (Block G), New Delhi,Delhi", null, new GeoPoint(28.542609, 77.30211299999999)));
    markerModels.add(new MarkerModel("Shaheen Bagh Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.length - 1)], "Redtape,Maulana Azad Road,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, new GeoPoint(28.543042999999997, 77.302843)));


    addOverLays(markerModels);
  }


  void addOverLays(ArrayList<MarkerModel> markerModels) {
    ArrayList<GeoPoint> points = new ArrayList<>();
    BasicInfoWindow infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);

    infoWindow.setTipColor(getResources().getColor(R.color.base_color));
    for (MarkerModel markerModel : markerModels) {
      Marker marker = new Marker(mMapView);
      marker.setTitle(markerModel.getTitle());
      marker.setDescription(markerModel.getDescription());
      marker.setSubDescription(markerModel.getSubDescription());

      marker.setPosition(markerModel.getGeoPoint());

      marker.setInfoWindow(infoWindow);
      marker.setRelatedObject(markerModel);
      mMapView.getOverlays().add(marker);
      points.add(markerModel.getGeoPoint());
    }
    mMapView.invalidate();
    mMapView.setBounds(points);
  }


  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {
    BasicInfoWindow.closeAllInfoWindowsOn(mMapView);
    return super.singleTapConfirmedHelper(p);
  }


}
