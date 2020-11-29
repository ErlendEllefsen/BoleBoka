package com.example.boleboka

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_personal_info.*
import java.text.SimpleDateFormat
import java.util.*


class Personal_info : Fragment()  {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
        }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logout_button.setOnClickListener { (activity as MainActivity).setupSignoutBtn() }

        val name: String = firebaseAuth.currentUser!!.displayName.toString()
        textView2.text = "Logged in as\n$name"

        val email: String = firebaseAuth.currentUser!!.email.toString()
        textView3.text = "Email\n$email"

        val photoUrl: String = firebaseAuth.currentUser!!.photoUrl.toString()
        Glide.with(context).load(photoUrl)
            .thumbnail(0.1f)
            .crossFade()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView4)

    }

    // Dette skulle være kode for å lagre høyde og vekt på brukeren
    // Skulle bli brukt til å regne ut BMI, men kom ikke så langt
    /* private fun savePersonalInfo() {
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

     }*/
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    internal fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}

private fun setText(it: FragmentActivity): String {
    return it.toString()
}
