package com.example.boleboka

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_workouts.*


class Workouts : Fragment(), Adapter.OnItemClickListener {

    private var model: Communicator?=null

    private val workoutList = generateWorkoutList(20)

    private val adapter = Adapter(workoutList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view.setHasFixedSize(true)
        //henter comm
        model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        btn_insert.setOnClickListener() {
             showDialog(view, model)
        }
    }

    private fun showDialog(view: View, modelProviders: Communicator?) {
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
                // Sender navnet på den nye workouten til exersise.kt
                model!!.setMsgCommunicator(workoutName)
                dialog.dismiss()
                view.findNavController().navigate(R.id.action_workouts_to_exercise)
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, desc: String) {
        /*
         * TODO: Jon, her må kode som legger til den nye workouten i databasen legges
         */
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()

        val database = FirebaseDatabase.getInstance()
        //val nameW = database.getReference("Users").child(uID).child("Workouts").child(name).child("Name")
        val descN = database.getReference("Users").child(uID).child("Workouts").child(name).child("Desc")

        //nameW.setValue(name)
        descN.setValue(desc)

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
        val workoutDialog = Dialog(fragment.requireContext())
        workoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        workoutDialog.setContentView(R.layout.edit_workout)
        val btnDelete =
            workoutDialog.btn_delete as com.google.android.material.floatingactionbutton.FloatingActionButton
        val btnAdd = workoutDialog.save_btn as Button
        val changeDesc = workoutDialog.changeDesc as EditText
        val changeName = workoutDialog.changeName as EditText
        workoutDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position)
            workoutDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val workoutName = changeName.text.toString()
            val workoutDesc = changeDesc.text.toString()
            if (workoutName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                workoutList[position].text1 = workoutName
                workoutList[position].text2 = workoutDesc
                adapter.notifyItemChanged(position)
                workoutDialog.dismiss()
            }
        }

    }

    private fun generateWorkoutList(size: Int): ArrayList<Workout_Item> {
        // workoutName
        // workoutDesc

        val list = ArrayList<Workout_Item>()

        for (i in 0 until size) {
            val item = Workout_Item("Item $i", "Line $i")
            list += item
        }
        return list


    }




}