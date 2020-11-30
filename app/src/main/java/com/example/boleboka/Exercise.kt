package com.example.boleboka

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_exercise.*
import kotlinx.android.synthetic.main.edit_exercise.*
import kotlinx.android.synthetic.main.fragment_exercises.*

/* Erlend: Hvordan recyclerview er koblet til adapteret i denne klassen er gjort med hjelp fra guide
 * fra Coding in Flow, referanse i dokumentet.
 */
class Exercise : Fragment(), AdapterExercise.OnItemClickListener {
    // Har hentet denne fra stackowerflow for å finne UserID til han som er logget på
    private val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private val uID = currentuser.toString()
    private lateinit var workoutName: String
    private lateinit var pathRem: String
    private lateinit var exerciseList: ArrayList<Exercise_Item>
    private lateinit var adapterEx: AdapterExercise
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Erlend: Henter workoutname fra communicator.kt
        val model = ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        workoutName = model.message.value!!.toString()
        // Erlend: Viser dialogvindu når +-knappen er trykket på
        btn_exersise_insert.setOnClickListener() {
            showDialog(workoutName)
        }
        // Erlend: Henter liste fra firebase
        exerciseList = generateExerciseList()
        adapterEx = AdapterExercise(exerciseList, this)
        // Erlend: setter adapteret for listen
        recycler_view_exercise.adapter = adapterEx
        // Erlend: setter layout for listen
        recycler_view_exercise.layoutManager = LinearLayoutManager(context)
        /* Erlend: performance optimization.
         * Om vi vet at listen har en bestemt lengde og høyde i fragmentet
         * kan denne metoden kalles for å spare på litt ytelse
         */
        recycler_view_exercise.setHasFixedSize(true)

