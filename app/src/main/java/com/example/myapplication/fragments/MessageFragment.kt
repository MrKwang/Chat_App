package com.example.myapplication.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.activities.ChatActivity
import com.example.myapplication.apdapter.RecentlyChatAdapter
import com.example.myapplication.databinding.FragmentMessageBinding
import com.example.myapplication.interfaces.OnItemCLickListener
import com.example.myapplication.model.RecentlyChatUser
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*


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


        //Search bar chưa dùng
        binding.searchView.setOnClickListener {
            Toast.makeText(activity, "Search something here", Toast.LENGTH_SHORT).show()

        }
        fetchData()
        adapter = RecentlyChatAdapter(recentList, object: OnItemCLickListener{
            override fun onClick(position: Int) {
                val intent = Intent(requireContext().applicationContext, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putString(Constants.KEY_NAME, recentList[position].username)
                bundle.putString(Constants.KEY_RECEIVE_ID, recentList[position].id)
                bundle.putString(Constants.KEY_IMAGE, recentList[position].image)
                bundle.putString(Constants.KEY_FCM_TOKEN, recentList[position].token)

                Log.d("BUNDLE", bundle.toString())
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onLongClick(position: Int): Boolean {
                Toast.makeText(activity,"Clicked", Toast.LENGTH_LONG).show()
                return true
            }
        })
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
            var newList: MutableList<RecentlyChatUser> = recentList.toMutableList();
            for (document in value.documentChanges ){

                if(document.type == DocumentChange.Type.ADDED){

                    if(document.document.getString(Constants.KEY_USER_1_ID).equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                        val name = document.document.getString(Constants.KEY_USER_2_NAME).toString()
                        val image = document.document.getString(Constants.KEY_USER_2_IMAGE).toString()
                        val id = document.document.getString(Constants.KEY_USER_2_ID).toString()
                        val lastMess =
                            document.document.getString(Constants.KEY_LAST_MESSAGE)?.trimStart()?.trimEnd().toString()
                        val timeStamp= document.document.getDate(Constants.KEY_TIME)
                        val token = document.document.getString(Constants.KEY_USER_2_TOKEN).toString()
                        if (timeStamp != null) {
                            newList.add(RecentlyChatUser(name,image,id,lastMess,
                                   timeStamp, setSendTime(timeStamp), token))
                        }

                    } else {
                        val name = document.document.getString(Constants.KEY_USER_1_NAME).toString()
                        val image = document.document.getString(Constants.KEY_USER_1_IMAGE).toString()
                        val id = document.document.getString(Constants.KEY_USER_1_ID).toString()
                        val lastMess = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                        val timeStamp = document.document.getDate(Constants.KEY_TIME)
                        val token = document.document.getString(Constants.KEY_USER_1_TOKEN).toString()
                        if (timeStamp != null) {
                            newList.add(RecentlyChatUser(name,image,id,lastMess,
                                timeStamp, setSendTime(timeStamp), token))
                        }


                    }
                } else if(document.type == DocumentChange.Type.MODIFIED){

                    newList = recentList.map { it.copy() } as MutableList<RecentlyChatUser>
                    val user1 = document.document.getString(Constants.KEY_USER_1_ID)
                    val user2 = document.document.getString(Constants.KEY_USER_2_ID)
                    val token1 = document.document.getString(Constants.KEY_USER_1_TOKEN).toString()
                    val token2 = document.document.getString(Constants.KEY_USER_2_TOKEN).toString()

                    for( i in 0 until newList.size){
                        if(newList[i].id == user1 || newList[i].id == user2){
                            newList[i].token = if(newList[i].id == user1) token1 else token2
                            newList[i].lastMess = document.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                            newList[i].time = document.document.getDate(Constants.KEY_TIME)!!
                            newList[i].sendTime = setSendTime(newList[i].time)
                            break
                        }
                    }
                }

            }

            newList.sortWith{ o1, o2 -> o2.time.compareTo(o1.time) }

            adapter.updateData(newList)

            if(recentList.size >0){

                binding.rvRecentList.visibility = View.VISIBLE;
                binding.tvNoConv.visibility = View.GONE;
            } else{
                binding.rvRecentList.visibility = View.GONE;
                binding.tvNoConv.visibility = View.VISIBLE;
            }

        }

    }

    private fun setSendTime(timeStamp: Date): String {

        val sentTime = if(System.currentTimeMillis() - timeStamp.time <= 23*3600*1000){
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeStamp)
        } else{
            SimpleDateFormat("EEE", Locale.getDefault()).format(timeStamp)
        }
        return  sentTime
    }


}