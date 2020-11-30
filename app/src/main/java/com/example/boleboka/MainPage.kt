package com.example.boleboka

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private lateinit var spinnerName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMainPageBinding>(
            inflater,
            R.layout.fragment_main_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* Jon: Setter spinner verdien til "" for at variabelen skal bli initialized
         * spinnerName får ny verdi fra funksjonen getSpinnerData om det er data i databasen
         */

        spinnerName = ""
        getSpinnerData()
        startBtn.setOnClickListener { view: View ->
            if (spinnerName != "") {
                view.findNavController()
                    .navigate(R.id.action_startWorkout_to_active_workout)
                Log.e("Chart", "Noe galt!")

            } else {
                Toast.makeText(context, "You have no workouts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.showNavBar()
    }

    private fun getSpinnerData(): ArrayList<String> {
         /* Jon: Funksjonen henter data fra databasen og legger det inn i en Arraylist
         * som derretter blir brukt til å legge informasjon inn i en spinner ved hjelp av en arrayadapter.
         * Spinner layout og dropdownlayout blir også satt her.
         */
        val list = ArrayList<String>()

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
            val uID = currentuser.toString()


            val firebase =
                FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Workouts")
            firebase
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val children = snapshot.children
                        // Jon: går igjennom alle verdiene i db
                        children.forEach {
                            // Jon: henter value fra plassen "Name"
                            val obj = it.child("Name").value.toString()
                            // Jon: legger value inn i list
                            list.add(obj)
                        }
                        val ad = ArrayAdapter(
                                fragment.requireContext(),
                                android.R.layout.simple_spinner_item, list
                                )
                        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner?.adapter = ad

                        //Jon: Setter arrayadapter på arraylisten list og layout på spinner
                        val adapter = ArrayAdapter(
                            fragment.requireContext(),
                            android.R.layout.simple_spinner_item, list
                        )
                        // Jon: setter dropdown layouten til spinner
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner?.adapter = adapter

                        spinner?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long,
                                ) {
                                    // Jon: setter ny verdi på spinnerName
                                      spinnerName = list[position]
                                    sendInfoToFragment(spinnerName)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    spinner.prompt = "Getting data"
                                }
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MainPage", "$error")
                    }


                })

        return list

    }

    private fun sendInfoToFragment(workoutName: String) {
        // Erlend: Sender workoutname til communicator når du starter en workout.
        model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        model!!.setMsgCommunicator(workoutName)
    }

}