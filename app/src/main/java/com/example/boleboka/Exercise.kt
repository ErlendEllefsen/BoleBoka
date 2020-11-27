package com.example.boleboka

import android.app.Dialog
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.config.GservicesValue.value
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_exercise.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_exercise.*
import kotlinx.android.synthetic.main.exercise_items.*
import kotlinx.android.synthetic.main.fragment_exercises.*


class Exercise : Fragment(), AdapterExercise.OnItemClickListener {
    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()
    private lateinit var workoutName: String
    private lateinit var exerciseList: ArrayList<Exercise_Item>
    private lateinit var adapterEx: AdapterExercise
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
        val model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        workoutName = model.message.value!!.toString()

        btn_exersise_insert.setOnClickListener() {
            showDialog(workoutName)
        }
        exerciseList = generateExerciseList()
        adapterEx = AdapterExercise(exerciseList, this)
        recycler_view_exercise.adapter = adapterEx
        recycler_view_exercise.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view_exercise.setHasFixedSize(true)

        // Henter message i communicator, burde også hente ett eller annet ID

        val txt = exerciseHeader as TextView
        model.message.observe(viewLifecycleOwner,
            { o -> txt.text = o!!.toString() })
        //POSITION @Dashern

       // val positionToast =
          //  Toast.makeText(context, "Current position is: $currentPosition", Toast.LENGTH_SHORT).show()

    }

    private fun showDialog(workoutName: String) {
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
            val exerciseReps = inputReps.text.toString()
            val exerciseSets = inputSets.text.toString()
            val exerciseRepsInt = exerciseReps.toInt()
            val exerciseSetsInt = exerciseSets.toInt()

            if (exerciseName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                insertItem(exerciseName, exerciseRepsInt, exerciseSetsInt, workoutName)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, reps: Int, sets: Int, workoutName: String) {
        /*
        Parameter verdiene blir lagret i firebase, workotname er bare med for å få riktig path i firebas
        recyclerviewet blir også oppdatert
         */


        val database = FirebaseDatabase.getInstance()
        val nameDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Name")
        val repsDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Reps")
        val setsDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Sets")
        nameDB.setValue(name)
        repsDB.setValue(reps)
        setsDB.setValue(sets)

        val atTop = !recycler_view_exercise.canScrollVertically(-1)
        val index = exerciseList.size
        val newItem = Exercise_Item(name, reps, sets)
        exerciseList.add(index, newItem)
        adapterEx.notifyItemInserted(index)
        if (atTop) {
            recycler_view_exercise.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int, workoutName: String) {

        //Enkel funksjon for å slette Exercise fra firebase


        val exerciseName = exerciseList[position].name
        val db = FirebaseDatabase.getInstance()

        val ref = db.getReference("Users").child(uID).child("Exercise").child(workoutName).child(exerciseName)
        ref.removeValue()

        exerciseList.removeAt(position)
        adapterEx.notifyItemRemoved(position)
        Toast.makeText(context, "Exercise $exerciseName deleted", Toast.LENGTH_SHORT).show()

    }
    private fun changeItem(name: String, reps: Int, sets: Int, workoutName: String, position: Int){
        /*
        Dette funksjonen legger nye verdier inn i firebase etter at brukeren har skrevet inn nye verdier i onclick menyen.
         */
        val databaseS = FirebaseDatabase.getInstance()
       val pathName = exerciseList[position].name
        Toast.makeText(context, "Exercise $exerciseName changed", Toast.LENGTH_SHORT).show()

        val nameDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(pathName).child("Name")
        val repsDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(pathName).child("Reps")
        val setsDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(pathName).child("Sets")
        exerciseList[position].name = name
        exerciseList[position].reps = reps
        exerciseList[position].sets = sets

        nameDB.setValue(name)
        repsDB.setValue(reps)
        setsDB.setValue(sets)


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
            removeItem(position, workoutName)
            exerciseDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val exName = changeName.text.toString()
            val exReps = changeReps.text.toString()
            val exSets = changeSets.text.toString()
            val exRepsInt = exReps.toInt()
            val exSetsInt = exSets.toInt()
            if (exName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                adapterEx.notifyItemChanged(position)
                changeItem(exName, exRepsInt, exSetsInt, workoutName, position)
                exerciseDialog.dismiss()
            }
        }
    }

    private fun generateExerciseList(): ArrayList<Exercise_Item> {
        /*
        Funksjonen henter data ut fra firebase on lagrer det i en arraylist
         arraylisten har en dataklassen exercise_item somm er koblett opp mot recyclerviewet.
         */
        val list = ArrayList<Exercise_Item>()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Exercise")
                .child(workoutName)
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapterEx.notifyDataSetChanged()
                    val children = snapshot.children
                    children.forEach {
                        val name = it.child("Name").value.toString()
                        val reps = it.child("Reps").value.toString()
                        val sets = it.child("Sets").value.toString()
                        val task = Exercise_Item(name, reps.toInt(), sets.toInt())
                        list.add(task)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }

            })
        return list
    }


}