        // Erlend: Henter message i communicator og setter som overskrift
        val txt = exerciseHeader as TextView
        model.message.observe(viewLifecycleOwner,
            { o -> txt.text = o!!.toString() })
    }

    private fun showDialog(workoutName: String) {
        // Erlend: Bygger dialogvindu
        val dialog = Dialog(fragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_exercise)
        val inputName = dialog.exerciseName as EditText
        val inputReps = dialog.exerciseReps as EditText
        val inputSets = dialog.exerciseSets as EditText
        val addBtn = dialog.add_exersice_btn as Button
        addBtn.setOnClickListener() {
            // Erlend: Henter ut verdiene som er blitt skrevet inn
            val exerciseName = inputName.text.toString()
            val exerciseReps = inputReps.text.toString()
            val exerciseSets = inputSets.text.toString()

            /* Erlend: Er det ikke blitt skrevet inn noe navn vil det komme en feilmelding
             * Om alt er greit vil verdiene bli sendt videre og dialogvinduet lukket
             */
            if (exerciseName == "" || exerciseReps == "" || exerciseSets == "") {
                val noNameToast = Toast.makeText(context, "Write in all fields", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                val exerciseRepsInt = exerciseReps.toInt()
                val exerciseSetsInt = exerciseSets.toInt()
                insertItem(exerciseName, exerciseRepsInt, exerciseSetsInt, workoutName)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun insertItem(name: String, reps: Int, sets: Int, workoutName: String) {
        /* Jon: Parameter verdiene blir lagret i firebase, workoutname er bare med for å få riktig path i firebase
         * recyclerviewet blir også oppdatert
         */
        val database = FirebaseDatabase.getInstance()
        val nameDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Name")
        val repsDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Reps")
        val setsDB =
            database.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Sets")
        nameDB.setValue(name)
        repsDB.setValue(reps)
        setsDB.setValue(sets)

        /* Erlend: Her settes informajonen inn i frontend.
         * atTop sjekker om det går ann å scrolle oppover i vinduet.
         * Om dette er mulig vil vinudet bli scrollet til toppen for at brukeren
         * skal kunne se den nye øvelsen som ble lagt til
         */
        val atTop = !recycler_view_exercise.canScrollVertically(-1)
        val index = exerciseList.size
        val newItem = Exercise_Item(name, reps, sets)
        exerciseList.add(index, newItem)
        // Erlend: Her får adapteret besjked om at en ny øvelse er biltt lagt til
        adapterEx.notifyItemInserted(index)
        if (atTop) {
            recycler_view_exercise.scrollToPosition(0)
        }
    }

    // Erlend: Om bruker trykker på deleteknappen i dialogvindu.
    private fun removeItem(position: Int, workoutName: String) {
        // Jon: Enkel funksjon for å slette Exercise fra firebase
        val exerciseName = exerciseList[position].name
        val db = FirebaseDatabase.getInstance()

        val ref = db.getReference("Users").child(uID).child("Exercise").child(workoutName).child(exerciseName)
        ref.removeValue()

        // Erlend: fjerner øvelsen i frontend og sier ifra til adapter.
        exerciseList.removeAt(position)
        adapterEx.notifyItemRemoved(position)
        Toast.makeText(context, "Exercise $exerciseName deleted", Toast.LENGTH_SHORT).show()

    }
    private fun changeItem(name: String, reps: Int, sets: Int, workoutName: String, position: Int){

         // Jon: Setter nye verdiene til "Name", "Reps" og "Sets" i firebase

        val databaseS = FirebaseDatabase.getInstance()
        // Jon : Henter exercise navnet til det itemet du har trykket på
       val pathName = exerciseList[position].name
        val exerciseName = exerciseList[position].name
        Toast.makeText(context, "Exercise $pathName changed", Toast.LENGTH_SHORT).show()

        val ref = databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName).child(exerciseName)
        ref.removeValue()

        val nameDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Name")
        val repsDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Reps")
        val setsDB =
            databaseS.getReference("Users").child(uID).child("Exercise").child(workoutName)
                .child(name).child("Sets")


        nameDB.setValue(name)
        repsDB.setValue(reps)
        setsDB.setValue(sets)

    }

    override fun onExerciseClick(position: Int) {
        // Erlend: Kalles om bruker trykker på en øvelse og bygger dialogvindu
        val exerciseDialog = Dialog(fragment.requireContext())
        exerciseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exerciseDialog.setContentView(R.layout.edit_exercise)
        val btnDelete =
            exerciseDialog.btn_delete_ex as com.google.android.material.floatingactionbutton.FloatingActionButton
        val btnAdd = exerciseDialog.save_btn_ex as Button
        val changeName = exerciseDialog.changeExName as EditText
        val changeReps = exerciseDialog.changeReps as EditText
        val changeSets = exerciseDialog.changeSets as EditText
        exerciseDialog.show()
        btnDelete.setOnClickListener {
            removeItem(position, workoutName)
            exerciseDialog.dismiss()
        }
        btnAdd.setOnClickListener {
            val exName = changeName.text.toString()
            val exReps = changeReps.text.toString()
            val exSets = changeSets.text.toString()

            if (exName == "" || exReps == "" || exSets == "") {
                val noNameToast = Toast.makeText(context, "Write in all fields", Toast.LENGTH_SHORT)
                noNameToast.show()
            } else {
                val exRepsInt = exReps.toInt()
                val exSetsInt = exSets.toInt()
                adapterEx.notifyItemChanged(position)
                changeItem(exName, exRepsInt, exSetsInt, workoutName, position)
                exerciseDialog.dismiss()
            }
        }
    }

    private fun generateExerciseList(): ArrayList<Exercise_Item> {

        /* Jon: Funksjonen henter data ut fra firebase on lagrer det i en arraylist
         * arraylisten har en dataklassen exercise_item som er koblett opp mot recyclerviewet.
         */
        val list = ArrayList<Exercise_Item>()
        val firebase =
            FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Exercise")
                .child(workoutName)
        firebase
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapterEx.notifyDataSetChanged()
                    val children = snapshot.children
                    children.forEach {
                        // Jon: henter verdiene fra riktig posisjon og legger de inn i en val
                        val name = it.child("Name").value.toString()
                        val reps = it.child("Reps").value.toString()
                        val sets = it.child("Sets").value.toString()
                        // Jon: legger verdiene inn i Exercise_item
                        val task = Exercise_Item(name, reps.toInt(), sets.toInt())
                        list.add(task)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }

            })
        return list
    }


}