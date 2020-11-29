package com.example.boleboka

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.AxisRenderer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class Chart : Fragment() {

    val listOfKeyDate = arrayListOf<String>()
    private var labelCount by Delegates.notNull<Int>()
    private var end by Delegates.notNull<Int>()
    private var start by Delegates.notNull<Int>()
    private lateinit var firebaseAuth: FirebaseAuth
    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()
    private val firebaseOvelse =
        FirebaseDatabase.getInstance().getReference("Users").child(uID).child(
            "Stats"
        )


    @RequiresApi(Build.VERSION_CODES.O)
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
        getSpinnerData()
    }

    private fun getSpinnerData(): ArrayList<String> {
        val listOfKeyOvelse = arrayListOf<String>()
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyOvelse.add(groupKey)
                }

                val ad = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item, listOfKeyOvelse
                )
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner2.adapter = ad

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item, listOfKeyOvelse
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner2.adapter = adapter

                spinner2.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val spinnerName = listOfKeyOvelse[position]
                            lineChart.clear()
                            lineChart.notifyDataSetChanged()
                            lineChart.invalidate()
                            getStatsExercise(spinnerName)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            spinner2.prompt = "Velg en Øvelse"
                        }
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        firebaseOvelse.addListenerForSingleValueEvent(eventListener)
        return listOfKeyOvelse
    }

    val listOfReps = arrayListOf<String>()
    val listOfSets = arrayListOf<String>()
    private fun getStatsExercise(spinnerName: String): ArrayList<String> {
        val listOfKeyStats = arrayListOf<String>()
        val firebaseStats = FirebaseDatabase.getInstance().getReference("Users")
            .child(uID).child("Stats").child(spinnerName)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val vekt = ds.child("Vekt").value
                    listOfKeyStats.add("$vekt")
                    val reps = ds.child("Reps").value
                    listOfReps.add("$reps")
                    val sets = ds.child("Sets").value
                    listOfSets.add("$sets")
                    setFilterSpinner(listOfKeyStats)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        firebaseStats.addListenerForSingleValueEvent(eventListener)
        getDateExercise(spinnerName)
        return listOfKeyStats
    }

    private fun setFilterSpinner(listOfKeyStats: ArrayList<String>): ArrayList<String> {
        val noData = "Not enough data to draw the graph line, minimum 5 entries"
        val listOfFilters = arrayListOf<String>()
        listOfFilters.add("Last 30 Days")
        listOfFilters.add("Last 90 Days")
        listOfFilters.add("Last Year")
        listOfFilters.add("All time")

        val ad = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, listOfFilters
        )
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = ad

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, listOfFilters
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = adapter

        spinner3.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    lineChart.clear()
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                    if (position == 0){
                        start = 35
                        end = listOfKeyStats.size
                        val entries = ArrayList<Entry>()
                        if (listOfKeyStats.size > 4) {
                            for (index in start until end) {
                                entries.add(
                                    Entry(
                                        java.lang.Float.parseFloat(index.toString()),
                                        java.lang.Float.parseFloat(
                                            listOfKeyStats[index],
                                        ),
                                    )
                                )
                                labelCount = 10
                            }
                        }
                        else {
                            tvStats.text = noData
                        }
                        lineChart.notifyDataSetChanged()
                        lineChart.invalidate()
                        generateLineData(entries)
                    } else {
                        if (position == 1) {
                            start = 20
                            end = listOfKeyStats.size
                            val entries = ArrayList<Entry>()
                            if (listOfKeyStats.size > 4) {
                                for (index in start until end) {
                                    entries.add(
                                        Entry(
                                            java.lang.Float.parseFloat(index.toString()),
                                            java.lang.Float.parseFloat(
                                                listOfKeyStats[index],
                                            ),
                                        )
                                    )
                                    labelCount = 5
                                }
                            }
                            else {
                                tvStats.text = noData
                            }
                            lineChart.notifyDataSetChanged()
                            lineChart.invalidate()
                            generateLineData(entries)
                        } else {
                            if (position == 2) {
                                start = 5
                                end = listOfKeyStats.size
                                val entries = ArrayList<Entry>()
                                if (listOfKeyStats.size > 4) {
                                    for (index in start until end) {
                                        entries.add(
                                            Entry(
                                                java.lang.Float.parseFloat(index.toString()),
                                                java.lang.Float.parseFloat(
                                                    listOfKeyStats[index],
                                                ),
                                            )
                                        )
                                        labelCount = 8
                                    }
                                }
                                else {
                                    tvStats.text = noData
                                }
                                lineChart.notifyDataSetChanged()
                                lineChart.invalidate()
                                generateLineData(entries)
                            } else {
                                if (position == 3) {
                                    start = 0
                                    end = listOfKeyStats.size
                                    val entries = ArrayList<Entry>()
                                    if (listOfKeyStats.size > 4) {
                                        for (index in start until end) {
                                            entries.add(
                                                Entry(
                                                    java.lang.Float.parseFloat(index.toString()),
                                                    java.lang.Float.parseFloat(
                                                        listOfKeyStats[index],
                                                    ),
                                                )
                                            )
                                            labelCount = 5
                                        }
                                    }
                                    else {
                                        tvStats.text = noData
                                    }
                                    lineChart.notifyDataSetChanged()
                                    lineChart.invalidate()
                                    generateLineData(entries)
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        return listOfFilters
    }

    @SuppressLint("SetTextI18n")
    private fun getDateExercise(spinnerName: String): List<String> {
        val firebaseDato = FirebaseDatabase.getInstance().getReference("Users").child(uID).child(
            "Stats"
        ).child(spinnerName)

        val eventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyDate.add(groupKey)
                }
                listOfKeyDate.toString()
                println(listOfKeyDate)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        firebaseDato.addListenerForSingleValueEvent(eventListener)
        listOfKeyDate.clear()
        tvStats.text = "Velg et punkt for å se mer data"
        return listOfKeyDate
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun generateLineData(entries: ArrayList<Entry>): LineData {

        val xLabel = ArrayList<String>()
        for (d in 0 until entries.size)
            xLabel.add(listOfKeyDate[d])

        val lineD = LineData()
        val dataSetl = LineDataSet(entries, "Kg")

        dataSetl.setDrawValues(false)
        dataSetl.setDrawFilled(false)
        dataSetl.lineWidth = 3f
        dataSetl.valueTextSize = 15f
        dataSetl.fillAlpha = R.color.colorFail

        lineChart.xAxis.labelRotationAngle = 0f
        lineChart.data = LineData(dataSetl)
        lineChart.axisRight.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Date"
        lineChart.setViewPortOffsets(80f, 0f, 85f, 100f)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)
        xAxis.setLabelCount(labelCount, true)
        xAxis.isCenterAxisLabelsEnabled
        xAxis.setAvoidFirstLastClipping(false)


        lineChart.description.textSize = 12f
        lineChart.xAxis.textSize = 12f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisLeft.textSize = 12f
        lineChart.setNoDataText("No data found")
        lineChart.animateX(1800, Easing.EaseInExpo)

        lineChart.isHighlightPerTapEnabled = true
        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {

            @SuppressLint("SetTextI18n")
            override fun onNothingSelected() {
                tvStats.text = "Velg et punkt for å se mer data"
            }
            @SuppressLint("SetTextI18n")
            override fun onValueSelected(e: Entry, h: Highlight) {
                val dateIndex = e.x.toInt()
                tvStats.text ="Weight: " + e.y.toString() + "kg " +
                        " Dato: " + listOfKeyDate[dateIndex] +
                        " Reps: " + listOfReps[dateIndex] +
                        " Sets: " + listOfSets[dateIndex]
            }
        })
        return lineD
    }
}



