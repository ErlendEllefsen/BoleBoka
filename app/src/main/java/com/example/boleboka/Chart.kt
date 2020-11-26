package com.example.boleboka

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chart.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.properties.Delegates


class Chart : Fragment() {

    var datoOvelse = ArrayList<String>()
    private var slutt by Delegates.notNull<Int>()
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
        setFilterSpinner()
    }

    /*
    private fun getOvelser(): ArrayList<String> {
        val listOfKeyOvelse = arrayListOf<String>()
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyOvelse.add(groupKey)
                }
                println(listOfKeyOvelse)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        firebaseOvelse.addListenerForSingleValueEvent(eventListener)
        return listOfKeyOvelse
    }

     */

    private fun setFilterSpinner(): ArrayList<String> {
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
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    datoOvelse.clear()
                    lineChart.clear()
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                    getSpinnerData()
                    /*
                    Toast.makeText(
                        requireContext(),
                        "Tidsavgrensing: $spinnerFilter",
                        Toast.LENGTH_LONG
                    ).show()

                     */
                    if (position == 0) {
                        start = 0
                        slutt = 5
                    } else {
                        if (position == 1) {
                            start = 0
                            slutt = 10
                        } else {
                            if (position == 2) {
                                start = 0
                                slutt = 10
                            } else {
                                if (position == 3) {
                                    start = 0
                                    slutt = 44
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
                            datoOvelse.clear()
                            lineChart.clear()
                            lineChart.notifyDataSetChanged()
                            lineChart.invalidate()
                            getStatsForOvelse(spinnerName)
                            /*
                            Toast.makeText(
                                requireContext(),
                                "Øvelse valgt: $spinnerName",
                                Toast.LENGTH_LONG
                            ).show()

                             */
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


    private fun getStatsForOvelse(spinnerName: String): ArrayList<String> {
        val listOfKeyStats = arrayListOf<String>()
        val firebaseStats = FirebaseDatabase.getInstance().getReference("Users")
            .child(uID).child("Stats").child(spinnerName)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val vekt = ds.child("Vekt").value
                    listOfKeyStats.add("$vekt")
                }
                generateLineData(listOfKeyStats)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        firebaseStats.addListenerForSingleValueEvent(eventListener)
        getDatoForOvelse(spinnerName)
        return listOfKeyStats
    }

    private fun getDatoForOvelse(spinnerName: String): List<String> {
        val valgtOvelse = spinnerName
        //println(valgtOvelse)
        val firebaseDato = FirebaseDatabase.getInstance().getReference("Users").child(uID).child(
            "Stats"
        ).child(valgtOvelse)

        val listOfKeyDato = arrayListOf<String>()
        val eventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyDato.add(groupKey)
                }
                listOfKeyDato.toString()
                datoOvelse.add(listOfKeyDato.toString())
                println(listOfKeyDato)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        firebaseDato.addListenerForSingleValueEvent(eventListener)
        return listOfKeyDato + datoOvelse
    }

    private fun generateLineData(listOfKeyStats: ArrayList<String>): LineData {
        val lineD = LineData()
        val entries = ArrayList<Entry>()
        for (index in start..slutt) {
            entries.add(
                Entry(
                    java.lang.Float.parseFloat(index.toString()),
                    java.lang.Float.parseFloat(
                        listOfKeyStats[index],
                    ),
                )
            )
        }
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()


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
        lineChart.description.text = ""

        val xVals: ArrayList<String> = setXAxisValues(getStartDate(), getEndDate())
        val axis: XAxis = lineChart.xAxis
        axis.valueFormatter = IndexAxisValueFormatter(xVals)

        lineChart.description.textSize = 12f
        lineChart.xAxis.textSize = 12f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisLeft.textSize = 12f
        lineChart.setNoDataText("No data found")
        lineChart.animateX(1800, Easing.EaseInExpo)

        //val markerView = Marker(requireActivity().applicationContext, R.layout.fragment_chart)
        //lineChart.marker = markerView

        return lineD

    }
/*
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(listOfKeyDato: ArrayList<String>) {
        val dateformatArray = ArrayList<String>()
        val localDate = LocalDate.now()
        val last30Days = localDate.minusDays(30)
        val last90Days = localDate.minusDays(90)
        val last365Days = localDate.minusDays(365)

        try {
            val localDateFormat365 = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(last365Days)
            dateformatArray.add(localDateFormat365)
            println(localDateFormat365)
        } catch (e: Exception) {
            println("Failed to format date")
        }
        try {
            val localDateFormat90 = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(last90Days)
            dateformatArray.add(localDateFormat90)
        } catch (e: Exception) {
            println("Failed to format date")
        }
        try {
            val localDateFormat30 = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(last30Days)
            dateformatArray.add(localDateFormat30)
        } catch (e: Exception) {
            println("Failed to format date")
        }
        for (i in 0 until dateformatArray.size) {
            dateformatArray[i]
        }
        val test = "01-04-2020"
        for (d in 0 until listOfKeyDato.size) {
            println(test == listOfKeyDato[d])
        }
        println(dateformatArray)
        println(dateformatArray)

        //println(listOfKeyDato)

    }

 */


private fun setXAxisValues(from: String, to: String): ArrayList<String> {
        val xVals = ArrayList<String>()

        return xVals
    }

    private fun getEndDate(): String {
        var endDate = String()
            endDate = 10.toString()
        return endDate
    }

    private fun getStartDate(): String {
        var startDate = String()
            startDate = 1.toString()
        return startDate
    }


/*
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

 */
}



