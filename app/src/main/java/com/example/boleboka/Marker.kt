package com.example.boleboka

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.Utils


class Marker(context:Context, layoutResource:Int):MarkerView(context, layoutResource) {
    private val tvStats: TextView = findViewById<TextView>(R.id.tvStats)
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    override fun refreshContent(e:Entry, highlight:Highlight) {
        if (e is LineData)
        {
            val ce = e as LineData
            tvStats.text = "" + Utils.formatNumber(ce.yMax, 0, true)
        }
        else
        {
            tvStats.text = "" + Utils.formatNumber(e.y, 0, true)
        }
        super.refreshContent(e, highlight)
    }
}

