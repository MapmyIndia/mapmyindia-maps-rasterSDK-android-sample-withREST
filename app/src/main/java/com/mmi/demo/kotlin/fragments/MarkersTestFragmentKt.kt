package com.mmi.demo.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.layers.Marker
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants
import kotlin.random.Random

class MarkersTestFragmentKt: Fragment() {
    private lateinit var mMapView: MapView
    private var mPrefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.mapview, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = (view.findViewById(R.id.mapview) as MapmyIndiaMapView).mapView

        val points: ArrayList<GeoPoint> = ArrayList()

        for (i in 0..100) {
            val position = GeoPoint(28.11493876707079 + Random.nextFloat() / 100,
                    77.3647260069847 + Random.nextFloat() / 100)
            addMarker(position)
            points.add(position)
        }

        mMapView.invalidate()
        mMapView.setBounds(points)
    }

    private fun addMarker(position: GeoPoint) {
        val marker = Marker(mMapView)
        marker.title = ""
        marker.description = ""
        marker.position = position
        marker.infoWindow = null
        mMapView.overlays.add(marker)

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
}