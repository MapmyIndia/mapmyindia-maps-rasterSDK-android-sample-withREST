package com.mmi.demo.kotlin.fragments

import android.os.Bundle
import com.mmi.demo.R
import com.mmi.demo.kotlin.model.MarkerModel
import com.mmi.layers.Marker
import com.mmi.layers.MarkerClusterer
import com.mmi.util.GeoPoint

class MarkersClusteringFragmentKt : MapBaseFragmentKt() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val markerModels: ArrayList<MarkerModel> = ArrayList()
        markerModels.add(MarkerModel(28.549356.toString() + "", 77.26780099999999.toString() + "", "28.549356,77.26780099999999", null, GeoPoint(28.549356, 77.26780099999999)));
        markerModels.add(MarkerModel(28.551844.toString() + "", 77.26749.toString() + "", "28.551844,77.26749", null, GeoPoint(28.551844, 77.26749)));
        markerModels.add(MarkerModel(28.554454.toString() + "", 77.265473.toString() + "", "28.554454,77.265473", null, GeoPoint(28.554454, 77.265473)));
        markerModels.add(MarkerModel(28.549637999999998.toString() + "", 77.262909.toString() + "", "28.549637999999998,77.262909", null, GeoPoint(28.549637999999998, 77.262909)));
        markerModels.add(MarkerModel(28.555245.toString() + "", 77.266117.toString() + "", "28.555245,77.266117", null, GeoPoint(28.555245, 77.266117)));
        markerModels.add(MarkerModel(28.558149.toString() + "", 77.269787.toString() + "", "28.558149,77.269787", null, GeoPoint(28.558149, 77.269787)));
        markerModels.add(MarkerModel(28.555369.toString() + "", 77.271042.toString() + "", "28.555369,77.271042", null, GeoPoint(28.555369, 77.271042)));
        markerModels.add(MarkerModel(28.544428.toString() + "", 77.279057.toString() + "", "28.544428,77.279057", null, GeoPoint(28.544428, 77.279057)));
        markerModels.add(MarkerModel(28.538275.toString() + "", 77.283821.toString() + "", "28.538275,77.283821", null, GeoPoint(28.538275, 77.283821)));
        markerModels.add(MarkerModel(28.536604999999998.toString() + "", 77.2872.toString() + "", "28.536604999999998,77.2872", null, GeoPoint(28.536604999999998, 77.2872)));
        markerModels.add(MarkerModel(28.538442999999997.toString() + "", 77.291921.toString() + "", "28.538442999999997,77.291921", null, GeoPoint(28.538442999999997, 77.291921)));
        markerModels.add(MarkerModel(28.542326.toString() + "", 77.30133.toString() + "", "28.542326,77.30133", null, GeoPoint(28.542326, 77.30133)));
        markerModels.add(MarkerModel(28.542609.toString() + "", 77.30211299999999.toString() + "", "28.542609,77.30211299999999", null, GeoPoint(28.542609, 77.30211299999999)));
        markerModels.add(MarkerModel(28.543042999999997.toString() + "", 77.302843.toString() + "", "28.543042999999997,77.302843", null, GeoPoint(28.543042999999997, 77.302843)));

        addOverlays(markerModels)
    }

    private fun addOverlays(markerModels: ArrayList<MarkerModel>) {
        val markerClusterer = MarkerClusterer(context)
        markerClusterer.setColor(resources.getColor(R.color.green_color))
        markerClusterer.mAnchorV = Marker.ANCHOR_CENTER

        markerClusterer.mTextAnchorV = Marker.ANCHOR_CENTER

        markerClusterer.setTextSize(12)

        val points: ArrayList<GeoPoint> = ArrayList()
        markerModels.forEach {
            val marker = Marker(mMapView)
            marker.title = it.title
            marker.description = it.description
            marker.subDescription = it.subDescription
            marker.position = it.geoPoint
            marker.icon = resources.getDrawable(R.drawable.marker_selected)
            marker.infoWindow = null
            marker.relatedObject = it
            markerClusterer.add(marker)
            points.add(it.geoPoint)
        }
        mMapView?.setBounds(points)
        mMapView?.overlays?.add(markerClusterer)

    }
}