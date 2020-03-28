package com.mmi.demo.kotlin.fragments

import android.widget.Toast
import com.avast.android.dialogs.fragment.SimpleDialogFragment
import com.avast.android.dialogs.iface.ISimpleDialogListener
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mmi.demo.R
import com.mmi.demo.kotlin.util.TransparentProgressDialog
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.Marker
import com.mmi.layers.PathOverlay
import com.mmi.services.api.directions.DirectionsCriteria
import com.mmi.services.api.directions.MapmyIndiaDirections
import com.mmi.services.api.directions.models.DirectionsResponse
import com.mmi.services.utils.Constants
import com.mmi.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectionPolylineFragmentKt: MapBaseFragmentKt(), ISimpleDialogListener {

    val REQUEST_CODE: Int = 5
    var selectedPoint: GeoPoint? = null
    var startPoint: GeoPoint? = null
    var endPoint: GeoPoint? = null
    var transparentProgressDialog: TransparentProgressDialog? = null
    val viaPoints: ArrayList<GeoPoint> = ArrayList()

    fun addPolyline(geoPoints: ArrayList<GeoPoint>, setBound: Boolean) {
        val pathOverlay: PathOverlay = PathOverlay(activity)
        pathOverlay.color = resources.getColor(R.color.base_color)
        pathOverlay.width = 10.0f
        pathOverlay.points = geoPoints
        mMapView?.overlays?.add(pathOverlay)
        mMapView?.postInvalidate()
        if(setBound) {
            mMapView?.setBounds(geoPoints)
        }

    }

    fun addMarker(point: GeoPoint, isVia: Boolean) {
        val marker = Marker(mMapView)
        if(isVia) {
            marker.icon = resources.getDrawable(R.drawable.marker_selected)
        }

        marker.position = point
        marker.infoWindow = null
        mMapView?.overlays?.add(marker)
        mMapView?.postInvalidate()
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        selectedPoint = p
        SimpleDialogFragment.createBuilder(activity, activity?.supportFragmentManager)
                .setTargetFragment(this, REQUEST_CODE)
                .setTitle(R.string.driving_directions).setMessage(R.string.set_as).setPositiveButtonText(R.string.set_departure)
                .setNegativeButtonText(R.string.set_destination).setNeutralButtonText(R.string.set_viapoint).show()
        return super.longPressHelper(p)
    }

    override fun onNegativeButtonClicked(requestCode: Int) {
        if(requestCode == REQUEST_CODE) {
            startPoint = selectedPoint
            addMarker(selectedPoint!!, false)

            if(endPoint != null && startPoint != null) {
                getDirections(startPoint!!, endPoint!!, viaPoints)
            }
        }
    }


    override fun onNeutralButtonClicked(requestCode: Int) {
        if(requestCode == REQUEST_CODE) {
            viaPoints.add(selectedPoint!!)
            addMarker(selectedPoint!!, true)
            if(endPoint != null && startPoint != null) {
                getDirections(startPoint!!, endPoint!!, viaPoints)
            }
        }
    }

    override fun onPositiveButtonClicked(requestCode: Int) {
        if(requestCode == REQUEST_CODE) {
            endPoint = selectedPoint
            addMarker(selectedPoint!!, false)

            if(endPoint != null && startPoint != null) {
                getDirections(startPoint!!, endPoint!!, viaPoints)
            }
        }
    }

    private fun getDirections(startPointLocal: GeoPoint, endPointLocal: GeoPoint, viaPoints: ArrayList<GeoPoint>) {
        transparentProgressDialog = TransparentProgressDialog(context!!, R.drawable.circle_loader, "")
        transparentProgressDialog?.show()
        clearOverlays()

        val builder: MapmyIndiaDirections.Builder = MapmyIndiaDirections.builder()
                .steps(true)
                .origin(Point.fromLngLat(startPointLocal.longitude, startPointLocal.latitude))
                .destination(Point.fromLngLat(endPointLocal.longitude, endPointLocal.latitude))
                .overview(DirectionsCriteria.OVERVIEW_FULL)

        viaPoints.forEach {
            builder.addWaypoint(Point.fromLngLat(it.longitude, it.latitude))
        }

        builder.build().enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if(response.isSuccessful) {
                    val directionsResponse: DirectionsResponse? = response.body()
                    if(directionsResponse != null) {
                        val pointList: MutableList<Point> = ArrayList()
                        directionsResponse.routes().forEach {
                            pointList.addAll(PolylineUtils.decode(it.geometry()!!, Constants.PRECISION_6))
                        }
                        val geoPoints: ArrayList<GeoPoint> = ArrayList()
                        pointList.forEach {
                            geoPoints.add(GeoPoint(it.latitude(), it.longitude()))
                        }
                        addPolyline(geoPoints, false)
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }

                addMarker(startPoint!!, false)
                addMarker(endPoint!!, false)
                viaPoints.forEach {
                    addMarker(it, true)
                }

                transparentProgressDialog?.dismiss()
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                t.printStackTrace()
                transparentProgressDialog?.dismiss()
            }


        })
    }

    private fun clearOverlays() {
        mMapView?.overlays?.clear()
        val mapEventsOverlay = MapEventsOverlay(activity, this)
        mMapView?.overlays?.add(mLocationOverlay)
        mMapView?.overlays?.add(mapEventsOverlay)
    }
}