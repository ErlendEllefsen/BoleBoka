package com.example.boleboka

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_exercises.*
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlinx.android.synthetic.main.fragment_workouts.btn_insert

class Workouts : Fragment(), Adapter.OnItemClickListener {

    private val workoutList = generateWorkoutList(20)

    private val adapter = Adapter(workoutList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view?.setBackgroundColor(Color.parseColor("#fffff"))
        return inflater.inflate( R.layout.fragment_workouts,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view.setHasFixedSize(true)
        btn_insert.setOnClickListener() {
             showDialog(view)
         }
    }

    private fun showDialog(view: View) {
        val dialog = Dialog(fragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_workout)
        val input = dialog.workoutName as EditText
        val inputDesc = dialog.workoutDesc as EditText
        val yesBtn = dialog.add_btn as Button
        yesBtn.setOnClickListener {
            val workoutName = input.text.toString()
            val workoutDesc = inputDesc.text.toString()

            // Sjekker om EditText er tom
            if (workoutName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                insertItem(workoutName, workoutDesc)
                dialog.dismiss()
              //  view.findNavController().navigate(R.id.action_workouts_to_exercise)
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, desc: String) {
        /*
         * TODO: Jon, her må kode som legger til den nye workouten i databasen legges
         */
        val atTop = !recycler_view.canScrollVertically(-1)
        val index = 0
        val newItem = Workout_Item(name, desc)
        workoutList.add(index, newItem)
        adapter.notifyItemInserted(index)
        // Sørger for item som blir lagt til ikke blir lagt til utenfor view
        if (atTop) {
            recycler_view.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int) {
        workoutList.removeAt(position)
        adapter.notifyItemRemoved(position)
        val workoutName = workoutList[position].text1
        Toast.makeText(context, "Workout $workoutName deleted", Toast.LENGTH_SHORT).show()
       // TODO("Slette i databasen")
    }


    override fun onItemClick(position: Int) {
        val exerciseDialog = Dialog(fragment.requireContext())
        exerciseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exerciseDialog.setContentView(R.layout.edit_workout)
        val btnDelete = exerciseDialog.btn_delete as com.google.android.material.floatingactionbutton.FloatingActionButton
        val btnAdd = exerciseDialog.save_btn_ex as Button
        exerciseDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position)
            exerciseDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val workoutName = "Workoutname"
            val workoutDesc = "Description"
            insertItem(workoutName, workoutDesc)
            exerciseDialog.dismiss()
        }

    }


    private fun generateWorkoutList(size: Int): ArrayList<Workout_Item> {

        val list = ArrayList<Workout_Item>()

        for (i in 0 until size) {
            val item = Workout_Item("Item $i", "Line $i")
            list += item
        }
        return list
    }




}