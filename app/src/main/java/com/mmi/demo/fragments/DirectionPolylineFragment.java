package com.mmi.demo.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import com.mmi.demo.R;
import com.mmi.demo.util.TransparentProgressDialog;
import com.mmi.layers.MapEventsOverlay;
import com.mmi.layers.Marker;
import com.mmi.layers.PathOverlay;
import com.mmi.services.api.directions.DirectionsCriteria;
import com.mmi.services.api.directions.MapmyIndiaDirections;
import com.mmi.services.api.directions.models.DirectionsResponse;
import com.mmi.services.api.directions.models.DirectionsRoute;
import com.mmi.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mohammad Akram on 03-04-2015
 */
public class DirectionPolylineFragment extends MapBaseFragment implements ISimpleDialogListener {
  public final String TAG = DirectionPolylineFragment.class.getSimpleName();

  final int REQUEST_CODE = 5;
  GeoPoint selectedPoint = null;
  GeoPoint startPoint = null;
  GeoPoint endPoint = null;
  TransparentProgressDialog transparentProgressDialog;
  ArrayList<GeoPoint> viaPoints = new ArrayList<>();

  @Override
  public String getSampleTitle() {
    return "Direction Polyline";
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);


  }

  public void addPolyline(final ArrayList<GeoPoint> geoPoints, final boolean setBound) {

    PathOverlay pathOverlay = new PathOverlay(getActivity());
    pathOverlay.setColor(getResources().getColor(R.color.base_color));
    pathOverlay.setWidth(10);
    pathOverlay.setPoints(geoPoints);
    mMapView.getOverlays().add(pathOverlay);
    mMapView.postInvalidate();

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (setBound)
          mMapView.setBounds(geoPoints);
      }
    });

  }


  public void addMarker(GeoPoint point, boolean isVia) {
    Marker marker = new Marker(mMapView);
    if (isVia)
      marker.setIcon(getResources().getDrawable(R.drawable.marker_selected));

    marker.setPosition(point);
    marker.setInfoWindow(null);

    mMapView.getOverlays().add(marker);
    mMapView.postInvalidate();
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {
    selectedPoint = p;
    List<Point> list = new ArrayList<>();
    list.add(Point.fromLngLat(77.705956, 28.984644));
    list.add(Point.fromLngLat(77.696732, 29.471397));
    getDirections(Point.fromLngLat(77.216721, 28.644800), Point.fromLngLat(77.226145, 28.942162), list);

//        SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setTargetFragment(this, REQUEST_CODE)
//                .setTitle(R.string.driving_directions).setMessage(R.string.set_as).setPositiveButtonText(R.string.set_departure)
//                .setNegativeButtonText(R.string.set_destination).setNeutralButtonText(R.string.set_viapoint).show();
    return super.longPressHelper(p);
  }

  @Override
  public void onNegativeButtonClicked(int requestCode) {
    switch (requestCode) {
      case REQUEST_CODE:
        startPoint = selectedPoint;
        addMarker(selectedPoint, false);
        if (endPoint != null && startPoint != null)
//                    getDirections(startPoint, endPoint, viaPoints);
          break;
    }
  }


  @Override
  public void onNeutralButtonClicked(int requestCode) {
    switch (requestCode) {
      case REQUEST_CODE:
        viaPoints.add(selectedPoint);
        addMarker(selectedPoint, true);
        if (endPoint != null && startPoint != null)
//                    getDirections(startPoint, endPoint, viaPoints);
          break;
    }
  }

  @Override
  public void onPositiveButtonClicked(int requestCode) {
    switch (requestCode) {
      case REQUEST_CODE:
        endPoint = selectedPoint;
        addMarker(selectedPoint, false);
        if (endPoint != null && startPoint != null)
//                    getDirections(startPoint, endPoint, viaPoints);
          break;
    }
  }


  void getDirections(final Point startPointLocal, final Point endPointLocal, final List<Point> viaPoints) {
    transparentProgressDialog = new TransparentProgressDialog(getContext(), R.drawable.circle_loader, "");
    transparentProgressDialog.show();
    clearOverlays();


    MapmyIndiaDirections.Builder builder = MapmyIndiaDirections.builder()
      .origin(startPointLocal)
      .steps(true)
      .resource(DirectionsCriteria.RESOURCE_ROUTE_ETA)
      .profile(DirectionsCriteria.PROFILE_DRIVING)
      .overview(DirectionsCriteria.OVERVIEW_FULL)
      .destination(endPointLocal);


    for (Point point : viaPoints) {
      builder.addWaypoint(point);
    }
    builder.build().enqueueCall(new Callback<DirectionsResponse>() {
      @Override
      public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        if (response.code() == 200) {
          if (response.body() != null) {
            DirectionsResponse directionsResponse = response.body();
            List<DirectionsRoute> tripList = directionsResponse.routes();
//                        List<com.mmi.services.api.directions.legacy.model.Trip> tripList = results.getTrips();
            if (tripList.size() > 0) {
              List<Point> pointList = PolylineUtils.decode(tripList.get(0).geometry(), 6);
              ArrayList<GeoPoint> geoPoints = new ArrayList<>();
              for (Point point : pointList) {
                geoPoints.add(new GeoPoint(point.latitude(), point.longitude()));
              }
              addPolyline(geoPoints, true);
            }
          }
        } else {
          Toast.makeText(getActivity(), response.message(), Toast.LENGTH_LONG).show();
        }
//                addMarker(startPoint, false);
//                addMarker(endPoint, false);
        startPoint = null;
        endPoint = null;
        transparentProgressDialog.dismiss();
      }

      @Override
      public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        transparentProgressDialog.dismiss();
        t.printStackTrace();
      }
    });

  }

  void clearOverlays() {
    mMapView.getOverlays().clear();
    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
    mMapView.getOverlays().add(mLocationOverlay);
    mMapView.getOverlays().add(mapEventsOverlay);

  }
}
