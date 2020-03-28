package com.mmi.demo.java.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.demo.java.util.TransparentProgressDialog;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.MapEventsOverlay;
import com.mmi.layers.MapEventsReceiver;
import com.mmi.layers.Marker;
import com.mmi.services.api.Place;
import com.mmi.services.api.PlaceResponse;
import com.mmi.services.api.geocoding.GeoCode;
import com.mmi.services.api.geocoding.GeoCodeResponse;
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding;
import com.mmi.services.api.reversegeocode.MapmyIndiaReverseGeoCode;
import com.mmi.util.GeoPoint;
import com.mmi.util.constants.MapViewConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class GeoCodeReverseGeoCodeFragment extends Fragment implements MapEventsReceiver, MapViewConstants, View.OnClickListener {

  MapView mMapView = null;
  BasicInfoWindow infoWindow;
  EditText searchEditText = null;
  TransparentProgressDialog transparentProgressDialog;
  private SharedPreferences mPrefs;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_geocode_reverse_geocode, container, false);
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();

    transparentProgressDialog = new TransparentProgressDialog(getContext(), R.drawable.circle_loader, "");

    mMapView.setMultiTouchControls(true);

    setupUI(view);
    infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);

    infoWindow.setTipColor(getResources().getColor(R.color.base_color));
    clearOverlays();
    return view;
  }

  private void setupUI(View view) {
    view.findViewById(R.id.search_button).setOnClickListener(this);
    searchEditText = view.findViewById(R.id.search_place);
  }


  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {
    return false;
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {

    transparentProgressDialog.show();
    MapmyIndiaReverseGeoCode.builder()
      .setLocation(p.getLatitude(), p.getLongitude())
      .build().enqueueCall(new Callback<PlaceResponse>() {
      @Override
      public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
        if (response.code() == 200) {
          if (response.body() != null) {
            List<Place> placesList = response.body().getPlaces();
            Place place = placesList.get(0);

            addOverLay(place, true);

            mMapView.invalidate();
          } else {
            Toast.makeText(getActivity(), "Not able to get value, Try again.", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
        }

        transparentProgressDialog.dismiss();
      }

      @Override
      public void onFailure(Call<PlaceResponse> call, Throwable t) {
        transparentProgressDialog.dismiss();
        Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
      }
    });



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

  @Override
  public void onClick(View v) {
    int id = v.getId();

    switch (id) {
      case R.id.search_button:
        String searchText = searchEditText.getText().toString();

        transparentProgressDialog.show();
        MapmyIndiaGeoCoding.builder()
          .setAddress(searchText)
          .build().enqueueCall(new Callback<GeoCodeResponse>() {
          @Override
          public void onResponse(Call<GeoCodeResponse> call, Response<GeoCodeResponse> response) {
            if (response.code() == 200) {
              if (response.body() != null) {
                List<GeoCode> placesList = response.body().getResults();
                addOverLays(placesList);
              } else {
                Toast.makeText(getActivity(), "Not able to get value, Try again.", Toast.LENGTH_SHORT).show();
              }
            } else {
              Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
            }

            transparentProgressDialog.dismiss();
          }

          @Override
          public void onFailure(Call<GeoCodeResponse> call, Throwable t) {
            transparentProgressDialog.dismiss();
            Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
          }
        });

        break;
    }
  }


  void addOverLays(List<GeoCode> places) {
    ArrayList<GeoPoint> points = new ArrayList<>();

    for (GeoCode place : places) {
      addOverLay(place, false);

      points.add(new GeoPoint(place.latitude, place.longitude));
    }
    mMapView.invalidate();
    mMapView.setBounds(points);
  }

  void addOverLay(Place place, boolean showInfo) {

    if (place == null)
      return;

    Marker marker = new Marker(mMapView);
    marker.setTitle(place.getLocality());
    marker.setDescription(place.getFormattedAddress());
    marker.setIcon(getResources().getDrawable(R.drawable.marker_selected));
    if (place.getLat() != null)
      marker.setPosition(new GeoPoint(Double.valueOf(place.getLat()), Double.valueOf(place.getLng())));

    marker.setInfoWindow(infoWindow);
    marker.setRelatedObject(place);

    if (showInfo)
      marker.showInfoWindow();
    mMapView.getOverlays().add(marker);


  }

  void addOverLay(GeoCode place, boolean showInfo) {

    if (place == null)
      return;

    Marker marker = new Marker(mMapView);
    marker.setTitle(place.locality);
    marker.setDescription(place.formattedAddress);
    marker.setIcon(getResources().getDrawable(R.drawable.marker_selected));
    marker.setPosition(new GeoPoint(place.latitude, place.longitude));

    marker.setInfoWindow(infoWindow);
    marker.setRelatedObject(place);

    if (showInfo)
      marker.showInfoWindow();
    mMapView.getOverlays().add(marker);


  }


  void clearOverlays() {
    mMapView.getOverlays().clear();
    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
    mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
  }
}
