package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivitySignUpBinding
private lateinit var preferenceManager: Preference
private var encodedImage: String? = null
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        preferenceManager = Preference(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        binding.edtSignIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnSignUp.setOnClickListener {
            if(signUpCheck()){
                loading(true)
                signUp()
            }
        }

        binding.imageLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            getImage.launch(intent)
        }
    }


    private fun signUp() {
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user : HashMap<String, Any> = HashMap()
        user[Constants.KEY_NAME] = binding.edtUsername.text.toString()
        user[Constants.KEY_PASSWORD] = binding.edtPass.text.toString()
        user[Constants.KEY_EMAIL] = binding.edtEmail.text.toString()
        user[Constants.KEY_IMAGE] = encodedImage!!
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                preferenceManager.putString(Constants.KEY_USER_ID,it.id)
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage)
                preferenceManager.putString(Constants.KEY_NAME, binding.edtUsername.text.toString())
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                makeToast(it.message.toString())
            }

    }

    private fun makeToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUpCheck(): Boolean{
        if (encodedImage == null)
        {
            makeToast("Please select a profile image!")
            return false
        } else if (binding.edtUsername.text.toString().trim().isEmpty()){
            makeToast("Please enter your username!")
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()){
            makeToast("Invalid Email!")
            return false
        } else if(binding.edtPass.text.toString().trim().isEmpty()) {
            makeToast("Please enter your password!")
            return false
        } else if(binding.edtRePass.text.toString().trim().isEmpty()) {
            makeToast("Please confirm your password!")
            return false
        } else if (binding.edtRePass.text.toString() != binding.edtPass.text.toString()){
            makeToast("Password and Confirm Password must be same!")
            return false
        } else return true
    }

    //function to convert image to encoded string
    private fun encodeImage(bitmap: Bitmap): String?{
        val scaledWidth = 150
        val scaledHeight = scaledWidth * bitmap.height / bitmap.width
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,scaledWidth,scaledHeight,false)   //Create a bitmap with scaled resolution
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)      //Nén ảnh bằng định dạng JPEG với 100% chất lượng và ghi vào baos
        val bytes = baos.toByteArray()                      //Chuyển sang dạng byte array
        return Base64.encodeToString(bytes,Base64.DEFAULT)  //Encode bytes to string


    }

    //Function to select image from device
    private val getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if(it.resultCode == RESULT_OK){
                    if(it.data != null){
                        val imageUri: Uri? = it.data!!.data
                        try {
                            val inputStream = contentResolver.openInputStream(imageUri!!)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.image.setImageBitmap(bitmap)
                            binding.textImage.visibility= View.GONE
                            encodedImage = encodeImage(bitmap)
                        } catch ( e: FileNotFoundException){
                            e.printStackTrace()
                        }
                    }
                }
            }
    )

    //function to show loading bar
    private fun loading(state: Boolean){
        if(state)
        {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.INVISIBLE
        } else
        {
            binding.progressBar.visibility = View.INVISIBLE
            binding.btnSignUp.visibility = View.VISIBLE
        }
    }
}