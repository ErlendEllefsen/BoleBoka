package com.example.boleboka

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_exercise.*
import kotlinx.android.synthetic.main.edit_exercise.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_exercises.*
import kotlinx.android.synthetic.main.fragment_workouts.*


class Exercise : Fragment(), AdapterExercise.OnItemClickListener {

    private val exerciseList = generateExerciseList(100)
    private val adapterEx = AdapterExercise(exerciseList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        val addBtn = dialog.add_exersice_btn as Button
        addBtn.setOnClickListener() {
            val exerciseName = inputName.text.toString()
            val exerciseRepsString = inputReps.text.toString()
            val exerciseReps = Integer.parseInt(exerciseRepsString)
            if (exerciseName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                insertItem(exerciseName, exerciseReps)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, reps: Int) {
        /*
         * TODO: Jon, kode som legger til den nye øvelsen i FB
         */
        val atTop = !recycler_view_exercise.canScrollVertically(-1)
        val index = 0
        val newItem = Exercise_Item(name, reps)
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
        val changeReps = exerciseDialog.changeExReps as EditText
        exerciseDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position)
            exerciseDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val exName = changeName.text.toString()
            val exRepsString = changeReps.text.toString()
            val exReps = Integer.parseInt(exRepsString)
            if (exName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                exerciseList[position].name = exName
                exerciseList[position].reps = exReps
                adapterEx.notifyItemChanged(position)
                exerciseDialog.dismiss()
            }
        }
    }

    private fun generateExerciseList(size: Int): ArrayList<Exercise_Item> {

        val list = ArrayList<Exercise_Item>()

        for (i in 0 until size) {
            val item = Exercise_Item("Exercise $i", 1)
            list += item
        }
        return list
    }


}