package com.mmi.demo.kotlin.model

import com.mmi.util.GeoPoint

data class MarkerModel(
        val title: String?,
        val imageUrl: String?,
        val description: String?,
        val subDescription: String?,
        val geoPoint: GeoPoint
)