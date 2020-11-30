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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class Chart : Fragment() {

    val listOfKeyDate = arrayListOf<String>()
    val listOfReps = arrayListOf<String>()
    val listOfSets = arrayListOf<String>()

    private var end by Delegates.notNull<Int>()
    private var start by Delegates.notNull<Int>()

    private lateinit var firebaseAuth: FirebaseAuth

    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSpinnerData()
    }

    private fun getSpinnerData(): ArrayList<String> {
        /*
            Robin
            Denne funskjonen henter alle øvelsene fra databasen, og legger disse øvelsene i en array.
            Arrayen med øvelser blir deretter lagt til spinneren ved hjelp av et array adapter.
            Layouten til spinneren og dropdown layouten blir også satt i denne funksjonen.
            Referanser kan finnes i dokumentasjonen til oppgaven.
        */
        val firebaseOvelse =
            FirebaseDatabase.getInstance().getReference("Users").child(uID).child(
                "Stats"
            )
        val listOfKeyOvelse = arrayListOf<String>()
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Her blir øvelsene hentet ifra databasen og lagt i en array.
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyOvelse.add(groupKey)
                }
                //Spinneren og adaptere er kodet på samme måte som i MainPage, kun tilpasset.
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
                            val spinnerExercise = listOfKeyOvelse[position]
                            /*
                                Når brukeren velger en øvelse i spinneren blir grafen som er tegnet slettet,
                                grafen får vite at datasettet har blitt endret også blir grafen oppdatert
                                med den nye informasjonen.
                             */
                            lineChart.clear()
                            lineChart.notifyDataSetChanged()
                            lineChart.invalidate()
                            getStatsExercise(spinnerExercise)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            spinner2.prompt = "Pick an Exercise"
                        }
                    }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
        firebaseOvelse.addListenerForSingleValueEvent(eventListener)
        return listOfKeyOvelse
    }

    private fun getStatsExercise(spinnerExercise: String): ArrayList<String> {
        val listOfKeyStats = arrayListOf<String>()
        val firebaseStats = FirebaseDatabase.getInstance().getReference("Users")
            .child(uID).child("Stats").child(spinnerExercise)
        /*
            Denne funksjonen henter informasjonen til den øvelsen som ble valgt av brukeren i
            funksjonen getSpinnerData. Informasjonen blir lagt i lister som senere brukes til å vise
            informasjon til brukeren.
        */
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
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
        firebaseStats.addListenerForSingleValueEvent(eventListener)
        getDateExercise(spinnerExercise)
        return listOfKeyStats
    }

    private fun setFilterSpinner(listOfKeyStats: ArrayList<String>): ArrayList<String> {
        /*
            Denne funksjonen setter et filter på informasjonen grafen skal ha tilgang til. Tanken her
            var at brukeren for eksempel skulle velge å se kun de siste 30 dagene med data.
            Men det å lage en funksjon som finner den nærmeste datoen i databasen 30 dager tilbake
            fra dagens dato var for tidkrevende.
        */
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
                    /*
                        For å styre hvor mye informasjon grafen skal ha tilgang til benyttes en if
                        setning. Dataen blir filtrert basert på posisjonen til spinneren.
                        Om listen av data ikke er større enn 4 vil ikke grafen ha nok data til å
                        bli tegnet. Listen entries er listen grafen blir tegnet av.
                    */
                    if (position == 0){
                        start = 35
                        end = listOfKeyStats.size
                        val entries = ArrayList<Entry>()
                        /*
                            Om listen med data fra databasen er større en 4 vil for løkken
                            legge denne dataen i en arraylist som blir parset til float.
                            Grafen leser bare float verdier. Om det ikke er nok verdier i listen vil
                            den returnere noData stringen.
                        */
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
                            }
                        }
                        else {
                            tvStats.text = noData
                        }
                        lineChart.notifyDataSetChanged()
                        lineChart.invalidate()
                        drawLineChart(entries)
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
                                }
                            }
                            else {
                                tvStats.text = noData
                            }
                            lineChart.notifyDataSetChanged()
                            lineChart.invalidate()
                            drawLineChart(entries)
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
                                    }
                                }
                                else {
                                    tvStats.text = noData
                                }
                                lineChart.notifyDataSetChanged()
                                lineChart.invalidate()
                                drawLineChart(entries)
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
                                        }
                                    }
                                    else {
                                        tvStats.text = noData
                                    }
                                    lineChart.notifyDataSetChanged()
                                    lineChart.invalidate()
                                    drawLineChart(entries)
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    spinner3.prompt = "Pick a filter"
                }
            }
        return listOfFilters
    }

    @SuppressLint("SetTextI18n")
    private fun getDateExercise(spinnerExercise: String): List<String> {
        /*
             Denne funksjonen henter alle datoene der den øvelsen som ble valgt i spinneren ble
             gjennomført. Disse datoene blir lagret i en array. Listen blir slettet hver gang
             brukeren henter en ny øvelse.
        */
        val firebaseDato = FirebaseDatabase.getInstance().getReference("Users").child(uID).child(
            "Stats"
        ).child(spinnerExercise)

        val eventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Her hentes alle datoene fra databasen, disse blir lagt i en array.
                for (ds in dataSnapshot.children) {
                    val groupKey = ds.key as String
                    listOfKeyDate.add(groupKey)
                }
                listOfKeyDate.toString()
                //println(listOfKeyDate)

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }

        firebaseDato.addListenerForSingleValueEvent(eventListener)
        listOfKeyDate.clear()
        tvStats.text = "Pick a value to see more data"
        return listOfKeyDate
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun drawLineChart(entries: ArrayList<Entry>): LineData {
        /*
            Denne funksjonen tegner grafen, den reformaterer labels på x-aksjen til dato og den
            gir brukeren en mulighet til å trykke på grafen og se mer detaljert hva som
            ble gjort den dagen.
        */

        /*
            For løkken settet dato som tilhørerer veriden som blir tegnet på grafen og legger
            denne datoen i en løkke.
        */
        val xLabel = ArrayList<String>()
        for (i in 0 until entries.size)
            xLabel.add(listOfKeyDate[i])

        val lineD = LineData()
        val dataSetl = LineDataSet(entries, "Kg")

        dataSetl.setDrawValues(false) //True -> Viser verien til et punkt på grafen.
        dataSetl.setDrawFilled(false) // True -> Fyller punktet.
        dataSetl.lineWidth = 3f
        dataSetl.valueTextSize = 15f
        dataSetl.fillAlpha = R.color.colorFail

        lineChart.xAxis.labelRotationAngle = 0f //Roterer label på x-aksjen fra vannrett til loddrett.
        lineChart.data = LineData(dataSetl) //Tegner dataset1 på grafen.
        lineChart.axisRight.isEnabled = false //True -> Tegner en y-akse på høyre side av grafen.
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Date"
        lineChart.setViewPortOffsets(80f, 0f, 85f, 100f) //Plasserer grafen.

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabel) //Setter custom labels.
        xAxis.setLabelCount(5, true) //Angir hvor mange labels soms kal vises.
        xAxis.isCenterAxisLabelsEnabled
        xAxis.setAvoidFirstLastClipping(false) //True -> Flytter labels inn på skjermbildet om disse er utenfor.


        lineChart.description.textSize = 12f
        lineChart.xAxis.textSize = 12f
        lineChart.axisLeft.textSize = 12f
        lineChart.setNoDataText("No data found")
        lineChart.animateX(1800, Easing.EaseInExpo) //Animerer tegningen av linjen.


        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            /*
                Når brukeren trykker på et punkt på grafen får brukeren mer informasjon
                i et tekst felt over grafen.
            */
            @SuppressLint("SetTextI18n")
            override fun onNothingSelected() {
                tvStats.text = "Pick a value to see more data"
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



