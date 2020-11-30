package com.example.boleboka

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_numstat.*


class Numstat : Fragment() {
    private lateinit var spinnerName: String
    private lateinit var dateTo: String
    private lateinit var dateFrom: String
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
        return inflater.inflate(R.layout.fragment_numstat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSpinnerEx()
        btn3.setOnClickListener{
            calcStats()
        }
    }

    private fun calcStats(){
        /*Denne funksjonen vil hente ut en valgt øvelse i en spinner og to valgte
        * datoer fra dato-feltene. Deretter vil den kalkulere om bruker har økt eller
        * redusert i styrke og regne ut hvor mye de kan maksimalt løfte
        * bare en gang.
        * */
        var test = true
            val currentuser = FirebaseAuth.getInstance().currentUser?.uid
            val uID = currentuser.toString()
            val database = FirebaseDatabase.getInstance().reference

            val readData = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sb1 = StringBuilder()
                    val sb2 = StringBuilder()
                    val sb3 = StringBuilder()
                    val sb4 = StringBuilder()
                    val sb5 = StringBuilder()
                    val sb6 = StringBuilder()
                    if (test) {

                        for (d in snapshot.children) {

                            val stat1 = d.child(uID).child("Stats").child(spinnerName).child(dateTo)
                                .child("Vekt").value
                            val rep1 = d.child(uID).child("Stats").child(spinnerName).child(dateTo)
                                .child("Reps").value
                            val set1 = d.child(uID).child("Stats").child(spinnerName).child(dateTo)
                                .child("Sets").value
                            val stat2 =
                                d.child(uID).child("Stats").child(spinnerName).child(dateFrom)
                                    .child("Vekt").value
                            val rep2 =
                                d.child(uID).child("Stats").child(spinnerName).child(dateFrom)
                                    .child("Reps").value
                            val set2 =
                                d.child(uID).child("Stats").child(spinnerName).child(dateFrom)
                                    .child("Sets").value
                            sb4.append("$stat2")
                            sb5.append("$rep2")
                            sb6.append("$set2")
                            sb1.append("$stat1")
                            sb2.append("$rep1")
                            sb3.append("$set1")
                        }
                        textView4.text = sb1
                        repFra.text = sb2
                        setFra.text = sb3
                        textView3.text = sb4
                        repTil.text = sb5
                        setTil.text = sb6


                        val fromDateWeight = sb1.toString()
                        val fromDateRep = sb2.toString()
                        val repMax1 =
                            (fromDateWeight.toDouble() / (1.0278 - 0.0278 * fromDateRep.toDouble())).toInt()
                        val toDateVekt = sb4.toString()
                        val toDateRep = sb5.toString()
                        val repMax2 =
                            (toDateVekt.toDouble() / (1.0278 - 0.0278 * toDateRep.toDouble())).toInt()

                        val percentIncrease =
                            ((repMax2.toDouble() - repMax1.toDouble()) / repMax1.toDouble()) * 100

                        styrkeOkning.text = percentIncrease.toString()
                        maxRep.text = repMax1.toString()
                        maxRepNa.text = repMax2.toString()
                    }
                    test = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Numbstats", "$error")
                }
            }
            database.addValueEventListener(readData)
            database.addListenerForSingleValueEvent(readData)

        }
    private fun getSpinnerEx(): ArrayList<String> {
        /*
        Jon
        Funksjonen henter data fra databasen og legger det inn i en Arraylist
        som derretter blir brukt til å legge informasjon inn i en spinner ved hjelp av en arrayadapter.
        SPinner layout og dropdownlayout blir også satt her.
         */
        val list = ArrayList<String>()

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(uID).child("Stats")
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                        val children = snapshot.children

                        children.forEach {


                            val obj = it.key.toString()
                            list.add(obj)
                        }

                        val ad = ArrayAdapter(
                            fragment.requireContext(),
                            android.R.layout.simple_spinner_item, list
                        )
                        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerNum.adapter = ad

                        val adapter = ArrayAdapter(
                            fragment.requireContext(),
                            android.R.layout.simple_spinner_item, list
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerNum.adapter = adapter

                        spinnerNum.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long,
                                ) {
                                    spinnerName = list[position]
                                    getSpinnerDateTo(spinnerName)
                                    getSpinnerDateFrom(spinnerName)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                }
                            }

                }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            })

        return list

    }
    private fun getSpinnerDateTo(spinnerName: String): ArrayList<String> {
        /*
        Funksjonen henter data fra databasen og legger det inn i en Arraylist
        som derretter blir brukt til å legge informasjon inn i en spinner ved hjelp av en arrayadapter.
        SPinner layout og dropdownlayout blir også satt her.
         */
        val list = ArrayList<String>()

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(uID).child("Stats").child(spinnerName)
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val children = snapshot.children

                    children.forEach {

                        val obj = it.key.toString()
                        list.add(obj)
                    }

                    val ad = ArrayAdapter(
                        fragment.requireContext(),
                        android.R.layout.simple_spinner_item, list
                    )
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDate1.adapter = ad

                    val adapter = ArrayAdapter(
                        fragment.requireContext(),
                        android.R.layout.simple_spinner_item, list
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDate1.adapter = adapter

                    spinnerDate1.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long,
                            ) {
                                dateTo = list[position]
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {
                            }
                        }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            })
        return list
    }
    private fun getSpinnerDateFrom(spinnerName: String): ArrayList<String> {
        /*
        Funksjonen henter data fra databasen og legger det inn i en Arraylist
        som derretter blir brukt til å legge informasjon inn i en spinner ved hjelp av en arrayadapter.
        SPinner layout og dropdownlayout blir også satt her.
         */
        val list = ArrayList<String>()

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(uID).child("Stats").child(spinnerName)
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val children = snapshot.children

                    children.forEach {

                        val obj = it.key.toString()
                        list.add(obj)
                    }

                    val ad = ArrayAdapter(
                        fragment.requireContext(),
                        android.R.layout.simple_spinner_item, list
                    )
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDate2.adapter = ad

                    val adapter = ArrayAdapter(
                        fragment.requireContext(),
                        android.R.layout.simple_spinner_item, list
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDate2.adapter = adapter

                    spinnerDate2.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long,
                            ) {
                                dateFrom = list[position]
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {
                            }
                        }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            })
        return list
    }
}