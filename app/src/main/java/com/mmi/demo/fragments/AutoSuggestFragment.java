package com.mmi.demo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.demo.R;
import com.mmi.demo.adapter.AutoCompleteAdapter;
import com.mmi.demo.util.TransparentProgressDialog;
import com.mmi.demo.widget.DelayAutoCompleteTextView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.services.api.auth.MapmyIndiaAuthentication;
import com.mmi.services.api.auth.model.AtlasAuthToken;
import com.mmi.services.api.autosuggest.model.ELocation;
import com.mmi.services.api.geocoding.GeoCode;
import com.mmi.services.api.geocoding.GeoCodeResponse;
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding;
import com.mmi.util.GeoPoint;
import com.mmi.util.LogUtils;
import com.mmi.util.constants.MapViewConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class AutoSuggestFragment extends Fragment implements MapViewConstants, View.OnClickListener {

  private static final String TAG = AutoSuggestFragment.class.getSimpleName();


  MapView mMapView = null;
  BasicInfoWindow infoWindow;
  DelayAutoCompleteTextView searchEditText = null;
  String authToken;
  String tokenType;
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
    getAuthToken();
    setupUI(view);
    infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);

    infoWindow.setTipColor(getResources().getColor(R.color.base_color));
    clearOverlays();
    return view;
  }

  private void setupUI(View view) {

    // view.findViewById(R.id.search_button).setOnClickListener(this);
    searchEditText = (DelayAutoCompleteTextView) view.findViewById(R.id.search_place);
    adapter = new AutoCompleteAdapter(getActivity());
    searchEditText.setAdapter(adapter);


    searchEditText.setLoadingIndicator(
      (ProgressBar) view.findViewById(R.id.loading_indicator));


    searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ELocation book = (ELocation) adapterView.getItemAtPosition(position);
        searchEditText.setText(book.placeName);

              /*  try {
                    String poiID = book.getPlaceId();

                    PlaceDetailsManager placeDetailsManager = new PlaceDetailsManager();
                    placeDetailsManager.getPlaceDetails(poiID, new PlaceDetailsListener() {
                        @Override
                        public void onResult(int code, final Place place) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addOverLays(place);
                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

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


  void addOverLays(GeoCode place) {
    ArrayList<GeoPoint> points = new ArrayList<>();

    // for (Place place : places) {
    addOverLay(place, false);
    points.add(new GeoPoint(place.latitude, place.longitude));
    //
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

  private void getAuthToken() {

    new MapmyIndiaAuthentication.Builder()
      .build()
      .enqueueCall(new Callback<AtlasAuthToken>() {
        @Override
        public void onResponse(Call<AtlasAuthToken> call, Response<AtlasAuthToken> response) {
          if (response.code() == 200) {
            authToken = response.body().getAccessToken();
            tokenType = response.body().getTokenType();
          }
        }

        @Override
        public void onFailure(Call<AtlasAuthToken> call, Throwable t) {
        }
      });
  }
}
