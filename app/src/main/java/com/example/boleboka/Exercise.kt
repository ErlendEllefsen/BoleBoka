package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boleboka.databinding.FragmentExercisesBinding
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_exercises.*
import kotlinx.android.synthetic.main.fragment_exercises.view.*


class Exercise : Fragment(), AdapterExercise.OnItemClickListener {

    private val exerciseList = generateExerciseList(5)
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
            val item = Exercise_Item("Exercise $i", i + 10)
            list += item
        }
        return list
    }


    override fun onExerciseClick(position: Int) {
        TODO("Not yet implemented")
    }
}