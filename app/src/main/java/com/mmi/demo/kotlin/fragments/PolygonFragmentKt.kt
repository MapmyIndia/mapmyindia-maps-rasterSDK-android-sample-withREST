package com.mmi.demo.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.demo.kotlin.model.MarkerModel
import com.mmi.events.MapListener
import com.mmi.events.ScrollEvent
import com.mmi.events.ZoomEvent
import com.mmi.layers.MapEventsOverlay
import com.mmi.layers.MapEventsReceiver
import com.mmi.layers.Polygon
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants

class PolygonFragmentKt: Fragment(), MapEventsReceiver {

    private lateinit var mMapView: MapView
    private var mPrefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_polygon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = (view.findViewById(R.id.mapview) as MapmyIndiaMapView).mapView
        val mapEventsOverlay = MapEventsOverlay(view.context!!, this)
        mMapView.overlays?.add(0, mapEventsOverlay)
        mMapView.setMultiTouchControls(true)

        val markerModels: MutableList<MarkerModel> = ArrayList()
        markerModels.add(MarkerModel(28.549356.toString() + "", 77.26780099999999.toString() + "", "Mapmyindia Head Office New Delhi,68,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.549356, 77.26780099999999)))
        markerModels.add(MarkerModel(28.551844.toString() + "", 77.26749.toString() + "", "Juice Shop,261,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.551844, 77.26749)))
        markerModels.add(MarkerModel(28.554454.toString() + "", 77.265473.toString() + "", "Modi Mill Bus Stop,Bhakti Vedant Swami Marg,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.554454, 77.265473)))
        markerModels.add(MarkerModel(28.549637999999998.toString() + "", 77.262909.toString() + "", "Dda Parking,Kalkaji Mandir Flyover U Turn,Okhla Industrial Estate Phase 3, New Delhi,Delhi\n", null, GeoPoint(28.549637999999998, 77.262909)))
        markerModels.add(MarkerModel(28.555245.toString() + "", 77.266117.toString() + "", "Modi Flour Mills,Okhla Flyover,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.555245, 77.266117)))
        markerModels.add(MarkerModel(28.558149.toString() + "", 77.269787.toString() + "", "Taxi Stand,Okhla Flyover,Ishwar Nagar, New Delhi,Delhi", null, GeoPoint(28.558149, 77.269787)))
        markerModels.add(MarkerModel(28.555369.toString() + "", 77.271042.toString() + "", "Basil,83,Sukhdev Vihar, New Delhi,Delhi", null, GeoPoint(28.555369, 77.271042)))
        markerModels.add(MarkerModel(28.544428.toString() + "", 77.279057.toString() + "", "Harkesh Nagar Bus Stop,Mathura Road/Nh 2,Jasola Vihar, New Delhi,Delhi", null, GeoPoint(28.544428, 77.279057)))
        markerModels.add(MarkerModel(28.538275.toString() + "", 77.283821.toString() + "", "Jasola Apollo Metro Station,Sarita Vihar Flyover/Nh 2,Jasola District Centre, New Delhi,Delhi", null, GeoPoint(28.538275, 77.283821)))
        markerModels.add(MarkerModel(28.536604999999998.toString() + "", 77.2872.toString() + "", "Sarita Vihar Bus Stop,Gd Birla Marg,Jasola District Centre, New Delhi,Delhi", null, GeoPoint(28.536604999999998, 77.2872)))
        markerModels.add(MarkerModel(28.538442999999997.toString() + "", 77.291921.toString() + "", "Sarita Vihar Pocket K Bus Stop,Gd Birla Marg,Jasola Vihar, New Delhi,Delhi", null, GeoPoint(28.538442999999997, 77.291921)))
        markerModels.add(MarkerModel(28.542326.toString() + "", 77.30133.toString() + "", "Addidas,Addidas,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, GeoPoint(28.542326, 77.30133)))
        markerModels.add(MarkerModel(28.542609.toString() + "", 77.30211299999999.toString() + "", "Central Bank Of India,G32/2,Abul Fazal Enclave Part 2 (Block G), New Delhi,Delhi", null, GeoPoint(28.542609, 77.30211299999999)))
        markerModels.add(MarkerModel(28.543042999999997.toString() + "", 77.302843.toString() + "", "Shaheen Bagh Bus Stop,Redtape,Maulana Azad Road,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, GeoPoint(28.543042999999997, 77.302843)))


        val points: ArrayList<GeoPoint> = ArrayList()
        markerModels.forEach {
            points.add(it.geoPoint)
        }

        val polygon = Polygon(context)
        polygon.fillColor = Color.BLUE

        polygon.points = points

        mMapView.setMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return false
            }
        })

        mMapView.overlays.add(polygon)
        mMapView.setBounds(points)
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