package com.example.boleboka

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.boleboka.databinding.FragmentMainPageBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_workout.*
import kotlinx.android.synthetic.main.edit_workout.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import java.text.FieldPosition


class MainPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun createSpinner(): ArrayList<Spinner_Item>{
        val list = ArrayList<Spinner_Item>()
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()

        val firebase =
            FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Workouts")
        firebase
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()) {

                        val children = snapshot.children
                        children.forEach {

                            var obj = it.key.toString()
                            var task = Spinner_Item(obj)
                            list.add(task)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Toast.makeText(context, "Changed", Toast.LENGTH_SHORT).show()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Toast.makeText(context, "Moved", Toast.LENGTH_SHORT).show()
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Funket ikke", Toast.LENGTH_SHORT).show()
                }


            })

        val languages = resources.getStringArray((R.array.Languages))


             ArrayAdapter<String>(requireActivity().applicationContext, android.R.layout.simple_spinner_item, languages).also { adapter ->
                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                 spinner.adapter = adapter


            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    Toast.makeText(context, languages[position], Toast.LENGTH_SHORT).show()
                }
                override  fun onNothingSelected(parent: AdapterView<*>){
                    Toast.makeText(context, "Nothing selected", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(context, "Funket ikke$list", Toast.LENGTH_SHORT).show()
        }
        return list


    }


}