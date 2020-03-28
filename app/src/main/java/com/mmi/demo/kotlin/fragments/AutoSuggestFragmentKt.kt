package com.mmi.demo.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.DelayAutoCompleteTextView
import com.mmi.demo.R
import com.mmi.demo.kotlin.adapter.AutoCompleteAdapter
import com.mmi.demo.kotlin.util.TransparentProgressDialog
import com.mmi.layers.BasicInfoWindow
import com.mmi.layers.Marker
import com.mmi.services.api.autosuggest.model.ELocation
import com.mmi.services.api.geocoding.GeoCode
import com.mmi.services.api.geocoding.GeoCodeResponse
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoSuggestFragmentKt: Fragment() {

    private lateinit var mMapView: MapView
    private lateinit var transparentProgressDialog: TransparentProgressDialog
    private var mPrefs: SharedPreferences? = null
    private lateinit var infoWindow: BasicInfoWindow
    private lateinit var searchEditText: DelayAutoCompleteTextView
    private var adapter: AutoCompleteAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_auto_complete, container, false)
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
        adapter = AutoCompleteAdapter(context!!)
        searchEditText.setAdapter(adapter)
        searchEditText.setLoadingIndicator(view.findViewById(R.id.loading_indicator))

        searchEditText.setOnItemClickListener { adapterView, view1, position, id ->

            val eLocation: ELocation = adapterView.getItemAtPosition(position) as ELocation
            searchEditText.setText(eLocation.placeName)
            transparentProgressDialog.show()

            MapmyIndiaGeoCoding.builder()
                    .setAddress(eLocation.placeName)
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



    private fun addOverLays(placesList: List<GeoCode>) {
        val points: ArrayList<GeoPoint> = ArrayList()
        placesList.forEach {
            addOverLay(it, false)
            points.add(GeoPoint(it.latitude, it.longitude))
        }
        mMapView.invalidate()
        mMapView.setBounds(points)
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
    }


}