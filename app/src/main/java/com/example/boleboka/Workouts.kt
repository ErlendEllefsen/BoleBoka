package com.example.boleboka

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlin.random.Random

class Workouts : Fragment(), Adapter.OnItemClickListener {

    private val workoutList = generateDummyList(20)
    private val adapter = Adapter(workoutList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workouts, container, false)
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
           /* btn_remove.setOnClickListener() {
                removeItem(view)
            }*/
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
                val noNameToast = Toast.makeText(context,"No name",Toast.LENGTH_SHORT)
                noNameToast.show()
            }
            else{
                insertItem(view, workoutName, workoutDesc)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    private fun insertItem(view: View, name: String, desc: String){
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
    private fun removeItem(view: View){
        val index = Random.nextInt(8)
        workoutList.removeAt(index)
        adapter.notifyItemRemoved(index)

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem = workoutList[position]
        clickedItem.text1 = "Clicked"
        adapter.notifyItemChanged(position)
        /*
         * TODO: Her skal vi implementere hva som skjer når man trykker på en bestemt workout.
         *       Man kan forwarde position herfra, som kanskje er det samme som ID til workouten i databasen?
         */
    }

    private fun generateDummyList(size: Int): ArrayList<Workout_Item> {

        val list = ArrayList<Workout_Item>()

        for (i in 0 until size){
            val item = Workout_Item("Item $i", "Line $i")
            list+=item
        }
        return list
    }
}