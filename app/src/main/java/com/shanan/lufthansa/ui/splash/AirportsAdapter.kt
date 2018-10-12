package com.shanan.lufthansa.ui.splash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.shanan.lufthansa.R
import com.shanan.lufthansa.model.Airport

open class AirportsAdapter(context: Context, resource: Int, list: ArrayList<Airport>) :
        ArrayAdapter<Airport>(context, resource, list) {

    var resource: Int
    var resultList: ArrayList<Airport>
    var inflater: LayoutInflater

    init {
        this.resource = resource
        this.resultList = list
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return resultList.size
    }

    override fun getItem(position: Int): Airport? {
        return resultList.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var holder: ViewHolder
        var retView: View

        if (convertView == null) {
            retView = inflater.inflate(resource, null)
            holder = ViewHolder()

            holder.name = retView.findViewById(R.id.airportName) as TextView?
            holder.city = retView.findViewById(R.id.cityCode) as TextView?
            holder.country = retView.findViewById(R.id.countryCode) as TextView?

            retView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }
        val item = resultList[position]
        holder.name?.text = item.names.name.value
        holder.city?.text = item.cityCode
        holder.country?.text = item.cityCode

        return retView
    }

    private val mFilter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as Airport).names.name.value
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint != null) {
                val suggestions = ArrayList<Airport>()

                suggestions.addAll(resultList)

                results.values = suggestions
                results.count = suggestions.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults?) {
            clear()
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll(results.values as ArrayList<Airport>)
            } else {
                // no filter, add entire original list back in
                addAll(resultList)
            }
            notifyDataSetChanged()
        }
    }

    internal class ViewHolder {
        var name: TextView? = null
        var city: TextView? = null
        var country: TextView? = null
    }

}