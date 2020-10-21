package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_active_workout.*

class ActiveWorkout : Fragment() {

    private val exerciseList = generateExersises(5)
    private var i = 0
    private val resultsList = ArrayList<Result_Item>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var currentList = exerciseList[i]
        var name = currentList.name
        var max = currentList.reps
        progressBar.progress += 100 / exerciseList.size
        setValues(name, max)
        btnBack.setOnClickListener() {
            if (exerciseList[i] == exerciseList.first()) {
                val startToast =
                    Toast.makeText(context, "This is the first exercise!", Toast.LENGTH_SHORT)
                startToast.show()
            } else {
                i -= 1
                progress(false)
                currentList = exerciseList[i]
                name = currentList.name
                max = currentList.reps
                setValues(name, max)
                val listWeight = resultsList[i].weight
                val listReps = resultsList[i].reps
                setValuesFromList(listWeight, listReps)
            }
        }
        btnNext.setOnClickListener() {
            if (exerciseList[i] == exerciseList.last()) {
                val endToast =
                    Toast.makeText(context, "This is the last exercise!", Toast.LENGTH_SHORT)
                endToast.show()
            } else {
                if (weight.text.toString() == "") {
                    val error =
                        Toast.makeText(context, "Weight can not be empty", Toast.LENGTH_SHORT)
                    error.show()
                } else {
                    storeValues(i)
                    i += 1
                    progress(true)
                    currentList = exerciseList[i]
                    name = currentList.name
                    max = currentList.reps
                    setValues(name, max)
                    if (resultsList.size > i) {
                        val listWeight = resultsList[i].weight
                        val listReps = resultsList[i].reps
                        setValuesFromList(listWeight, listReps)
                    }
                    if (currentList == exerciseList.last())
                        btnFinish.visibility = View.VISIBLE
                }
            }
        }
        btnFinish.setOnClickListener() {
            storeValues(i)
            view.findNavController().navigate(R.id.action_active_workout_to_startWorkout)
            saveToDB()
        }

    }

    private fun setValuesFromList(listWeight: Int, listReps: Int) {
        numberPicker.value = listReps
        weight.setText(listWeight.toString())
    }

    private fun saveToDB() {
        // TODO: Lagre resultlist til databasen
        val list = Toast.makeText(context, resultsList.toString(), Toast.LENGTH_SHORT)
        list.show()
    }

    private fun progress(prog: Boolean) {
        val divider = 100 / exerciseList.size
        if (prog) {
            progressBar.progress += divider
        } else {
            progressBar.progress -= divider
        }
        //  val list = Toast.makeText(context, progressBar.progress.toString(), Toast.LENGTH_SHORT).show()

    }

    private fun setValues(name: String, max: Int) {
        textView.text = name
        numberPicker.maxValue = max
        numberPicker.minValue = 0
        numberPicker.value = 0
        weight.setText("")
    }

    private fun storeValues(pos: Int) {
        val reps = numberPicker.value
        val wgt = Integer.parseInt(weight.text.toString().trim())
        val item = Result_Item(reps, wgt)
        if (resultsList.isEmpty())
            resultsList.add(pos, item)
        else {
            if (resultsList.size > pos) {
                resultsList[pos] = item
            } else
                resultsList.add(pos, item)
        }
    }

    private fun generateExersises(size: Int): ArrayList<Exercise_Item> {

        val list = ArrayList<Exercise_Item>()

        for (i in 0 until size) {
            val item = Exercise_Item("Exersise $i", i + 5, i)
            list += item
        }
        return list
    }
}