package com.example.boleboka

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.view.ViewGroup
import java.time.LocalDateTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_personal_info.*
import java.text.SimpleDateFormat
import java.util.*


class Personal_info : Fragment() {

   // lateinit var activityLogin: LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // activityLogin.textView2.text = "personName"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logout_button.setOnClickListener { (activity as MainActivity).setupSignoutBtn() }
        btnSave.setOnClickListener{
            savePersonalInfo()
        }
    }
    private fun savePersonalInfo() {
        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        val uID = currentuser.toString()
        val stringW = weight.text.toString()
        val stringH = height.text.toString()

        val intW = stringW.toInt()
        val intH = stringH.toInt()

        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy-MM-dd")

        val database = FirebaseDatabase.getInstance()
        val heightDB =
            database.getReference("Users").child(uID).child("Userdata").child(dateInString)
                .child("Height")
        val weightDB =
            database.getReference("Users").child(uID).child("Userdata").child(dateInString)
                .child("Weight")

        heightDB.setValue(intH)
        weightDB.setValue(intW)

    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    internal fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}