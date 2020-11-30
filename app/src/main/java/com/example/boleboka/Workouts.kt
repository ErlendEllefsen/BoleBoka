package com.example.boleboka

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlin.collections.ArrayList

/* Erlend: Hvordan recyclerview er koblet til adapteret i denne klassen er gjort med hjelp fra guide
 * fra Coding in Flow, referanse i dokumentet.
 */

class Workouts : Fragment(), AdapterWorkout.OnItemClickListener {

    private var model: Communicator? = null
    private val workoutList = generateWorkoutList()
    private val adapter = AdapterWorkout(workoutList, this)
    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_insert.setOnClickListener() {
            showDialog(view)
        }
    }

    override fun onStart() {
        super.onStart()
        // Erlend: setter adapteret for listen
        recycler_view.adapter = adapter
        // Erlend: setter layout for listen
        recycler_view.layoutManager = LinearLayoutManager(context)
        /* Erlend: performance optimization.
        *  Om vi vet at listen har en bestemt lengde og høyde i fragmentet
        *  kan denne metoden kalles for å spare på litt ytelse
        */
        recycler_view.setHasFixedSize(true)
    }


    private fun showDialog(view: View) {
        // Erlend: Bygger dialogvidnuet
        val dialog = Dialog(fragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_workout)
        val input = dialog.workoutName as EditText
        val inputDesc = dialog.workoutDesc as EditText
        val yesBtn = dialog.add_btn as Button
        yesBtn.setOnClickListener {
            val workoutName = input.text.toString()
            val workoutDesc = inputDesc.text.toString()

            // Erlend: Sjekker om EditText er tom
            if (workoutName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                insertItem(workoutName, workoutDesc)
                // Erlend: Sender navnet på den nye workouten til exersise.kt
                sendInfoToFragment(workoutName, workoutList.size + 1)
                dialog.dismiss()
                // Erlend: Sender brukeren til exersice siden til workouten når en ny workout er lagd.
                view.findNavController().navigate(R.id.action_workouts_to_exercise)
            }
        }
        dialog.show()
    }

    private fun sendInfoToFragment(workoutName: String, position: Int) {
        // Erlend: Sender posisjon og workoutname til communicator.kt
        model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        model!!.setMsgCommunicator(workoutName)
        model!!.positionCommunicator(position)
    }

    private fun insertItem(name: String, desc: String) {
         /*Jon: Funksjon som lagrer parameter verdiene på hver sin plass i firebase
         * Her blir det også lagt en verdi under Exercise for å kunne lagre øvelser under riktig økt
         */

        val database = FirebaseDatabase.getInstance()
        val nameW = database.getReference("Users").child(uID).child("Workouts").child(name).child("Name")
        val descW = database.getReference("Users").child(uID).child("Workouts").child(name).child("Desc")
        val exN = database.getReference("Users").child(uID).child("Exercise").child(name)

        nameW.setValue(name)
        descW.setValue(desc)
        exN.setValue(name)

        val atTop = !recycler_view.canScrollVertically(-1)
        val index = 0
        val newItem = Workout_Item(name, desc)
        workoutList.add(newItem)
        adapter.notifyItemInserted(index)
        // Erlend: Sørger for item som blir lagt til ikke blir lagt til utenfor view
        if (atTop) {
            recycler_view.scrollToPosition(0)
        }
    }

    private fun removeItem(position: Int, nameW: String) {
        // Jon: sletter Workout fra firebase og recycleviewet

        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("Users").child(uID).child("Workouts").child(nameW)
        val refE = db.getReference("Users").child(uID).child("Exercise").child(nameW)
        ref.removeValue()
        refE.removeValue()

        // Erlend: fjerner workout i frontend og sier ifra til adapter.
        workoutList.removeAt(position)
        adapter.notifyItemRemoved(position)
        Toast.makeText(context, "Workout $nameW deleted", Toast.LENGTH_SHORT).show()

    }
    private fun changeWorkout(workoutName: String, workoutDesc: String, position: Int){
        // Jon: Bytter verdiene som ligger under "Name" og "Desc" i firebase men får ikke endret key(ståre mer forklart i dokument)

        val database = FirebaseDatabase.getInstance()
        val pathName = workoutList[position].text1
        workoutList.removeAt(position)

        val nameW = database.getReference("Users").child(uID).child("Workouts").child(pathName)
            .child("Name")
        val descW = database.getReference("Users").child(uID).child("Workouts").child(pathName)
            .child("Desc")

        nameW.setValue(workoutName)
        descW.setValue(workoutDesc)

        val changedItem = Workout_Item(workoutName, workoutDesc)
        workoutList.add(position, changedItem)
    }


    override fun onItemClick(position: Int) {
        // Erlend: Kalles om bruker trykker på en workout og bygger dialogvindu
        val workoutDialog = Dialog(fragment.requireContext())
        workoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        workoutDialog.setContentView(R.layout.edit_workout)
        val btnDelete =
            workoutDialog.btn_delete as FloatingActionButton
        val btnEdit =
            workoutDialog.btn_edit as FloatingActionButton
        val btnAdd = workoutDialog.save_btn as Button
        val changeDesc = workoutDialog.changeDesc as EditText
        val changeName = workoutDialog.changeName as EditText
        val nameW = workoutList[position].text1
        workoutDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position, nameW)
            workoutDialog.dismiss()
        }
        btnEdit.setOnClickListener {
            sendInfoToFragment(workoutList[position].text1, position)
            workoutDialog.dismiss()
            view?.findNavController()?.navigate(R.id.action_workouts_to_exercise)

        }
        btnAdd.setOnClickListener {
            val workoutName = changeName.text.toString()
            val workoutDesc = changeDesc.text.toString()
            if (workoutName == "") {
                val noNameToast = Toast.makeText(context, "No name", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                adapter.notifyItemChanged(position)
                changeWorkout(workoutName, workoutDesc, position)
                adapter.notifyItemChanged(position)
                workoutDialog.dismiss()
            }
        }

    }

    private fun generateWorkoutList(): ArrayList<Workout_Item> {
         /* Jon:Funksjonen henter ut data fra firebase ved hjelp av en singlevaluelistener og legger de i en arraylist
         * arraylisten har en dataklasse som er koblett opp mot recyclerviewet
         */

        val list = ArrayList<Workout_Item>()
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()

        val firebase = FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Workouts")
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        adapter.notifyDataSetChanged()
                        val children = snapshot.children
                        children.forEach {

                            val obj = it.child("Name").value.toString()
                            val obj2 = it.child("Desc").value.toString()
                            val task = Workout_Item(obj, obj2)
                            list.add(task)
                        }
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Funket ikke", Toast.LENGTH_SHORT).show()
                }


            })

        return list
    }
    }


