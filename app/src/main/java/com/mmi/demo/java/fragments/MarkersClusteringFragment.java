package com.mmi.demo.java.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import com.mmi.demo.R;
import com.mmi.demo.java.model.MarkerModel;
import com.mmi.layers.Marker;
import com.mmi.layers.MarkerClusterer;
import com.mmi.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class MarkersClusteringFragment extends MapBaseFragment {


  @Override
  public String getSampleTitle() {
    return "Maps Marker";
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ArrayList<MarkerModel> markerModels = new ArrayList<>();
    markerModels.add(new MarkerModel(28.549356 + "", 77.26780099999999 + "", "28.549356,77.26780099999999", null, new GeoPoint(28.549356, 77.26780099999999)));
    markerModels.add(new MarkerModel(28.551844 + "", 77.26749 + "", "28.551844,77.26749", null, new GeoPoint(28.551844, 77.26749)));
    markerModels.add(new MarkerModel(28.554454 + "", 77.265473 + "", "28.554454,77.265473", null, new GeoPoint(28.554454, 77.265473)));
    markerModels.add(new MarkerModel(28.549637999999998 + "", 77.262909 + "", "28.549637999999998,77.262909", null, new GeoPoint(28.549637999999998, 77.262909)));
    markerModels.add(new MarkerModel(28.555245 + "", 77.266117 + "", "28.555245,77.266117", null, new GeoPoint(28.555245, 77.266117)));
    markerModels.add(new MarkerModel(28.558149 + "", 77.269787 + "", "28.558149,77.269787", null, new GeoPoint(28.558149, 77.269787)));
    markerModels.add(new MarkerModel(28.555369 + "", 77.271042 + "", "28.555369,77.271042", null, new GeoPoint(28.555369, 77.271042)));
    markerModels.add(new MarkerModel(28.544428 + "", 77.279057 + "", "28.544428,77.279057", null, new GeoPoint(28.544428, 77.279057)));
    markerModels.add(new MarkerModel(28.538275 + "", 77.283821 + "", "28.538275,77.283821", null, new GeoPoint(28.538275, 77.283821)));
    markerModels.add(new MarkerModel(28.536604999999998 + "", 77.2872 + "", "28.536604999999998,77.2872", null, new GeoPoint(28.536604999999998, 77.2872)));
    markerModels.add(new MarkerModel(28.538442999999997 + "", 77.291921 + "", "28.538442999999997,77.291921", null, new GeoPoint(28.538442999999997, 77.291921)));
    markerModels.add(new MarkerModel(28.542326 + "", 77.30133 + "", "28.542326,77.30133", null, new GeoPoint(28.542326, 77.30133)));
    markerModels.add(new MarkerModel(28.542609 + "", 77.30211299999999 + "", "28.542609,77.30211299999999", null, new GeoPoint(28.542609, 77.30211299999999)));
    markerModels.add(new MarkerModel(28.543042999999997 + "", 77.302843 + "", "28.543042999999997,77.302843", null, new GeoPoint(28.543042999999997, 77.302843)));


    addOverLays(markerModels);
  }

  void addOverLays(ArrayList<MarkerModel> markerModels) {

    MarkerClusterer markerClusterer = new MarkerClusterer(getActivity());

    markerClusterer.setColor(getResources().getColor(R.color.green_color));
    markerClusterer.mAnchorV = Marker.ANCHOR_CENTER;

    markerClusterer.mTextAnchorV = Marker.ANCHOR_CENTER;

    markerClusterer.setTextSize(12);
    ArrayList<GeoPoint> points = new ArrayList<>();

    for (MarkerModel markerModel : markerModels) {
      Marker marker = new Marker(mMapView);
      marker.setTitle(markerModel.getTitle());
      marker.setDescription(markerModel.getDescription());
      marker.setIcon(getResources().getDrawable(R.drawable.marker_selected));
      marker.setPosition(markerModel.getGeoPoint());
      marker.setInfoWindow(null);
      marker.setRelatedObject(markerModel);
      markerClusterer.add(marker);
      points.add(markerModel.getGeoPoint());
    }
    mMapView.setBounds(points);
    mMapView.getOverlays().add(markerClusterer);
    mMapView.invalidate();

  }


  Bitmap generateMarker() {
    ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
    biggerCircle.setIntrinsicHeight(50);
    biggerCircle.setIntrinsicWidth(50);
    biggerCircle.setBounds(new Rect(0, 0, 50, 50));
    biggerCircle.getPaint().setColor(Color.parseColor("#799C5A"));
    biggerCircle.setAlpha(200);

    ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
    smallerCircle.setIntrinsicHeight(10);
    smallerCircle.setIntrinsicWidth(10);
    smallerCircle.setBounds(new Rect(0, 0, 10, 10));
    smallerCircle.getPaint().setColor(Color.parseColor("#799C5A"));
    smallerCircle.setPadding(5, 5, 5, 5);
    smallerCircle.setAlpha(95);
    Drawable[] d = {smallerCircle, biggerCircle};

    return getSingleDrawable(new LayerDrawable(d)).getBitmap();
  }

  public BitmapDrawable getSingleDrawable(LayerDrawable layerDrawable) {

    int resourceBitmapHeight = 15, resourceBitmapWidth = 15;

    float widthInInches = 0.3f;

    int widthInPixels = (int) (widthInInches * getResources().getDisplayMetrics().densityDpi);
    int heightInPixels = (int) (widthInPixels * resourceBitmapHeight / resourceBitmapWidth);

    int insetLeft = 10, insetTop = 10, insetRight = 10, insetBottom = 10;

    layerDrawable.setLayerInset(1, insetLeft, insetTop, insetRight, insetBottom);

    Bitmap bitmap = Bitmap.createBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(bitmap);
    layerDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
    layerDrawable.draw(canvas);

    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
    bitmapDrawable.setBounds(0, 0, widthInPixels, heightInPixels);

    return bitmapDrawable;
  }
}
