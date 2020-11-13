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
        var reps = currentList.reps
        var sets = currentList.sets
        progressBar.progress += 100 / exerciseList.size
        setValues(name, reps, sets)
        btnBack.setOnClickListener() {
            if (exerciseList[i] == exerciseList.first()) {
                errorMessage("This is the first exercise!")
            } else {
                i -= 1
                progress(false)
                currentList = exerciseList[i]
                name = currentList.name
                reps = currentList.reps
                sets = currentList.sets
                setValues(name, reps, sets)
                val listWeight = resultsList[i].weight
                val listReps = resultsList[i].reps
                val listSets = resultsList[i].sets
                setValuesFromList(listWeight, listReps, listSets)
            }
        }
        btnNext.setOnClickListener() {
            if (exerciseList[i] == exerciseList.last()) {
                errorMessage("This is the last exercise!")
            } else {
                if (weight.text.toString() == "") {
                    errorMessage("Weight cannot be empty")
                } else {
                    storeValues(i)
                    i += 1
                    progress(true)
                    currentList = exerciseList[i]
                    name = currentList.name
                    reps = currentList.reps
                    sets = currentList.sets
                    setValues(name, reps, sets)
                    if (resultsList.size > i) {
                        val listWeight = resultsList[i].weight
                        val listReps = resultsList[i].reps
                        val listSets = resultsList[i].sets
                        setValuesFromList(listWeight, listReps, listSets)
                    }
                    if (currentList == exerciseList.last())
                        btnFinish.visibility = View.VISIBLE
                }
            }
        }
        btnFinish.setOnClickListener() {
            if (weight.text.toString() == "") {
                errorMessage("Weight cannot be empty")
            } else {
                storeValues(i)
                view.findNavController().navigate(R.id.action_active_workout_to_startWorkout)
                saveToDB()
            }
        }

    }

    private fun errorMessage(message: String) {
        val error =
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
        error.show()
    }

    private fun setValuesFromList(listWeight: Int, listReps: Int, listSets: Int) {
        numberPicker.value = listReps
        numberPickerSets.value = listSets
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

    private fun setValues(name: String, reps: Int, sets: Int) {
        textView.text = name
        numberPicker.maxValue = reps
        numberPicker.minValue = 0
        numberPicker.value = 0
        numberPickerSets.maxValue = sets
        numberPickerSets.minValue = 0
        numberPickerSets.value = 0
        weight.setText("")
    }

    private fun storeValues(pos: Int) {
        val reps = numberPicker.value
        val wgt = Integer.parseInt(weight.text.toString().trim())
        val sets = numberPickerSets.value
        val item = Result_Item(reps, wgt, sets)
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
