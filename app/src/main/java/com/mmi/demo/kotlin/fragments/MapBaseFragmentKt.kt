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
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.MapEventsReceiver
import com.mmi.layers.UserLocationOverlay
import com.mmi.layers.location.GpsLocationProvider
import com.mmi.layers.location.IFollowLocationListener
import com.mmi.layers.location.IMyLocationConsumer
import com.mmi.util.GeoPoint
import com.mmi.util.LogUtils
import com.mmi.util.constants.MapViewConstants

/**
 * A simple [Fragment] subclass.
 */
open class MapBaseFragmentKt : Fragment(), MapEventsReceiver {

    companion object {
        private val TAG: String = MapBaseFragmentKt::class.java.simpleName
    }

    internal var mPrefs: SharedPreferences? = null
    internal var mMapView: MapView? = null
    internal var mLocationOverlay: UserLocationOverlay? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val mapmyIndiaMapView = MapmyIndiaMapView(context)

        mMapView = mapmyIndiaMapView.mapView
        mMapView?.setMultiTouchControls(true)
        mMapView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        return mapmyIndiaMapView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMapView?.maxZoomLevel = 18
        mMapView?.minZoomLevel = 4

        val source = GpsLocationProvider(context)
        this.mLocationOverlay = UserLocationOverlay(source, mMapView)

        mLocationOverlay?.setOnLocationClickListener {

        }
        val mapEventsOverlay = MapEventsOverlay(context, this)
        mMapView?.overlays?.add(0, mapEventsOverlay)
        mMapView?.overlays?.add(mLocationOverlay)

        mLocationOverlay?.enableMyLocation()


        source.secondaryConsumer = IMyLocationConsumer { location, iMyLocationProvider ->
            if (location == null) {
                return@IMyLocationConsumer
            }
        }

        mLocationOverlay?.followLocationListener = object : IFollowLocationListener {
            override fun followMeDisabled() {

            }

            override fun followMeEnabled() {

            }
        }

        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

    }

    override fun onPause() {
        super.onPause()
        val editor: SharedPreferences.Editor? = mPrefs?.edit()
        editor?.putInt(MapViewConstants.PREFS_SCROLL_X, mMapView?.scrollX!!)
        editor?.putInt(MapViewConstants.PREFS_SCROLL_Y, mMapView?.scrollY!!)
        editor?.putInt(MapViewConstants.PREFS_ZOOM_LEVEL, mMapView?.zoomLevel!!)
        editor?.commit()

        LogUtils.LOGE(TAG, "onPause")
        LogUtils.LOGE(TAG, mMapView?.scrollX.toString())
        LogUtils.LOGE(TAG, mMapView?.scrollY.toString() + "")
        LogUtils.LOGE(TAG, mMapView?.zoomLevel.toString() + "")

        mLocationOverlay!!.disableMyLocation()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.setZoom(mPrefs?.getInt(MapViewConstants.PREFS_ZOOM_LEVEL, 5)!!)
        mMapView?.scrollTo(mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_X, 5)!!, mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_Y, 5)!!)

        LogUtils.LOGE(TAG, "onResume")
        LogUtils.LOGE(TAG, mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_X, 5).toString())
        LogUtils.LOGE(TAG, mPrefs?.getInt(MapViewConstants.PREFS_SCROLL_Y, 5).toString())
        LogUtils.LOGE(TAG, mPrefs?.getInt(MapViewConstants.PREFS_ZOOM_LEVEL, 5).toString())
    }

    override fun onDetach() {
        super.onDetach()
        mMapView?.overlays?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.overlays?.clear()

        mLocationOverlay?.disableMyLocation()
    }

    override fun longPressHelper(p0: GeoPoint?): Boolean {
        return true
    }

    override fun singleTapConfirmedHelper(p0: GeoPoint?): Boolean {
        return true
    }

}
