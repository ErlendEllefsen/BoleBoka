package com.example.boleboka

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.text.parseAsHtml
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.core.Tag
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.exercise_items.*
import kotlinx.android.synthetic.main.fragment_chart.*

class Chart : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var datalist: MutableList<Data>
    private lateinit var listView: ListView


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
        entries.add(Entry(15f, 16f))
        entries.add(Entry(23f, 23f))

        val vl = LineDataSet(entries, "Workout")
        vl.setDrawValues(true)
        vl.setDrawFilled(false)
        vl.lineWidth = 3f
        vl.valueTextSize = 13f
        vl.fillAlpha = R.color.colorFail

        lineChart.xAxis.labelRotationAngle = 0f
        lineChart.data = LineData(vl)
        lineChart.axisRight.isEnabled = true
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Date"
        lineChart.setNoDataText("No data found")
        lineChart.animateX(1800, Easing.EaseInExpo)

        val markerView = Marker(requireActivity().applicationContext, R.layout.fragment_chart)
        lineChart.marker = markerView


    }


    private fun setData(count: Int) {
        val intoLoop = input.text.toString()
        val entries = ArrayList<Entry>()
        val test = intoLoop.toInt()
        for (i in 0..test )
    }

    private fun getData() {
        Log.e("Getdata", "Hallo")
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()
        val database = FirebaseDatabase.getInstance().reference

        val readData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sb = StringBuilder()
                    for (d in snapshot.children){
                        val ovelse = d.child(uID).child("Userdata").value
                        val dato = d.child(uID).child("Workouts").value
                        sb.append("$ovelse\n $dato\n")
                    }
                textView5.text = sb
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Chart", "Noe galt!")
            }
        }
        database.addValueEventListener(readData)
        database.addListenerForSingleValueEvent(readData)
    }
}



}
