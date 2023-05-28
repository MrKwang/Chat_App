package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ContentInfoCompat.Flags
import com.example.myapplication.R
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.databinding.FragmentContactBinding
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.utilities.Preference

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentSettingsBinding
private lateinit var preferenceManager: Preference
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater,container, false)
        preferenceManager = Preference(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogOut.setOnClickListener {
            preferenceManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK))
            startActivity(intent)
        }
    }
}