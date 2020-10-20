package com.example.boleboka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_personal_info.*



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
    }



/*
    fun getGAInfo() {
        val manager = AccountManager.get(context)
        val accounts = manager.getAccountsByType("com.google")
        val username: MutableList<String> = LinkedList<String>()

        for (account in accounts) {
            username.add(account.name)
        }
    }
*/
}