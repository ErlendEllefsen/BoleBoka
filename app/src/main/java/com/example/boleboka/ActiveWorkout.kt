package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_active_workout.*

class ActiveWorkout : Fragment() {

    private val testList = generateExersises(5)
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
            if (i <= 0) {
                i = 0
                val startToast = Toast.makeText(context, "Start", Toast.LENGTH_SHORT)
                startToast.show()
            } else {
                i -= 1
                progress(false)
                currentList = testList[i]
                name = currentList.heading
                min = currentList.minReps
                max = currentList.maxReps
                setValues(name, min, max)
            }
        }

        btnNext.setOnClickListener() {
            if (i >= 4) {
                val endToast = Toast.makeText(context, "End", Toast.LENGTH_SHORT)
                endToast.show()

            } else {
                val numReps = numberPicker.value
                saveToArray(i, numReps)
                i += 1
                progress(true)
                currentList = testList[i]
                name = currentList.heading
                min = currentList.minReps
                max = currentList.maxReps
                setValues(name, min, max)
                if (i >= 4)
                    btnFinish.visibility = View.VISIBLE

            }
        }
        btnFinish.setOnClickListener() {
            saveToDB()
        }

    }

    private fun saveToDB() {
        TODO("Not yet implemented")
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
        numberPicker.minValue = min
    }

    private fun saveToArray(position: Int, numReps: Int) {
        /*
         * TODO: Lagre reps fra posisijonen i databasen
         *  ide: lage en array med lik lengde som exersiseArray og replace values ettersom
         *       så sende data samlet til databasen når workout er ferdig
         */
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