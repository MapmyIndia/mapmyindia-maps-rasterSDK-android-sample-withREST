package com.mmi.demo.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.demo.kotlin.util.TransparentProgressDialog
import com.mmi.layers.BasicInfoWindow
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.MapEventsReceiver
import com.mmi.layers.Marker
import com.mmi.services.api.Place
import com.mmi.services.api.PlaceResponse
import com.mmi.services.api.geocoding.GeoCode
import com.mmi.services.api.geocoding.GeoCodeResponse
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding
import com.mmi.services.api.reversegeocode.MapmyIndiaReverseGeoCode
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeoCodeReverseGeoCodeFragmentKt : Fragment(), MapEventsReceiver, View.OnClickListener {
    private lateinit var mMapView: MapView
    private lateinit var transparentProgressDialog: TransparentProgressDialog
    private var mPrefs: SharedPreferences? = null
    private lateinit var infoWindow: BasicInfoWindow
    private lateinit var searchEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_geocode_reverse_geocode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = (view.findViewById(R.id.mapview) as MapmyIndiaMapView).mapView

        transparentProgressDialog = TransparentProgressDialog(context!!, R.drawable.circle_loader, "")
        mMapView.setMultiTouchControls(true)

        setupUI(view)
        infoWindow = BasicInfoWindow(R.layout.tooltip, mMapView)
        infoWindow.setTipColor(resources.getColor(R.color.base_color))
        clearOverlays()


    }

    private fun setupUI(view: View) {
        searchEditText = view.findViewById(R.id.search_place)
        view.findViewById<ImageButton>(R.id.search_button).setOnClickListener(this)
    }

    override fun singleTapConfirmedHelper(p0: GeoPoint?): Boolean {
        return false
    }

    override fun longPressHelper(p: GeoPoint): Boolean {
        transparentProgressDialog.show()

        MapmyIndiaReverseGeoCode.builder()
                .setLocation(p.latitude, p.longitude)
                .build().enqueueCall(object : Callback<PlaceResponse> {

                    override fun onResponse(call: Call<PlaceResponse>, response: Response<PlaceResponse>) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val placeList: List<Place>? = response.body()?.places
                                val place: Place? = placeList?.get(0)

                                addOverLay(place, true)
                                mMapView.invalidate()

                            } else {
                                Toast.makeText(context, "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        }

                        transparentProgressDialog.dismiss()
                    }

                    override fun onFailure(call: Call<PlaceResponse>, t: Throwable) {
                        t.printStackTrace()
                        transparentProgressDialog.dismiss()
                    }

                })
        return false
    }


    override fun onPause() {
        super.onPause()
        val editor: SharedPreferences.Editor? = mPrefs?.edit()
        editor?.putInt(MapViewConstants.PREFS_SCROLL_X, mMapView.scrollX)
        editor?.putInt(MapViewConstants.PREFS_SCROLL_Y, mMapView.scrollY)
        editor?.putInt(MapViewConstants.PREFS_ZOOM_LEVEL, mMapView.zoomLevel)
        editor?.apply()


    }

    override fun onResume() {
        super.onResume()
        mMapView.setZoom(mPrefs?.getInt(MapViewConstants.PREFS_ZOOM_LEVEL, 5)!!)
        mMapView.scrollTo(mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_X, 5)!!, mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_Y, 5)!!)

    }


    override fun onClick(v: View?) {
        if (v?.id == R.id.search_button) {
            val searchText: String = searchEditText.text?.toString()!!
            transparentProgressDialog.show()

            MapmyIndiaGeoCoding.builder()
                    .setAddress(searchText)
                    .build().enqueueCall(object : Callback<GeoCodeResponse> {

                        override fun onResponse(call: Call<GeoCodeResponse>, response: Response<GeoCodeResponse>) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val placesList: List<GeoCode> = response.body()?.results!!
                                    addOverLays(placesList)
                                } else {
                                    Toast.makeText(context, "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                            }

                            transparentProgressDialog.dismiss()

                        }

                        override fun onFailure(call: Call<GeoCodeResponse>, t: Throwable) {
                            t.printStackTrace()
                            transparentProgressDialog.dismiss()
                        }


                    })
        }
    }

    private fun addOverLays(placesList: List<GeoCode>) {
        val points: ArrayList<GeoPoint> = ArrayList()
        placesList.forEach {
            addOverLay(it, false)
            points.add(GeoPoint(it.latitude, it.longitude))
        }
        mMapView.invalidate()
        mMapView.setBounds(points)
    }

    private fun addOverLay(place: Place?, showInfo: Boolean) {
        if(place == null) {
            return
        }

        val marker = Marker(mMapView)
        marker.title = place.locality
        marker.description = place.formattedAddress
        marker.icon = resources.getDrawable(R.drawable.marker_selected)
        marker.position = GeoPoint(place.lat.toDouble(), place.lng.toDouble())
        marker.infoWindow = infoWindow
        marker.relatedObject = place


        if(showInfo) {
            marker.showInfoWindow()
        }
        mMapView.overlays.add(marker)
    }

    private fun addOverLay(place: GeoCode, showInfo: Boolean) {


        val marker = Marker(mMapView)
        marker.title = place.locality
        marker.description = place.formattedAddress
        marker.icon = resources.getDrawable(R.drawable.marker_selected)
        marker.position = GeoPoint(place.latitude, place.longitude)
        marker.infoWindow = infoWindow
        marker.relatedObject = place


        if(showInfo) {
            marker.showInfoWindow()
        }
        mMapView.overlays.add(marker)
    }

    private fun clearOverlays() {
        mMapView.overlays.clear()
        val mapEventsOverlay = MapEventsOverlay(context!!, this)
        mMapView.overlays?.add(0, mapEventsOverlay)
    }


}