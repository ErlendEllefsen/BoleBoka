package com.example.boleboka

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
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

    }

    fun hideNavBar() {
        bottomNav.visibility = View.INVISIBLE
    }

    fun showNavBar() {
        bottomNav.visibility = View.VISIBLE
    }

    fun setupSignoutBtn() {
        logout_button.setOnClickListener() {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.main, fragment)
    transaction.addToBackStack(null)
    transaction.commit()
}



