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

    private val testList = generateExersises(6)
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
        var currentList = testList[i]
        var name = currentList.heading
        var min = currentList.minReps
        var max = currentList.maxReps
        progressBar.progress += 100 / testList.size
        setValues(name, min, max)

        btnBack.setOnClickListener() {
            if (testList[i] == testList.first()) {
                val startToast =
                    Toast.makeText(context, "This is the first exercise!", Toast.LENGTH_SHORT)
                startToast.show()
            } else {
                i -= 1
                progress(false)
                currentList = testList[i]
                name = currentList.heading
                min = currentList.minReps
                max = currentList.maxReps
                setValues(name, min, max)
                val listWeight = resultsList[i].weight
                val listReps = resultsList[i].reps
                setValuesFromList(listWeight, listReps)
            }
        }

        btnNext.setOnClickListener() {
            if (testList[i] == testList.last()) {
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
                    currentList = testList[i]
                    name = currentList.heading
                    min = currentList.minReps
                    max = currentList.maxReps
                    setValues(name, min, max)
                    if (resultsList.size > i) {
                        val listWeight = resultsList[i].weight
                        val listReps = resultsList[i].reps
                        setValuesFromList(listWeight, listReps)
                    }
                    if (currentList == testList.last())
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
        val divider = 100 / testList.size
        if (prog) {
            progressBar.progress += divider
        } else {
            progressBar.progress -= divider
        }
    }

    private fun setValues(name: String, min: Int, max: Int) {
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

    private fun generateExersises(size: Int): ArrayList<Test_Item> {

        val list = ArrayList<Test_Item>()

        for (i in 0 until size) {
            val item = Test_Item("Exersise $i", i, i + 2)
            list += item
        }
        return list
    }
}