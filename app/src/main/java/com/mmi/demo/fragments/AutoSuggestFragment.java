package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.demo.adapter.AutoCompleteAdapter;
import com.mmi.demo.util.TransparentProgressDialog;
import com.mmi.demo.widget.DelayAutoCompleteTextView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.services.api.autosuggest.model.ELocation;
import com.mmi.services.api.geocoding.GeoCode;
import com.mmi.services.api.geocoding.GeoCodeResponse;
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding;
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
public class AutoSuggestFragment extends Fragment implements MapViewConstants {


  MapView mMapView = null;
  BasicInfoWindow infoWindow;
  DelayAutoCompleteTextView searchEditText = null;
  TransparentProgressDialog transparentProgressDialog;
  private SharedPreferences mPrefs;
  private AutoCompleteAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_auto_complete, container, false);
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

    searchEditText = view.findViewById(R.id.search_place);
    adapter = new AutoCompleteAdapter(getActivity());
    searchEditText.setAdapter(adapter);


    searchEditText.setLoadingIndicator(view.findViewById(R.id.loading_indicator));


    searchEditText.setOnItemClickListener((adapterView, view1, position, id) -> {
      ELocation book = (ELocation) adapterView.getItemAtPosition(position);
      searchEditText.setText(book.placeName);
      transparentProgressDialog.show();
      MapmyIndiaGeoCoding.builder()
        .setAddress(book.placeName)
        .build().enqueueCall(new Callback<GeoCodeResponse>() {
        @Override
        public void onResponse(Call<GeoCodeResponse> call, Response<GeoCodeResponse> response) {
          if (response.code() == 200) {
            if (response.body() != null) {
              List<GeoCode> placesList = response.body().getResults();
              GeoCode place = placesList.get(0);
              addOverLays(place);
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
          Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
          transparentProgressDialog.dismiss();
        }
      });

    });
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



  void addOverLays(GeoCode place) {
    ArrayList<GeoPoint> points = new ArrayList<>();
    addOverLay(place, false);
    points.add(new GeoPoint(place.latitude, place.longitude));
    mMapView.postInvalidate();
    mMapView.setBounds(points);
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
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

}
