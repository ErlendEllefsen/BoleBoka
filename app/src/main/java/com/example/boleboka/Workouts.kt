package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_workouts.*

class Workouts : Fragment() {


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
        val workoutList = generateDummyList(500)
        recycler_view.adapter = Adapter(workoutList)
        recycler_view.layoutManager = LinearLayoutManager(context)
        //performance optimization
        recycler_view.setHasFixedSize(true)
    }
    private fun generateDummyList(size: Int): List<Workout_Item> {

        val list = ArrayList<Workout_Item>()

        for (i in 0 until size){
            val item = Workout_Item("Item $i", "Line 2")
            list+=item
        }
        return list
    }
}