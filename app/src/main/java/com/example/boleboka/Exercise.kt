package com.example.boleboka

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_exercise.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_exercise.*
import kotlinx.android.synthetic.main.fragment_exercises.*
import kotlinx.android.synthetic.main.fragment_personal_info.*


class Exercise : Fragment(), AdapterExercise.OnItemClickListener {

    private val exerciseList = generateExerciseList(100)
    private val adapterEx = AdapterExercise(exerciseList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_exercises, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_exercise.adapter = adapterEx
        recycler_view_exercise.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view_exercise.setHasFixedSize(true)

        // Henter message i communicator, burde også hente ett eller annet ID
        val model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        val txt = exerciseHeader as TextView
        model.message.observe(viewLifecycleOwner,
            { o -> txt.text = o!!.toString() })
        //POSITION @Dashern
        val currentPosition = model.position.value!!
        val positionToast =
            Toast.makeText(context, "Current position is: $currentPosition", Toast.LENGTH_SHORT)
                .show()
        btn_exersise_insert.setOnClickListener() {
            showDialog(view)
        }
    }

    private fun showDialog(view: View) {
        val dialog = Dialog(fragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_exercise)
        val inputName = dialog.exerciseName as EditText
        // TODO: gjøre om til numberpicker
        val inputReps = dialog.exerciseReps as EditText
        val inputSets = dialog.exerciseSets as EditText
        val addBtn = dialog.add_exersice_btn as Button
        addBtn.setOnClickListener() {
            val exerciseName = inputName.text.toString()
            val exerciseRepsString = inputReps.text.toString()
            val exerciseSetsString = inputSets.text.toString()
            val exerciseReps = Integer.parseInt(exerciseRepsString)
            val exerciseSets = Integer.parseInt(exerciseSetsString)

            if (exerciseName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                insertItem(exerciseName, exerciseReps, exerciseSets)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, reps: Int, sets: Int) {
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()


        /*val workoutsName = workoutName.text.toString()

        val database = FirebaseDatabase.getInstance()
        val nameDB = database.getReference("Users").child(uID).child("Workouts").child(name)
        val repsDB = database.getReference("Users").child(uID).child("Workouts").child("Stats").child(
            "dateInString").child("Reps")
        val setsDB = database.getReference("Users").child(uID).child("Workouts").child("Stats").child(
            "dateInString").child("Sets")
        nameDB.setValue(name)
        repsDB.setValue(reps)
        setsDB.setValue(sets)

         */

        val atTop = !recycler_view_exercise.canScrollVertically(-1)
        val index = 0
        val newItem = Exercise_Item(name, reps, sets)
        exerciseList.add(index, newItem)
        adapterEx.notifyItemInserted(index)
        if (atTop) {
            recycler_view_exercise.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int) {
        exerciseList.removeAt(position)
        adapterEx.notifyItemRemoved(position)
        val exerciseName = exerciseList[position].name
        Toast.makeText(context, "Exercise $exerciseName deleted", Toast.LENGTH_SHORT).show()
        // TODO("Slette øvelse i databasen")
    }

    override fun onExerciseClick(position: Int) {
        val exerciseDialog = Dialog(fragment.requireContext())
        exerciseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exerciseDialog.setContentView(R.layout.edit_exercise)
        val btnDelete =
            exerciseDialog.btn_delete_ex as com.google.android.material.floatingactionbutton.FloatingActionButton
        val btnAdd = exerciseDialog.save_btn_ex as Button
        val changeName = exerciseDialog.changeExName as EditText
        val changeReps = exerciseDialog.changeReps as EditText
        val changeSets = exerciseDialog.changeSets as EditText
        exerciseDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position)
            exerciseDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val exName = changeName.text.toString()
            val exRepsString = changeReps.text.toString()
            val exSetsString = changeSets.text.toString()
            val exReps = Integer.parseInt(exRepsString)
            val exSets = Integer.parseInt(exSetsString)
            if (exName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                exerciseList[position].name = exName
                exerciseList[position].reps = exReps
                exerciseList[position].sets = exSets
                adapterEx.notifyItemChanged(position)
                exerciseDialog.dismiss()
            }
        }
    }

    private fun generateExerciseList(size: Int): ArrayList<Exercise_Item> {

        val list = ArrayList<Exercise_Item>()

        for (i in 0 until size) {
            val item = Exercise_Item("Exercise $i", i + 5, i)
            list += item
        }
        return list
    }


}