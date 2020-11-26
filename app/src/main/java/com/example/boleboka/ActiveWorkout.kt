package com.example.boleboka

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_active_workout.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ActiveWorkout : Fragment() {

    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()
    private lateinit var workoutName: String
    private lateinit var exerciseList: ArrayList<Exercise_Item>
    private var i = 0
    private var yes = false
    private val resultsList = ArrayList<Result_Item>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        workoutName = model.message.value!!.toString()
        generateExersises(view)
        Toast.makeText(context, workoutName, Toast.LENGTH_LONG).show()
    }

    private fun startWorkout(view: View, exerciseList: ArrayList<Exercise_Item>) {
        Toast.makeText(context, "YO", Toast.LENGTH_LONG).show()
        var currentList = exerciseList[i]
        var name = currentList.name
        var reps = currentList.reps
        var sets = currentList.sets
        progressBar.progress += 100 / exerciseList.size
        setValues(name, reps, sets)
        btnBack.setOnClickListener() {
            if (exerciseList[i] == exerciseList.first()) {
                errorMessage("This is the first exercise!")
            } else {
                i -= 1
                progress(false, exerciseList)
                currentList = exerciseList[i]
                name = currentList.name
                reps = currentList.reps
                sets = currentList.sets
                setValues(name, reps, sets)
                val listWeight = resultsList[i].weight
                val listReps = resultsList[i].reps
                val listSets = resultsList[i].sets
                setValuesFromList(listWeight, listReps, listSets)
            }
        }
        btnNext.setOnClickListener() {
            if (exerciseList[i] == exerciseList.last()) {
                errorMessage("This is the last exercise!")
            } else {
                if (weight.text.toString() == "") {
                    errorMessage("Weight cannot be empty")
                } else {
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
                    if (currentList == exerciseList.last())
                        btnFinish.visibility = View.VISIBLE
                }
            }
        }
        btnFinish.setOnClickListener() {
            if (weight.text.toString() == "") {
                errorMessage("Weight cannot be empty")
            } else {
                storeValues(i)
                view.findNavController().navigate(R.id.action_active_workout_to_startWorkout)
                saveToDB()
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
                .child(exerciseList[i].name).child(currentDate).child("Seps")
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
        val divider = 100 / exerciseList.size
        if (prog) {
            progressBar.progress += divider
        } else {
            progressBar.progress -= divider
        }
        //  val list = Toast.makeText(context, progressBar.progress.toString(), Toast.LENGTH_SHORT).show()

    }

    private fun setValues(name: String, reps: Int, sets: Int) {
        textView.text = name
        numberPicker.maxValue = reps
        numberPicker.minValue = 0
        numberPicker.value = 0
        numberPickerSets.maxValue = sets
        numberPickerSets.minValue = 0
        numberPickerSets.value = 0
        weight.setText("")
    }

    private fun storeValues(pos: Int) {
        val reps = numberPicker.value
        val wgt = Integer.parseInt(weight.text.toString().trim())
        val sets = numberPickerSets.value
        val item = Result_Item(reps, wgt, sets)
        if (resultsList.isEmpty())
            resultsList.add(pos, item)
        else {
            if (resultsList.size > pos) {
                resultsList[pos] = item
            } else
                resultsList.add(pos, item)
        }
    }

    private fun generateExersises(view: View) {
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
                        startWorkout(view, exerciseList)
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }

            })

    }
}