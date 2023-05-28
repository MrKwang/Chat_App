package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.interfaces.OnItemCLickListener
import com.example.myapplication.apdapter.UserListAdapter
import com.example.myapplication.databinding.ActivityNewMesssageBinding
import com.example.myapplication.model.UserList
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityNewMesssageBinding
private lateinit var preferenceManager: Preference
class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewMesssageBinding.inflate(layoutInflater)
        preferenceManager = Preference(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getUser()
        setListener()

    }

    private fun setListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getUser() {
        val database = FirebaseFirestore.getInstance()
        val userList = mutableListOf<UserList>()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnSuccessListener{
                if(!it.isEmpty){
                    for(document in it.documents){
                        if(preferenceManager.getString(Constants.KEY_USER_ID) == document.id ) continue
                        val image = document.get(Constants.KEY_IMAGE).toString()
                        val name = document.get(Constants.KEY_NAME).toString()
                        val token = document.get(Constants.KEY_FCM_TOKEN).toString()
                        val id = document.id
                        userList.add(UserList(name,image,token,id))
                    }
                    val adapter = UserListAdapter(userList, object: OnItemCLickListener {
                        override fun onClick(position: Int) {
                            val intent = Intent(this@NewMessageActivity, ChatActivity::class.java)
                            intent.putExtra(Constants.KEY_NAME, userList[position].username)
                            intent.putExtra(Constants.KEY_IMAGE, userList[position].image)
                            intent.putExtra(Constants.KEY_RECEIVE_ID, userList[position].id)
                            intent.putExtra(Constants.KEY_FCM_TOKEN, userList[position].token)
                            startActivity(intent)
                            finish()
                        }
                    })
                    binding.rvUserList.adapter = adapter
                    binding.rvUserList.setHasFixedSize(true)
                    binding.rvUserList.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                } else
                    binding.tvError.visibility = View.VISIBLE

            }
            .addOnFailureListener {
                Toast.makeText(this@NewMessageActivity, "Data retrieve failed", Toast.LENGTH_SHORT).show()
            }

    }
}