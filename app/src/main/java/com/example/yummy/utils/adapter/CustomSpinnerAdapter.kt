package com.example.yummy.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.yummy.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>,
    private val icons: List<Int>
) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_with_icon, parent, false)
        val iconView = view.findViewById<ImageView>(R.id.icon)
        val textView = view.findViewById<TextView>(R.id.text)

        val item = getItem(position)

        textView.text = item

        if (position == 0) {
            iconView.visibility = View.VISIBLE
            iconView.setImageResource(icons[position])
        } else {
            iconView.visibility = View.GONE
        }

        return view
    }
}