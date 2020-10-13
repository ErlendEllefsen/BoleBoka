package com.example.boleboka

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.boleboka.databinding.FragmentMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main_page.*


class MainPage : Fragment() {

    companion object {
        fun getLaunchIntent (from: Context) = Intent (from, MainPage::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //setupUI()

        val binding = DataBindingUtil.inflate<FragmentMainPageBinding>(inflater,
            R.layout.fragment_main_page,container,false)

        binding.startBtn.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_startWorkout_to_active_workout)
        }
        return binding.root
    }

    private fun setupUI () {
        logout_button.setOnClickListener {
            logout()
        }
    }

    private fun logout () {
        startActivity(LoginActivity.getLaunchIntent(requireActivity()))
        FirebaseAuth.getInstance().signOut()
    }

}