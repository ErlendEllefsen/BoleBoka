package com.example.boleboka

import android.app.Dialog
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlin.collections.ArrayList


class Workouts : Fragment(), Adapter.OnItemClickListener {

    private var model: Communicator? = null
    private val workoutList = generateWorkoutList()
    private val adapter = Adapter(workoutList, this)
    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_insert.setOnClickListener() {
            showDialog(view, model)
        }
    }

    override fun onStart() {
        super.onStart()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view.setHasFixedSize(true)
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
                sendInfoToFragment(workoutName, workoutList.size + 1)
                dialog.dismiss()
                view.findNavController().navigate(R.id.action_workouts_to_exercise)
            }
        }
        dialog.show()
    }

    private fun sendInfoToFragment(workoutName: String, position: Int) {
        model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        model!!.setMsgCommunicator(workoutName)
        model!!.positionCommunicator(position)
    }

    private fun insertItem(name: String, desc: String) {

        val database = FirebaseDatabase.getInstance()
        val nameW = database.getReference("Users").child(uID).child("Workouts").child(name).child("Name")
        val descW = database.getReference("Users").child(uID).child("Workouts").child(name).child("Desc")
        val exN = database.getReference("Users").child(uID).child("Exercise").child(name)

        nameW.setValue(name)
        descW.setValue(desc)
        exN.setValue(name)

        val atTop = !recycler_view.canScrollVertically(-1)
        val index = 0
        val newItem = Workout_Item(name, desc)
        workoutList.add(newItem)
        adapter.notifyItemInserted(index)
        // Sørger for item som blir lagt til ikke blir lagt til utenfor view
        if (atTop) {
            recycler_view.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int, nameW: String) {
        // sletter Workout fra firebase og recycleviewet
        val output = nameW

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("Users").child(uID).child("Workouts").child(nameW)
        val refE = db.getReference("Users").child(uID).child("Exercise").child(nameW)
        ref.removeValue()
        refE.removeValue()

        workoutList.removeAt(position)
        adapter.notifyItemRemoved(position)
        Toast.makeText(context, "Workout $output deleted", Toast.LENGTH_SHORT).show()

    }
    private fun changeWorkout(workoutName: String, workoutDesc: String, position: Int){
        val database = FirebaseDatabase.getInstance()

        val pathName = workoutList[position].text1

        val nameW = database.getReference("Users").child(uID).child("Workouts").child(pathName).child("Name")
        val descW = database.getReference("Users").child(uID).child("Workouts").child(pathName).child("Desc")

        workoutList[position].text1 = workoutName
        workoutList[position].text2 = workoutDesc

        nameW.setValue(workoutName)
        descW.setValue(workoutDesc)

    }


    override fun onItemClick(position: Int) {
        val workoutDialog = Dialog(fragment.requireContext())
        workoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        workoutDialog.setContentView(R.layout.edit_workout)
        val btnDelete =
            workoutDialog.btn_delete as FloatingActionButton
        val btnEdit =
            workoutDialog.btn_edit as FloatingActionButton
        val btnAdd = workoutDialog.save_btn as Button
        val changeDesc = workoutDialog.changeDesc as EditText
        val changeName = workoutDialog.changeName as EditText
        val nameW = workoutList[position].text1
        workoutDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position, nameW)
            workoutDialog.dismiss()
        }
        btnEdit.setOnClickListener {
            sendInfoToFragment(workoutList[position].text1, position)
            workoutDialog.dismiss()
            view?.findNavController()?.navigate(R.id.action_workouts_to_exercise)

        }
        btnAdd.setOnClickListener {
            val workoutName = changeName.text.toString()
            val workoutDesc = changeDesc.text.toString()
            if (workoutName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                adapter.notifyItemChanged(position)
                changeWorkout(workoutName, workoutDesc, position)
                workoutDialog.dismiss()
            }
        }

    }

    private fun generateWorkoutList(): ArrayList<Workout_Item> {

        val list = ArrayList<Workout_Item>()
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()

        val firebase = FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Workouts")
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        adapter.notifyDataSetChanged()
                        val children = snapshot.children
                        children.forEach {

                            var obj = it.child("Name").value.toString()
                            var obj2 = it.child("Desc").value.toString()
                            var task = Workout_Item(obj, obj2)
                            list.add(task)
                        }
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    //  Toast.makeText(context, "Funket ikke", Toast.LENGTH_SHORT).show()
                }


            })

        return list
    }
    }


