package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.interfaces.OnItemCLickListener
import com.example.myapplication.activities.ChatActivity
import com.example.myapplication.apdapter.UserListAdapter
import com.example.myapplication.databinding.FragmentContactBinding
import com.example.myapplication.model.UserList
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentContactBinding
private lateinit var preferenceManager: Preference
class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater,container,false)
        preferenceManager = Preference(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
    }

    private fun getUser() {
        binding.progressBar3.visibility = View.VISIBLE
        val database = FirebaseFirestore.getInstance()
        val userList = mutableListOf<UserList>()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnSuccessListener{
                if(!it.isEmpty){
                    for(document in it.documents){
                        if(preferenceManager.getString(
                                Constants.KEY_USER_ID) == document.id ) continue
                        val image = document.get(Constants.KEY_IMAGE).toString()
                        val name = document.get(Constants.KEY_NAME).toString()
                        val token = document.get(Constants.KEY_FCM_TOKEN).toString()
                        val id = document.id
                        userList.add(UserList(name,image,token,id))
                    }
                    val adapter = UserListAdapter(userList, object: OnItemCLickListener {
                        override fun onClick(position: Int) {
                            val intent = Intent(requireContext(), ChatActivity::class.java)
                            intent.putExtra(Constants.KEY_NAME, userList[position].username)
                            intent.putExtra(Constants.KEY_IMAGE, userList[position].image)
                            intent.putExtra(Constants.KEY_RECEIVE_ID, userList[position].id)
                            intent.putExtra(Constants.KEY_FCM_TOKEN, userList[position].token)
                            startActivity(intent)

                        }
                    })
                    binding.rvUserList.adapter = adapter
                    binding.rvUserList.setHasFixedSize(true)
                    binding.rvUserList.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                } else{
                    binding.rvUserList.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE

                }
                binding.progressBar3.visibility = View.GONE

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Data retrieve failed", Toast.LENGTH_SHORT).show()
            }
    }


}