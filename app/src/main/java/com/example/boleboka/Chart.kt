package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chart.*

class Chart : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entries = ArrayList<Entry>()

        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 9f))
        entries.add(Entry(5f, 20f))
        entries.add(Entry(7f, 16f))
        entries.add(Entry(20f, 21f))

        val vl = LineDataSet(entries, "Workout")
        vl.setDrawValues(true)
        vl.setDrawFilled(false)
        vl.lineWidth = 3f
        vl.fillAlpha = R.color.colorFail

        lineChart.xAxis.labelRotationAngle = 0f
        lineChart.data = LineData(vl)
        lineChart.axisRight.isEnabled = false
        //lineChart.xAxis.axisMaximum = xAxisSize()
        //lineChart.xAxis.setLabelCount(10, true)
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Date"
        lineChart.setNoDataText("No data found")
        lineChart.animateX(1800, Easing.EaseInExpo)

        val markerView = Marker(requireActivity().applicationContext, R.layout.fragment_chart)
        lineChart.marker = markerView

    }
}
