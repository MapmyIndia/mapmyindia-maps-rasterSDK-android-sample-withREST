package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.demo.util.TransparentProgressDialog;
import com.mmi.demo.widget.DelayAutoCompleteTextView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.services.api.nearby.MapmyIndiaNearby;
import com.mmi.services.api.nearby.model.NearbyAtlasResponse;
import com.mmi.services.api.nearby.model.NearbyAtlasResult;
import com.mmi.util.GeoPoint;
import com.mmi.util.LogUtils;
import com.mmi.util.constants.MapViewConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class NearbyFragment extends Fragment implements MapViewConstants, View.OnClickListener {

  private static final String TAG = NearbyFragment.class.getSimpleName();

  EditText categoryEditTextView;

  MapView mMapView = null;
  BasicInfoWindow infoWindow;
  DelayAutoCompleteTextView searchEditText = null;
  TransparentProgressDialog transparentProgressDialog;
  private SharedPreferences mPrefs;
  private EditText keywordsEditTextView;
  private EditText latitudeEditTextView;
  private EditText longitudeEditTextView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_nearby, container, false);
    mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.mapview)).getMapView();

    mMapView.setMultiTouchControls(true);

    setupUI(view);
    infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);

    infoWindow.setTipColor(getResources().getColor(R.color.base_color));
    transparentProgressDialog = new TransparentProgressDialog(getContext(), R.drawable.circle_loader, "");
    clearOverlays();
    return view;
  }

  private void setupUI(View view) {

    // view.findViewById(R.id.search_button).setOnClickListener(this);
    categoryEditTextView = (EditText) view.findViewById(R.id.cat_editText);
    keywordsEditTextView = (EditText) view.findViewById(R.id.keywords_editText);
    latitudeEditTextView = (EditText) view.findViewById(R.id.latitude_edit_text);
    longitudeEditTextView = (EditText) view.findViewById(R.id.longitude_edit_text);
    final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.categories_radioButton) {
          categoryEditTextView.setEnabled(true);
          keywordsEditTextView.setEnabled(false);
        } else {
          categoryEditTextView.setEnabled(false);
          keywordsEditTextView.setEnabled(true);

        }
      }
    });

    view.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        try {
          String category = categoryEditTextView.getText().toString();
          String keywords = keywordsEditTextView.getText().toString();

          String lat = latitudeEditTextView.getText().toString();

          String lng = longitudeEditTextView.getText().toString();

          if (radioGroup.getCheckedRadioButtonId() == R.id.categories_radioButton)
            keywords = null;
          else
            category = null;

          if (lat.length() > 0 && lng.length() > 0) {
            GeoPoint point = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
            if ((keywords != null && keywords.length() > 0)) {
              transparentProgressDialog.show();

              MapmyIndiaNearby.builder()
                .setLocation(Double.parseDouble(lat), Double.parseDouble(lng))
                .keyword(keywords)
                .build()
                .enqueueCall(new Callback<NearbyAtlasResponse>() {
                  @Override
                  public void onResponse(Call<NearbyAtlasResponse> call, Response<NearbyAtlasResponse> response) {

                    if (response.code() == 200) {
                      if (response.body() != null) {
                        ArrayList<NearbyAtlasResult> nearByList = response.body().getSuggestedLocations();
                        if (nearByList.size() > 0) {
                          addOverLays(nearByList);
                        }
                      } else {
                        Toast.makeText(getContext(), "Not able to get value, Try again.", Toast.LENGTH_SHORT).show();
                      }
                    } else {
                      Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                    }

                    transparentProgressDialog.dismiss();
                  }


                  @Override
                  public void onFailure(Call<NearbyAtlasResponse> call, Throwable t) {
                    transparentProgressDialog.dismiss();
                  }
                });
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });


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

  @Override
  public void onClick(View v) {
    int id = v.getId();

    switch (id) {
      case R.id.search_button:

        break;
    }
  }


  void addOverLays(ArrayList<NearbyAtlasResult> places) {
    ArrayList<GeoPoint> points = new ArrayList<>();

    for (NearbyAtlasResult place : places) {
      addOverLay(place, false);

      points.add(new GeoPoint(Double.valueOf(place.getLatitude()), Double.valueOf(place.getLongitude())));
    }
    mMapView.postInvalidate();
    mMapView.setBounds(points);
  }


  void addOverLay(NearbyAtlasResult place, boolean showInfo) {

    if (place == null)
      return;

    Marker marker = new Marker(mMapView);
    marker.setTitle(place.getPlaceName());
    marker.setDescription(place.getPlaceAddress());
    marker.setIcon(getResources().getDrawable(R.drawable.marker_selected));
    marker.setPosition(new GeoPoint(Double.valueOf(place.getLatitude()), Double.valueOf(place.getLongitude())));

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
