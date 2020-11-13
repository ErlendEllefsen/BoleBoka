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

    private val exerciseList = generateExerciseList("Bryst")
    private val adapterEx = AdapterExercise(exerciseList, this)
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    val uID = currentuser.toString()

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
        val workoutName = model.message.value!!.toString()
        val currentPosition = model.position.value!!
        val positionToast =
            Toast.makeText(context, "Current position is: $currentPosition", Toast.LENGTH_SHORT)
                .show()
        btn_exersise_insert.setOnClickListener() {
            showDialog(view, workoutName)
        }
    }

    private fun showDialog(view: View, workoutName: String) {
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

            val database = FirebaseDatabase.getInstance()
            val nameDB =
                database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                    .child(name)
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
        val index = 0
        val newItem = Exercise_Item(name, reps, sets)
        exerciseList.add(index, newItem)
        adapterEx.notifyItemInserted(index)
        if (atTop) {
            recycler_view_exercise.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int) {
        val exerciseName = exerciseList[position].name

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("Users").child(uID).child("Exercise").child(exerciseName)
        ref.removeValue()
        exerciseList.removeAt(position)
        adapterEx.notifyItemRemoved(position)
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
            val exReps = changeReps.text.toString()
            val exSets = changeSets.text.toString()
            val exRepsInt = exReps.toInt()
            val exSetsInt = exSets.toInt()
            if (exName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                exerciseList[position].name = exName
                exerciseList[position].reps = exRepsInt
                exerciseList[position].sets = exSetsInt
                adapterEx.notifyItemChanged(position)
                exerciseDialog.dismiss()
            }
        }
    }

    private fun generateExerciseList(workoutName: String): ArrayList<Exercise_Item>{

        val list = ArrayList<Exercise_Item>()
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()


        val firebase = FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Exercise").child(workoutName)
        firebase
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    if (snapshot.exists())  {

                        val children = snapshot.children
                        children.forEach {

                            var name = it.key.toString()
                            var reps = it.child("Reps").value.toString()
                            var sets = it.child("Sets").value.toString()
                            var task = Exercise_Item(name, reps.toInt(), sets.toInt())
                            list.add(task)
                        }

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        return list
    }


}