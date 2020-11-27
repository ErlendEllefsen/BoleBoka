package com.example.boleboka

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class Marker(context:Context, layoutResource:Int):MarkerView(context, layoutResource) {
    private val tvContent:TextView
    init{
        // this markerview only displays a textview
        tvContent = findViewById(R.id.tvStats) as TextView
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    override fun refreshContent(e:Entry, highlight:Highlight) {
        tvContent.text = "Something" // set the entry-value as the display text
    }
    fun getXOffset(xAxis:Float):Int {
        // this will center the marker-view horizontally
        return -(width / 2)
    }
    fun getYOffset(yAxis:Float):Int {
        // this will cause the marker-view to be above the selected value
        return -height
    }
}
