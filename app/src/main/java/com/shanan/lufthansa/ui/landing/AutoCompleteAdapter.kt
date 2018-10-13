package com.shanan.lufthansa.ui.landing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.shanan.lufthansa.R
import com.shanan.lufthansa.model.Airport

class AutoCompleteAdapter(private val mContext: Context, resource: Int, textViewResourceId: Int, private val airports: MutableList<Airport>) : ArrayAdapter<Airport>(mContext, resource, textViewResourceId, airports) {
    private var inflater: LayoutInflater? = null

    fun setItems(items: List<Airport>) {
        airports.clear()
        airports.addAll(items)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return airports.size
    }

    override fun getItem(position: Int): Airport? {
        return airports[position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var convertView = view

        if (inflater == null) {
            inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null && inflater != null) {
            convertView = inflater!!.inflate(R.layout.item_airport,
                    parent, false)
        }

        if (convertView != null) {
            val airportName = convertView.findViewById<TextView>(R.id.airportName)
            val cityCode = convertView.findViewById<TextView>(R.id.cityCode)

            val (_, _, cityCode1, _, names) = airports[position]

            airportName.text = names.name.value
            cityCode.text = cityCode1
        }

        return convertView
    }
}