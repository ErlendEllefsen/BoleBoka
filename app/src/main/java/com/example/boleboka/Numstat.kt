package com.example.boleboka

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.fragment_numstat.*


class Numstat : Fragment() {

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

        btn3.setOnClickListener {
            calcStats()
        }


    }

    private fun calcStats(){
        if (testOvelse.text.toString() == "" || datoFra.text.toString() == "" || datoTil.text.toString() == "") {
            errorMessage("Fill inn the empty fields!")
        } else {
            Log.e("Getstats", "Hallo")
            val currentuser = FirebaseAuth.getInstance().currentUser?.uid
            val uID = currentuser.toString()
            val database = FirebaseDatabase.getInstance().reference
            val ovelse = testOvelse.text.toString()
            val fraDato = datoFra.text.toString()
            val tilDato = datoTil.text.toString()

            val readData = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sb1 = StringBuilder()
                    val sb2 = StringBuilder()
                    val sb3 = StringBuilder()
                    val sb4 = StringBuilder()
                    val sb5 = StringBuilder()
                    val sb6 = StringBuilder()

                    for (d in snapshot.children) {

                        val stat1 = d.child(uID).child("Stats").child(ovelse).child(fraDato)
                            .child("Vekt").value
                        val rep1 = d.child(uID).child("Stats").child(ovelse).child(fraDato)
                            .child("Reps").value
                        val set1 = d.child(uID).child("Stats").child(ovelse).child(fraDato)
                            .child("Sets").value
                        val stat2 = d.child(uID).child("Stats").child(ovelse).child(tilDato)
                            .child("Vekt").value
                        val rep2 = d.child(uID).child("Stats").child(ovelse).child(tilDato)
                            .child("Reps").value
                        val set2 = d.child(uID).child("Stats").child(ovelse).child(tilDato)
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

                    val fraDatoVekt = sb1.toString()
                    val fraDatoRep = sb2.toString()
                    val repMax1 =
                        (fraDatoVekt.toDouble() / (1.0278 - 0.0278 * fraDatoRep.toDouble())).toInt()
                    val tilDatoVekt = sb4.toString()
                    val tilDatoRep = sb5.toString()
                    val repMax2 =
                        (tilDatoVekt.toDouble() / (1.0278 - 0.0278 * tilDatoRep.toDouble())).toInt()

                    val prosentOkning =
                        ((repMax2.toDouble() - repMax1.toDouble()) / repMax1.toDouble()) * 100

                    styrkeOkning.text = prosentOkning.toString()
                    maxRep.text = repMax1.toString()
                    maxRepNa.text = repMax2.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Numstat", "errorrrorrorrorrorro")
                }
            }
            database.addValueEventListener(readData)
            database.addListenerForSingleValueEvent(readData)

        }
    }

    private fun errorMessage(message: String) {
        val error =
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
        error.show()
    }

}