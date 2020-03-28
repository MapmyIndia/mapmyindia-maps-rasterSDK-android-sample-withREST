package com.mmi.demo.kotlin.fragments

import android.os.Bundle
import com.mmi.demo.R
import com.mmi.demo.kotlin.model.MarkerModel
import com.mmi.demo.kotlin.util.Data
import com.mmi.demo.kotlin.util.Utils
import com.mmi.layers.BasicInfoWindow
import com.mmi.layers.Marker
import com.mmi.util.GeoPoint

class MarkerInfoWindowFragmentKt: MapBaseFragmentKt() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val markerModels: MutableList<MarkerModel> = ArrayList()
        markerModels.add(MarkerModel("Mapmyindia Head Office New Delhi", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "68,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.549356, 77.26780099999999)))
        markerModels.add(MarkerModel("Juice Shop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "261,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.551844, 77.26749)))
        markerModels.add(MarkerModel("Modi Mill Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Bhakti Vedant Swami Marg,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.554454, 77.265473)))
        markerModels.add(MarkerModel("Dda Parking", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Kalkaji Mandir Flyover U Turn,Okhla Industrial Estate Phase 3, New Delhi,Delhi\n", null, GeoPoint(28.549637999999998, 77.262909)))
        markerModels.add(MarkerModel("Modi Flour Mills", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Okhla Flyover,Okhla Industrial Estate Phase 3, New Delhi,Delhi", null, GeoPoint(28.555245, 77.266117)))
        markerModels.add(MarkerModel("Taxi Stand", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Okhla Flyover,Ishwar Nagar, New Delhi,Delhi", null, GeoPoint(28.558149, 77.269787)))
        markerModels.add(MarkerModel("Basil", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "83,Sukhdev Vihar, New Delhi,Delhi", null, GeoPoint(28.555369, 77.271042)))
        markerModels.add(MarkerModel("Harkesh Nagar Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Mathura Road/Nh 2,Jasola Vihar, New Delhi,Delhi", null, GeoPoint(28.544428, 77.279057)))
        markerModels.add(MarkerModel("Jasola Apollo Metro Station", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Sarita Vihar Flyover/Nh 2,Jasola District Centre, New Delhi,Delhi", null, GeoPoint(28.538275, 77.283821)))
        markerModels.add(MarkerModel("Sarita Vihar Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Gd Birla Marg,Jasola District Centre, New Delhi,Delhi", null, GeoPoint(28.536604999999998, 77.2872)))
        markerModels.add(MarkerModel("Sarita Vihar Pocket K Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Gd Birla Marg,Jasola Vihar, New Delhi,Delhi", null, GeoPoint(28.538442999999997, 77.291921)))
        markerModels.add(MarkerModel("Addidas", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Abul Fazal Enclave Part 2, New Delhi,Delhi", null, GeoPoint(28.542326, 77.30133)))
        markerModels.add(MarkerModel("Central Bank Of India", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "G32/2,Abul Fazal Enclave Part 2 (Block G), New Delhi,Delhi", null, GeoPoint(28.542609, 77.30211299999999)))
        markerModels.add(MarkerModel("Shaheen Bagh Bus Stop", Data.URLS[Utils.randInt(0, Data.URLS.size - 1)], "Redtape,Maulana Azad Road,Abul Fazal Enclave Part 2, New Delhi,Delhi", null, GeoPoint(28.543042999999997, 77.302843)))

        addOverLays(markerModels)
    }

    private fun addOverLays(markerModels: List<MarkerModel>) {
        val points: ArrayList<GeoPoint> = ArrayList()

        val infoWindow = BasicInfoWindow(R.layout.tooltip, mMapView)
        infoWindow.setTipColor(resources.getColor(R.color.base_color))
        markerModels.forEach {
            val marker = Marker(mMapView)
            marker.title = it.title
            marker.description = it.description
            marker.subDescription = it.subDescription
            marker.position = it.geoPoint

            marker.infoWindow = infoWindow
            marker.relatedObject = it
            mMapView?.overlays?.add(marker)
            points.add(it.geoPoint)
        }

        mMapView?.invalidate()
        mMapView?.setBounds(points)

        mMapView?.invalidate()
    }

    override fun singleTapConfirmedHelper(p0: GeoPoint?): Boolean {
        BasicInfoWindow.closeAllInfoWindowsOn(mMapView)
        return super.singleTapConfirmedHelper(p0)
    }
}