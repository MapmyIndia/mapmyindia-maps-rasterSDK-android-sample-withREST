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
import android.widget.RadioGroup
import android.widget.Toast
import com.mmi.MapView
import com.mmi.MapmyIndiaMapView
import com.mmi.demo.R
import com.mmi.demo.kotlin.util.TransparentProgressDialog
import com.mmi.layers.BasicInfoWindow
import com.mmi.layers.Marker
import com.mmi.services.api.nearby.MapmyIndiaNearby
import com.mmi.services.api.nearby.model.NearbyAtlasResponse
import com.mmi.services.api.nearby.model.NearbyAtlasResult
import com.mmi.util.GeoPoint
import com.mmi.util.constants.MapViewConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyFragmentKt : Fragment() {

    private lateinit var mMapView: MapView
    private lateinit var transparentProgressDialog: TransparentProgressDialog
    private var mPrefs: SharedPreferences? = null
    private lateinit var infoWindow: BasicInfoWindow
    private lateinit var categoryEditTextView: EditText
    private lateinit var keywordsEditTextView: EditText
    private lateinit var latitudeEditTextView: EditText
    private lateinit var longitudeEditTextView: EditText


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPrefs = context?.getSharedPreferences(MapViewConstants.PREFS_NAME, Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_nearby, container, false)
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
        categoryEditTextView = view.findViewById(R.id.cat_editText)
        keywordsEditTextView = view.findViewById(R.id.keywords_editText)
        latitudeEditTextView = view.findViewById(R.id.latitude_edit_text)
        longitudeEditTextView = view.findViewById(R.id.longitude_edit_text)

        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.categories_radioButton) {
                categoryEditTextView.isEnabled = true
                keywordsEditTextView.isEnabled = false
            } else {
                categoryEditTextView.isEnabled = false
                keywordsEditTextView.isEnabled = true
            }
        }

        val button: Button = view.findViewById(R.id.search_button)
        button.setOnClickListener {

            var keywords: String? = keywordsEditTextView.text.toString()
            val lat: String = latitudeEditTextView.text.toString()
            val lng: String = longitudeEditTextView.text.toString()

            if (radioGroup.checkedRadioButtonId == R.id.categories_radioButton) {
                keywords = null
            }

            if (lat.isNotEmpty() && lng.isNotEmpty()) {
                if (keywords != null && keywords.isNotEmpty()) {
                    transparentProgressDialog.show()
                    MapmyIndiaNearby.builder()
                            .setLocation(lat.toDouble(), lng.toDouble())
                            .keyword(keywords)
                            .build()
                            .enqueueCall(object : Callback<NearbyAtlasResponse>{

                                override fun onResponse(call: Call<NearbyAtlasResponse>, response: Response<NearbyAtlasResponse>) {
                                    if (response.code() == 200) {
                                        if (response.body() != null) {
                                            val nearByList: List<NearbyAtlasResult> = response.body()?.suggestedLocations!!
                                            addOverLays(nearByList)
                                        } else {
                                            Toast.makeText(context, "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                                    }

                                    transparentProgressDialog.dismiss()
                                }

                                override fun onFailure(call: Call<NearbyAtlasResponse>, t: Throwable) {
                                    transparentProgressDialog.dismiss()
                                }


                            })
                }
            }
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


    private fun addOverLays(placesList: List<NearbyAtlasResult>) {
        val points: ArrayList<GeoPoint> = ArrayList()
        placesList.forEach {
            addOverLay(it, false)
            points.add(GeoPoint(it.latitude, it.longitude))
        }
        mMapView.invalidate()
        mMapView.setBounds(points)
    }

    private fun addOverLay(place: NearbyAtlasResult?, showInfo: Boolean) {
        if (place == null) {
            return
        }

        val marker = Marker(mMapView)
        marker.title = place.getPlaceName()
        marker.description = place.getPlaceAddress()
        marker.icon = resources.getDrawable(R.drawable.marker_selected)
        marker.position = GeoPoint(place.latitude, place.longitude)
        marker.infoWindow = infoWindow
        marker.relatedObject = place


        if (showInfo) {
            marker.showInfoWindow()
        }
        mMapView.overlays.add(marker)
    }

    private fun clearOverlays() {
        mMapView.overlays.clear()

    }


}