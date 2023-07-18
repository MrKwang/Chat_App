package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.myapplication.animations.ZoomOutPageTransformer
import com.example.myapplication.apdapter.ViewPagerAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.system.exitProcess


@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityMainBinding
private lateinit var preferenceManager: Preference
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding  = ActivityMainBinding.inflate(layoutInflater)
        preferenceManager = Preference(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(this@MainActivity)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.setPageTransformer(ZoomOutPageTransformer())

        setBackPress()               //back press
        setOnViewPagerChanges()      //change fragment on viewpager when navigation bar is clicked
        setOnNavigationIconChanges() //change icon menu when slide between 2 fragments
        loadImage()
        getToken()

        binding.btnAdd.setOnClickListener {
            openNewMessage()
        }

    }

    private fun openNewMessage() {
        val intent = Intent(this@MainActivity, NewMessageActivity::class.java)
        startActivity((intent))
    }

    private fun loadImage() {
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun setBackPress() {
        var backPressedTime: Long = 0
        onBackPressedDispatcher.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(backPressedTime + 2000 > System.currentTimeMillis()){
                    moveTaskToBack(true)     // Function to end
                    exitProcess(-1)            // application
                } else
                {
                    Toast.makeText(this@MainActivity, "Press back again to exit" , Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun setOnNavigationIconChanges() {
        binding.viewPager2.registerOnPageChangeCallback(object: OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> binding.botNavigation.setCurrentActiveItem(0)
                    1 -> binding.botNavigation.setCurrentActiveItem(1)
                    2 -> binding.botNavigation.setCurrentActiveItem(2)
                    3 -> binding.botNavigation.setCurrentActiveItem(3)
                    else -> binding.botNavigation.setCurrentActiveItem(0)
                }
            }
        })

    }

    private fun setOnViewPagerChanges() {
        binding.botNavigation.setNavigationChangeListener { view, position ->
            when(position){
                0 -> binding.viewPager2.currentItem = 0
                1-> binding.viewPager2.currentItem = 1
                2-> binding.viewPager2.currentItem = 2
                3-> binding.viewPager2.currentItem = 3
                else -> binding.viewPager2.currentItem = 0
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager2.unregisterOnPageChangeCallback(object: OnPageChangeCallback(){})

    }

    private fun updateToken(token: String){
        val database = FirebaseFirestore.getInstance()
        val documentReference =  database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )

        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Token updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "Unable to update token!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }
}





