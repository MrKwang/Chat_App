package com.example.myapplication.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.apdapter.RecentlyChatAdapter
import com.example.myapplication.databinding.FragmentMessageBinding
import com.example.myapplication.model.RecentlyChatUser
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private lateinit var preferenceManager: Preference
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: RecentlyChatAdapter
    private var recentList: MutableList<RecentlyChatUser> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater,container, false)
        preferenceManager = Preference(requireContext().applicationContext)
        database = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnClickListener {
            Toast.makeText(activity, "Search something here", Toast.LENGTH_SHORT).show()

        }
        fetchData()
        adapter = RecentlyChatAdapter(recentList)
        binding.rvRecentList.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        binding.rvRecentList.adapter = adapter
        binding.rvRecentList.setHasFixedSize(true)


    }

    private fun fetchData() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_USER_1_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_USER_2_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener: EventListener<QuerySnapshot> = EventListener { value, error ->
        if (error != null) {
            return@EventListener
        }
        if(value != null){
            val newList = mutableListOf<RecentlyChatUser>()
            for (document in value.documentChanges ){
                if(document.type == DocumentChange.Type.ADDED){
                    if(document.document.getString(Constants.KEY_USER_1_ID).equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                        val name = document.document.getString(Constants.KEY_USER_2_NAME).toString()
                        val image = document.document.getString(Constants.KEY_USER_2_IMAGE).toString()
                        val id = document.document.getString(Constants.KEY_USER_2_ID).toString()
                        val lastMess = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                        val time = document.document.getDate(Constants.KEY_TIME)
                        newList.add(RecentlyChatUser(name,image,id,lastMess,time!!))
                    } else {
                        val name = document.document.getString(Constants.KEY_USER_1_NAME).toString()
                        val image = document.document.getString(Constants.KEY_USER_1_IMAGE).toString()
                        val id = document.document.getString(Constants.KEY_USER_1_ID).toString()
                        val lastMess = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                        val time = document.document.getDate(Constants.KEY_TIME)
                        newList.add(RecentlyChatUser(name,image,id,lastMess,time!!))

                    }
                } else if(document.type == DocumentChange.Type.MODIFIED){
                    val user1 = document.document.getString(Constants.KEY_USER_1_ID)
                    val user2 = document.document.getString(Constants.KEY_USER_2_ID)

                    for( i in 1 until recentList.size){
                        if(recentList[i].id.equals(user1) && recentList[i].id.equals(user2)){
                            recentList[i].lastMess = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                            recentList[i].time = document.document.getDate(Constants.KEY_TIME)!!
                            break
                        }
                    }
                }
            }
            adapter.getData(newList)
            if(recentList.size >0){
                recentList.sortWith{o1, o2 -> o1.time.compareTo(o2.time)}
                binding.rvRecentList.visibility = View.VISIBLE;
                binding.tvNoConv.visibility = View.GONE;
            } else{
                binding.rvRecentList.visibility = View.GONE;
                binding.tvNoConv.visibility = View.VISIBLE;
            }


        }

    }

}