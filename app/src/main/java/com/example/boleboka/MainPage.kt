package com.example.boleboka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.boleboka.databinding.FragmentMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_page.*



class MainPage : Fragment() {

    private var model: Communicator? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMainPageBinding>(
            inflater,
            R.layout.fragment_main_page, container, false
        )

        binding.startBtn.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_startWorkout_to_active_workout)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSpinnerData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun getSpinnerData(): ArrayList<String> {
        val languages = resources.getStringArray(R.array.Languages)
        val list = ArrayList<String>()
        //list.add("Select a workout")

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
            val uID = currentuser.toString()


            val firebase =
                FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Workouts")
            firebase
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val children = snapshot.children

                        children.forEach {

                            val obj = it.child("Name").value.toString()
                            list.add(obj)
                        }

                        val ad = ArrayAdapter(
                            fragment.requireContext(),
                            android.R.layout.simple_spinner_item, list
                        )
                        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = ad


                        val adapter = ArrayAdapter(
                            fragment.requireContext(),
                            android.R.layout.simple_spinner_item, list
                        )

                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        spinner.adapter = adapter

                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long,
                                ) {
                                    val spinnerName = list[position]
                                    sendInfoToFragment(spinnerName)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    spinner.prompt = "Select a Workout"
                                }
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }


                })
            /*
            if (spinner == null) {
                val dd = ArrayList<String>()
                val i = languages.toString()
                dd.add(i)
                val ad = ArrayAdapter(
                    fragment.requireContext(),
                    android.R.layout.simple_spinner_item, list
                )
                spinner.adapter = ad

            }

             */

        return list

    }

    private fun sendInfoToFragment(workoutName: String) {
        model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        model!!.setMsgCommunicator(workoutName)
    }

}