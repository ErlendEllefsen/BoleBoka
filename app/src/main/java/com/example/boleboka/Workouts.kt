package com.example.boleboka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.boleboka.databinding.FragmentWorkoutsBinding

class Workouts : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentWorkoutsBinding>(inflater,
            R.layout.fragment_workouts,container,false)
        binding.chestButton.setOnClickListener {
            binding.chestButton.setOnClickListener { view: View ->
                view.findNavController().navigate(R.id.action_workouts_to_make_workout)
            }
        }

        return binding.root
    }

    }