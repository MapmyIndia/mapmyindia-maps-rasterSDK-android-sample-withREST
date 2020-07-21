package com.mmi.demo.kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.mmi.demo.R
import com.mmi.services.api.autosuggest.MapmyIndiaAutoSuggest
import com.mmi.services.api.autosuggest.model.AutoSuggestAtlasResponse
import com.mmi.services.api.autosuggest.model.ELocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoCompleteAdapter(private val context: Context) : BaseAdapter(), Filterable {

    private var resultList: List<ELocation> = ArrayList()
    private var suggestedList: List<ELocation>? = null

    override fun getCount(): Int {
        return resultList.size
    }

    override fun getItem(item: Int): ELocation {
        return resultList.get(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.place_list_item, parent, false)
        }

        if (view != null) {
            val textView: TextView = view.findViewById(R.id.text1)
            textView.text = getItem(position).placeName
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val suggestions: List<ELocation>? = getSuggestions(constraint.toString())

                    filterResults.values = suggestions
                    filterResults.count = suggestions?.size ?: 0
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    resultList = results.values as List<ELocation>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

        }
    }

    private fun getSuggestions(searchText: String): List<ELocation>? {
        var mapmyIndiaAutoSuggest = MapmyIndiaAutoSuggest.builder()
                .query(searchText)
                .build()
        mapmyIndiaAutoSuggest.enqueueCall(object : Callback<AutoSuggestAtlasResponse> {
            override fun onFailure(call: Call<AutoSuggestAtlasResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<AutoSuggestAtlasResponse>, response: Response<AutoSuggestAtlasResponse>) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        suggestedList = response.body()?.suggestedLocations
                    }
                }
            }

        })

        return suggestedList
    }
}