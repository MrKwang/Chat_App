package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityLoginBinding
private lateinit var preferenceManager: Preference
class LoginActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        preferenceManager = Preference(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListener()

    }

    private fun setListener() {
        binding.edtSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            if(checkLogin()){
                login()
            }
        }
    }

    private fun checkLogin(): Boolean{
        if (binding.edtEmail.text.toString().trim().isEmpty()){
            makeToast("Please enter your email!")
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()){
            makeToast("Invalid Email!")
            return false
        } else if(binding.edtPass.text.toString().trim().isEmpty()) {
            makeToast("Please enter your password!")
            return false
        } else return true

    }

    private fun login(){
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.edtEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.edtPass.text.toString())
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result != null && it.result.documents.size >0){
                    val documentSnapshot = it.result.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(
                        Constants.KEY_NAME,
                        documentSnapshot.getString(Constants.KEY_NAME))
                    preferenceManager.putString(
                        Constants.KEY_IMAGE,
                        documentSnapshot.getString(Constants.KEY_IMAGE))
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    startActivity(intent)
                } else{
                    makeToast("Wrong email or password!!")
                    loading(false)
                }

            }
    }

    private fun makeToast(message: String){
            Toast.makeText(applicationContext,  message, Toast.LENGTH_SHORT).show()
    }
    private fun loading(state: Boolean){
        if(state)
        {
            binding.progressBar2.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.INVISIBLE
        } else
        {
            binding.progressBar2.visibility = View.INVISIBLE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }
}

