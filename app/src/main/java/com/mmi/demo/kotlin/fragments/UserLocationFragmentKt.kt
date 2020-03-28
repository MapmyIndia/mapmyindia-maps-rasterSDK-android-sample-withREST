package com.mmi.demo.kotlin.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
import com.mmi.util.constants.MapViewConstants

class UserLocationFragmentKt: Fragment() {

    private var mLocationOverlay: UserLocationOverlay? = null
    private lateinit var mMapView: MapView
    private var mPrefs: SharedPreferences? = null

    private val locationFoundHandler: Handler = MyHandler()

      override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.mapview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = (view.findViewById(R.id.mapview) as MapmyIndiaMapView).mapView

        this.mLocationOverlay = UserLocationOverlay(GpsLocationProvider(context), mMapView)
        mLocationOverlay?.enableMyLocation()
        mMapView.overlays?.add(mLocationOverlay)

        mLocationOverlay?.runOnFirstFix {
            locationFoundHandler.sendEmptyMessage(1)
        }
        mMapView.invalidate()
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

    @SuppressLint("HandlerLeak")
    inner class MyHandler: Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg?.what == 1) {
                mMapView.setCenter(mLocationOverlay?.myLocation)
                mMapView.setZoom(mMapView.maxZoomLevel)
            }
        }
    }

}