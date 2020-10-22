package com.example.boleboka

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.boleboka.MainActivity.Companion.getLaunchIntent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_personal_info.*

class MainActivity : AppCompatActivity() {

    companion object {
        fun getLaunchIntent (from: Context) = Intent (from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        // setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
        // TODO: 21.10.2020 Hente informasjon fra LoginActivity, mulig informasjonen ikke blir sendt?
        val intent = intent
        val personFamilyName = intent.getStringExtra("AccName")
       // Toast.makeText(this, personFamilyName.toString(), Toast.LENGTH_LONG).show()
        /*
         * Returnerer "Family Name: Null" (i et textview i dette fragmentet)
         * Får ikke hentet informasjonen fra LoginActivity.kt

            val result = findViewById<TextView>(R.id.textView)
            result.text = "Family Name: $personFamilyName"

        */

    }

    fun getFamilyname (): String? {
        // TODO: 21.10.2020 Lag en funksjon som sender informasjon om brukeren til personal_info.kt
        /*
        val intent = intent
        val personFamilyName = intent.getStringExtra("Family Name")
        return personFamilyName

         */

        val personFamilyName = intent.getStringExtra("Family Name")
        return personFamilyName.toString()
    }

     fun setupSignoutBtn () {
        logout_button.setOnClickListener() {
            logout()
        }
    }

    private fun logout () {
        FirebaseAuth.getInstance().signOut()
        startActivity(LoginActivity.getLaunchIntent(this))
    }
}

