package com.example.myapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.apdapter.ChatAdapter
import com.example.myapplication.databinding.ActivityChatBinding
import com.example.myapplication.interfaces.APIService
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.network.APIClient
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var preferenceManager: Preference
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: ChatAdapter
    private var userChatReceive: MutableList<ChatMessage> = mutableListOf()
    private var conversationId: String? = null
    private var isUserOnline = false
    private var imageUser: Bitmap? = null
    private lateinit var receiveId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        preferenceManager = Preference(applicationContext)
        database = FirebaseFirestore.getInstance()
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        receiveId = intent.extras?.getString(Constants.KEY_RECEIVE_ID).toString()
        imageUser = getBitmap(intent.getStringExtra(Constants.KEY_IMAGE).toString())

        adapter = ChatAdapter(
            imageUser,
            userChatReceive,
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
        initView()
        loadMessage()
        setListener()
    }

   private fun initView(){

        binding.rvChatScreen.adapter = adapter
        binding.rvChatScreen.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        binding.rvChatScreen.setHasFixedSize(true)
        (binding.rvChatScreen.layoutManager as LinearLayoutManager).stackFromEnd =  true
        if(imageUser != null) binding.imgChatUser.setImageBitmap(imageUser)
        binding.tvUsername.text = intent.extras?.getString(Constants.KEY_NAME).toString()

    }

   private val eventListener: EventListener<QuerySnapshot> = EventListener { value, error ->
       if(error != null) {
           return@EventListener
       }
       if(value != null){
           for (document in value.documentChanges){
               if(document.type == DocumentChange.Type.ADDED) {
                   val id = document.document.id
                   val message = document.document.getString(Constants.KEY_MESSAGE).toString()
                   val sendId = document.document.getString(Constants.KEY_SEND_ID).toString()
                   val receiveId = document.document.getString(Constants.KEY_RECEIVE_ID).toString()
                   val timeStamp = document.document.getDate(Constants.KEY_TIME)
                   val day = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timeStamp!!)
                   val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeStamp)
                   userChatReceive.add(ChatMessage(id, sendId, receiveId, message,timeStamp, day, time))
               }
           }
           userChatReceive.sortWith{ o1, o2 -> o1.timeStamp.compareTo(o2.timeStamp) }


           if(userChatReceive.size == 0){
               binding.tvNoMessage.visibility = View.VISIBLE
           } else {
               binding.rvChatScreen.scrollToPosition(userChatReceive.size - 1)
               binding.tvNoMessage.visibility = View.GONE
           }

           val newList = userChatReceive.mapIndexed { index, chatMessage ->
               val nextMessage = userChatReceive.getOrNull(index + 1)
               chatMessage.copy(showAva = shouldShowAvatar(nextMessage,chatMessage))
           }

           adapter.getData(newList.toMutableList())

       }
       if(conversationId == null){
           fetchRecentlyMessage()
       }
       loading(false)
   }

    private fun shouldShowAvatar(nextMess: ChatMessage?, currentMess: ChatMessage): Boolean {

        val result = if(nextMess == null) {
            true
        } else {
            if(currentMess.sendId == nextMess.sendId){
                val currentTime = currentMess.timeStamp.time
                val nextTime = nextMess.timeStamp.time
                (nextTime - currentTime) / (60 * 1000) >= 2
            } else (nextMess.sendId == preferenceManager.getString(Constants.KEY_USER_ID))

        }
        return result
    }

    private fun loadMessage(){
        loading(true)

        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SEND_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVE_ID,receiveId)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_RECEIVE_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_SEND_ID, receiveId)
            .addSnapshotListener(eventListener)


    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
            .show()    }
    private fun sendNotification(body: String){

        APIClient.getClient().create(APIService::class.java)
            .sendMessage(Constants.getRemoteHeaders(),  body)
            .enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful){
                        try {
                            if (!response.body().isNullOrEmpty()){
                                val responseJson = JSONObject(response.body().toString())
                                Log.e("FCM Token", "${response.body()} \n $body")
                                val result = responseJson.getJSONArray("results")
                                if(responseJson.getInt("failure") == 1){
                                    val error = result.get(0) as JSONObject
                                    showToast("Error " + error.getString("error").toString())
                                }
                            }
                        } catch (e: JSONException){
                            e.printStackTrace()
                        }
                        showToast("Sent successfully")
                    } else showToast("Response " + response.code())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    showToast("onFailure " + t.message.toString())
                }

            })
    }

    private fun sendToFireStore(){
        val date = Date()
        val message: HashMap<String, Any> = HashMap()
        message[Constants.KEY_SEND_ID] = preferenceManager.getString(Constants.KEY_USER_ID).toString()
        message[Constants.KEY_RECEIVE_ID] = receiveId
        message[Constants.KEY_MESSAGE] = binding.edtInputMessage.text.trimStart().trimEnd().toString()
        message[Constants.KEY_TIME] = date
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)

        if(conversationId != null){
            updateConversation(binding.edtInputMessage.text.toString())
        } else {

            val conversation : HashMap<String, Any > = HashMap()
            conversation[Constants.KEY_USER_1_ID] = preferenceManager.getString(Constants.KEY_USER_ID).toString()
            conversation[Constants.KEY_USER_1_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE).toString()
            conversation[Constants.KEY_USER_1_NAME] = preferenceManager.getString(Constants.KEY_NAME).toString()
            conversation[Constants.KEY_USER_1_TOKEN] = preferenceManager.getString(Constants.KEY_FCM_TOKEN).toString()
            conversation[Constants.KEY_USER_2_ID] = receiveId
            conversation[Constants.KEY_USER_2_IMAGE] = intent.getStringExtra(Constants.KEY_IMAGE).toString()
            conversation[Constants.KEY_USER_2_NAME] = intent.extras?.getString(Constants.KEY_NAME).toString()
            conversation[Constants.KEY_USER_2_TOKEN] =
                intent.extras?.getString(Constants.KEY_FCM_TOKEN).toString()
            conversation[Constants.KEY_TIME] = date
            conversation[Constants.KEY_LAST_MESSAGE] = binding.edtInputMessage.text.trimStart().trimEnd().toString()
            newConversation(conversation)
        }

        if(!isUserOnline){
            try {
                val tokens = JSONArray()
                tokens.put(intent.getStringExtra(Constants.KEY_FCM_TOKEN))

                val data = JSONObject()
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN))
                data.put(Constants.KEY_MESSAGE, binding.edtInputMessage.text.trimStart().trimEnd().toString())
                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

                sendNotification(body.toString())
            } catch (e: Exception){
                showToast( e.message.toString() )
            }
        }

        binding.edtInputMessage.text = null
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSend.setOnClickListener{
            if(!binding.edtInputMessage.text.isNullOrEmpty()){
                sendToFireStore()
            }
        }
        binding.cardView.setOnClickListener {
            binding.rvChatScreen.scrollToPosition(userChatReceive.size - 1)
        }
    }


    private fun getBitmap(img: String?): Bitmap? {
        return if(img != null){
            val bytes = Base64.decode(img, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes,0, bytes.size)
        } else null
    }

    private fun loading(state: Boolean){
        if(state)
        {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvChatScreen.visibility = View.GONE
        } else
        {
            binding.progressBar.visibility = View.GONE
            binding.rvChatScreen.visibility = View.VISIBLE
        }
    }
    // Return conversationId if condition is true  --> set value for conversationId
    private fun fetchRecentlyMessage(){
        if(userChatReceive.size > 0){
            checkConversation(preferenceManager.getString(Constants.KEY_USER_ID).toString(), receiveId)      //2 function return only 1 value for conversationId
            checkConversation( receiveId, preferenceManager.getString(Constants.KEY_USER_ID).toString())
        }
    }

    //check if conversation is existed , get id of that conversation
    private fun checkConversation(sendId: String, receiveId: String){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_USER_1_ID, sendId)
            .whereEqualTo(Constants.KEY_USER_2_ID,receiveId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    //add new conversation to collection
    private fun newConversation(conversation : HashMap<String, Any>){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .add(conversation)
            .addOnSuccessListener {
                conversationId = it.id
            }
    }

    private fun updateConversation(message: String ){
        val date = Date()
        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .document(conversationId.toString())
            .update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIME, date
                )

    }

    private val onCompleteListener : OnCompleteListener<QuerySnapshot> = OnCompleteListener {
        if(it.isSuccessful && it.result!= null && !it.result.isEmpty){
            val snapshot = it.result.documents[0]
            conversationId = snapshot.id
        }
    }

    private fun listenAvailability() {

        database.collection(Constants.KEY_COLLECTION_USERS).document(receiveId)
            .addSnapshotListener(this@ChatActivity, EventListener { value, error ->
                if(error != null){
                    return@EventListener
                }

                if(value != null){
                    if (value.get(Constants.KEY_AVAILABILITY) != null){
                        val availability = value.getLong(Constants.KEY_AVAILABILITY)?.toInt()
                        isUserOnline =  availability == 1
                    }
                    if(imageUser == null){
                        imageUser = getBitmap(value.getString(Constants.KEY_IMAGE))
                        imageUser?.let {
                            adapter.setReceivedBitmap(it)
                            binding.imgChatUser.setImageBitmap(it)
                        }
                        adapter.notifyItemRangeChanged(0, userChatReceive.size)
                    }
                }

                if(isUserOnline){
                    binding.availability.setText(R.string.online)
                } else
                    binding.availability.setText(R.string.offline)
            })


    }

    override fun onResume() {
        super.onResume()
        listenAvailability()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show()
        if(intent != null && intent.hasExtra(Constants.KEY_USER_ID)){
            Log.d("NEW INTENT", Constants.KEY_USER_ID)
        } else
            Log.d("NEW INTENT", "null")

    }
}