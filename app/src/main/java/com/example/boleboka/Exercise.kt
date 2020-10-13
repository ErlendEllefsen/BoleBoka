package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_exercises.*


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

        // Henter message i communicator
        val model= ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        val txt = exerciseHeader as TextView
        model.message.observe(viewLifecycleOwner,
            { o -> txt.text = o!!.toString() })
    }

    private fun deleteItem(view: View, position: Int) {

    }

    private fun insertItem(view: View, name: String, desc: String) {
        TODO()
    }

    private fun removeItem(view: View) {
        TODO()
    }

    private fun generateExerciseList(size: Int): ArrayList<Exercise_Item> {

        val list = ArrayList<Exercise_Item>()

        for (i in 0 until size) {
            val item = Exercise_Item("Exercise $i", 1)
            list += item
        }
        return list
    }


    override fun onExerciseClick(position: Int) {
        TODO("Not yet implemented")
    }
}