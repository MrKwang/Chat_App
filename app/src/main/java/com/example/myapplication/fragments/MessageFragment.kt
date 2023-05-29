package com.example.myapplication.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.apdapter.ChatAdapter
import com.example.myapplication.databinding.FragmentMessageBinding
import com.example.myapplication.model.UserList
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore


class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private lateinit var preferenceManager: Preference
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: ChatAdapter
    private var recentList: MutableList<UserList> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater,container, false)
        preferenceManager = Preference(requireContext().applicationContext)
        database = FirebaseFirestore.getInstance()
        binding.rvRecentList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnClickListener {
            Toast.makeText(activity, "Search something here", Toast.LENGTH_SHORT).show()
            //val supportFragment = activity?.supportFragmentManager.beginTransaction().add()
        }


    }





}