package com.mmi.demo.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.events.MapListener
import com.mmi.events.ScrollEvent
import com.mmi.events.ZoomEvent
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.MapEventsReceiver
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants
import java.util.*

class MapEventFragmentKt: Fragment(), MapEventsReceiver {

    private lateinit var mMapView: MapView
    private lateinit var zoomLevelTextView: TextView
    private lateinit var mapCenterTextView: TextView
    private lateinit var topLeftTextView: TextView
    private lateinit var topRightTextView: TextView
    private lateinit var bottomLeftTextView: TextView
    private lateinit var bottomRightTextView: TextView
    private var mPrefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_map_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = (view.findViewById(R.id.mapview) as MapmyIndiaMapView).mapView
        val mapEventsOverlay = MapEventsOverlay(view.context!!, this)
        mMapView.overlays?.add(0, mapEventsOverlay)
        mMapView.setMultiTouchControls(true)

        setupUI(view)

        mMapView.setMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                setMapData(mMapView)
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                setMapData(mMapView)
                return false
            }
        })
    }

    private fun setMapData(mapView: MapView) {
        zoomLevelTextView.text = String.format(Locale.getDefault(), "%d", mapView.zoomLevel)
        mapCenterTextView.text = String.format(Locale.getDefault(), "%f", mapView.mapCenter.latitudeE6 / 1E6, mapView.mapCenter.longitudeE6.toDouble() / 1E6)
        topLeftTextView.text = String.format(Locale.getDefault(), "%f",  mapView.boundingBox.latNorthE6 / 1E6, mapView.boundingBox.lonEastE6 / 1E6)
        topRightTextView.text = String.format(Locale.getDefault(), "%f",mapView.boundingBox.latNorthE6 / 1E6, mapView.boundingBox.lonWestE6 / 1E6)
        bottomLeftTextView.text = String.format(Locale.getDefault(), "%f", mapView.boundingBox.latSouthE6 / 1E6, mapView.boundingBox.lonEastE6 / 1E6)
        bottomRightTextView.text = String.format(Locale.getDefault(), "%f", mapView.boundingBox.latSouthE6 / 1E6, mapView.boundingBox.lonWestE6 / 1E6)
    }

    private fun setupUI(view: View) {
        zoomLevelTextView = view.findViewById(R.id.zoom_level_text_view)
        mapCenterTextView = view.findViewById(R.id.map_center_text_view)
        topLeftTextView = view.findViewById(R.id.top_left_text_view)
        topRightTextView = view.findViewById(R.id.top_right_text_view)
        bottomLeftTextView = view.findViewById(R.id.bottom_left_text_view)
        bottomRightTextView = view.findViewById(R.id.bottom_right_text_view)
    }

    override fun longPressHelper(p0: GeoPoint?): Boolean {
        return false
    }

    override fun singleTapConfirmedHelper(p0: GeoPoint?): Boolean {
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

}