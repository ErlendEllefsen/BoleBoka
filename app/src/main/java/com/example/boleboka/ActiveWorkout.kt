package com.example.boleboka

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_active_workout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/* Erlend: I denne klassen går man gjennom en workout.
 * Reps, sets og weight blir lagret i en array som når
 * du er ferdig med treningen blir sendt til databasen
 * Google Material Design hadde ett bilde av hvordan de ønsket
 * en side som dette skulle se ut, men ingen hjelpebibloteker
 * til hvordan det skulle fungere. Alle andre hjelpebibloteker som jeg fant
 * var som regel i java og fungerte ikke som jeg ønsket.
 * Så kodet denne helt fra bunnen av.
 */
class ActiveWorkout : Fragment() {

    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()
    private lateinit var workoutName: String
    private lateinit var exerciseList: ArrayList<Exercise_Item>
    private var i = 0
    private val resultsList = ArrayList<Result_Item>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_active_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)?.hideNavBar()
        /* Erlend: Henter workoutname i communicator-klassen, for å senere bruke
         * det for å finne riktig workout i databasen
         */
        val model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        workoutName = model.message.value!!.toString()
        generateExersices(view)
    }

    // Erlend: Denne kjøres etter listen med øvelser er blitt hentet
    private fun startWorkout(view: View, exerciseList: ArrayList<Exercise_Item>) {
        // Erlend: bruker currentuser for å lettere lese koden. Setter verdier
        var currentList = exerciseList[i]
        var name = currentList.name
        var reps = currentList.reps
        var sets = currentList.sets
        // Erlend: setter start progress for første øvelse.
        progressBar.progress += 100 / exerciseList.size
        // Erlend: sender verdiene til funksjon som setter verdiene i komponenter
        setValues(name, reps, sets)
        /* Erlend: Listeners til både back button og next button kommer her.
         * Det viktige her er om du er på siste eller første øvelse i treningslisten
         * må programmet stoppe deg for å forhindre krasj.
         * Også om en ønsker å gå tilbake til forrigje øvelse å redigere på det som har blitt
         * skrevet inn der bli husket, og nye verdier må overskrive de som allerede var der.
         */
        btnBack.setOnClickListener() {
            // Erlend: Om en prøver å gå tilbake når man er i første øvelse
            if (exerciseList[i] == exerciseList.first()) {
                errorMessage("This is the first exercise!")
            } else {
                // Erlend: Om brukeren kan gå tilbake en øvelse
                i -= 1
                progress(false, exerciseList)
                currentList = exerciseList[i]
                name = currentList.name
                reps = currentList.reps
                sets = currentList.sets
                setValues(name, reps, sets)
                // Erlend: Når man går tilbake vil man få frem de verdiene man skrev i forrigje øvelse.
                val listWeight = resultsList[i].weight
                val listReps = resultsList[i].reps
                val listSets = resultsList[i].sets
                setValuesFromList(listWeight, listReps, listSets)
            }
        }
        btnNext.setOnClickListener() {
            // Erlend: Om det er siste øvelse vil du ikke kunne trykke videre
            if (exerciseList[i] == exerciseList.last()) {
                errorMessage("This is the last exercise!")
            } else {
                // Erlend: Vekt kan ikke være tom
                if (weight.text.toString() == "") {
                    errorMessage("Weight cannot be empty")
                } else {
                    // Erlend: Lagrer verdiene
                    storeValues(i)
                    i += 1
                    progress(true, exerciseList)
                    currentList = exerciseList[i]
                    name = currentList.name
                    reps = currentList.reps
                    sets = currentList.sets
                    setValues(name, reps, sets)
                    if (resultsList.size > i) {
                        val listWeight = resultsList[i].weight
                        val listReps = resultsList[i].reps
                        val listSets = resultsList[i].sets
                        setValuesFromList(listWeight, listReps, listSets)
                    }
                    // Erlend: Om brukeren har gått gjennom alle øvelsene vil det være
                    // mulig å avslutte.
                    if (currentList == exerciseList.last())
                        btnFinish.visibility = View.VISIBLE
                }
            }
        }
        btnFinish.setOnClickListener() {
            // Erlend: Må fortsatt sjekke om vekt er tom på siste øvelse
            if (weight.text.toString() == "") {
                errorMessage("Weight cannot be empty")
            } else {
                // Erlend: Alle veriene lagres og du blir sendt tilbake til forsiden
                storeValues(i)
                view.findNavController().navigate(R.id.action_active_workout_to_startWorkout)
                saveToDB()
                (activity as MainActivity?)?.showNavBar()
            }
        }
    }

    private fun errorMessage(message: String) {
        val error =
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
        error.show()
    }

    private fun setValuesFromList(listWeight: Int, listReps: Int, listSets: Int) {
        numberPicker.value = listReps
        numberPickerSets.value = listSets
        weight.setText(listWeight.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveToDB() {
        val date = Calendar.getInstance().time
        val simpleDate = SimpleDateFormat("MM-dd-yyyy")
        val currentDate = simpleDate.format(date)


        for (i in 0 until resultsList.size) {
            val database = FirebaseDatabase.getInstance()
            val sets = database.getReference("Users").child(uID).child("Stats")
                .child(exerciseList[i].name).child(currentDate).child("Sets")
            val reps = database.getReference("Users").child(uID).child("Stats")
                .child(exerciseList[i].name).child(currentDate).child("Reps")
            val vekt = database.getReference("Users").child(uID).child("Stats")
                .child(exerciseList[i].name).child(currentDate).child("Vekt")

            sets.setValue(resultsList[i].sets)
            reps.setValue(resultsList[i].reps)
            vekt.setValue(resultsList[i].weight)
        }
        Toast.makeText(context, "Results saved", Toast.LENGTH_SHORT).show()
    }

    private fun progress(prog: Boolean, exerciseList: ArrayList<Exercise_Item>) {
        // Erlend: Deler progressbaren opp i biter ettersom hvor mange øvelser det er
        val divider = 100 / exerciseList.size
        // Erlend: divider plusses på eller trekkes fra ettersom
        if (prog) {
            progressBar.progress += divider
        } else {
            progressBar.progress -= divider
        }
    }

    private fun setValues(name: String, reps: Int, sets: Int) {
        /* Erlend: Gir verdier til komponenter på siden ut ifra
         * hvor i listen vi er.
         */
        textView.text = name
        numberPicker.maxValue = reps + 5
        numberPicker.value = reps
        numberPickerSets.maxValue = sets + 5
        numberPickerSets.value = sets
        weight.setText("")
        numberPicker.minValue = 0
        numberPickerSets.minValue = 0
    }


    private fun storeValues(pos: Int) {
        // Erlend: Henter alle verdiene og lagrer dem i en liste.
        val reps = numberPicker.value
        val wgt = Integer.parseInt(weight.text.toString().trim())
        val sets = numberPickerSets.value
        val item = Result_Item(reps, wgt, sets)
        /* Erlend: Her sørges det for at resultatene blir satt i riktig
         * posisjon i listen
         */
        // Er listen tom settes resultatet bare rett inn i listen
        if (resultsList.isEmpty())
            resultsList.add(pos, item)
        else {
            /* Er listens størrelse større en posisjonen vil
             * resultatet overskrive en bestemt posisjon i listen.
             * Om den ikke er det vil den sette inn resultatet sist i listen
             */
            if (resultsList.size > pos) {
                resultsList[pos] = item
            } else
                resultsList.add(pos, item)
        }
    }

    private fun generateExersices(view: View) {
        // Erlend: Henter listen med øvelser i firebase
        val list = ArrayList<Exercise_Item>()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Exercise")
                .child(workoutName)
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    children.forEach {
                        val name = it.child("Name").value.toString()
                        val reps = it.child("Reps").value.toString()
                        val sets = it.child("Sets").value.toString()
                        val task = Exercise_Item(name, reps.toInt(), sets.toInt())
                        list.add(task)
                    }
                    exerciseList = list
                    // Sender listen til startWorkout etter all data er blitt hentet
                    startWorkout(view, exerciseList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }

            })

    }
}