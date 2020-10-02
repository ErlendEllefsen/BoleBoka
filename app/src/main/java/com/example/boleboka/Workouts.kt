package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlin.random.Random

class Workouts : Fragment(), Adapter.OnItemClickListener {

    private val workoutList = generateDummyList(500)
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
                insertItem(view)
            }
            btn_remove.setOnClickListener() {
                removeItem(view)
            }
    }

    private fun insertItem(view: View){
        val index = Random.nextInt(1,8)
        val newItem = Workout_Item("NEW ITEM AT $index", "DAMN DANIEL 2")
        workoutList.add(index, newItem)
        adapter.notifyItemInserted(index)
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
            val item = Workout_Item("Item $i", "Line 2")
            list+=item
        }
        return list
    }
}