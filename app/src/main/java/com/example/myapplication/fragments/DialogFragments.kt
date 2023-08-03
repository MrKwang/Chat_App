package com.example.myapplication.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.DialogEditBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class DialogFragments : DialogFragment() {

    private lateinit var binding: DialogEditBinding
    private lateinit var preferenceManager: Preference
    private lateinit var database: FirebaseFirestore
    private var encodedImage: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        database = FirebaseFirestore.getInstance()
        preferenceManager = Preference(requireContext().applicationContext)
        binding = DialogEditBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.btnSave.setOnClickListener {
            updateProfile()
            dismiss()
            Toast.makeText(requireContext(),"Saved!", Toast.LENGTH_SHORT).show()
        }

        binding.layoutImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            getImage.launch(intent)
        }

        loadImage()
        binding.edtNameEdit.setText(preferenceManager.getString(Constants.KEY_NAME))
        binding.edtPassEdit.setText(preferenceManager.getString(Constants.KEY_PASSWORD))

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        return dialog
    }

    private fun updateProfile(){
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
            .get()
            .addOnSuccessListener {
                val id = it.documents[0].id
                val hashmap = HashMap<String, Any>()
                hashmap[Constants.KEY_NAME] = binding.edtNameEdit.text.trim().toString()
                hashmap[Constants.KEY_PASSWORD] = binding.edtPassEdit.text.trim().toString()
                if(encodedImage != null)  hashmap[Constants.KEY_IMAGE] = encodedImage!!
                database.collection(Constants.KEY_COLLECTION_USERS).document(id)
                    .update(hashmap)

            }

        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_USER_1_NAME, preferenceManager.getString(Constants.KEY_NAME))
            .get()
            .addOnSuccessListener {
                for(doc in it.documents) {
                    database.collection(Constants.KEY_COLLECTION_CONVERSATION).document(doc.id)
                        .update(
                            Constants.KEY_USER_1_NAME,
                            binding.edtNameEdit.text.trim().toString()
                        )
                }
            }

        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_USER_2_NAME, preferenceManager.getString(Constants.KEY_NAME))
            .get()
            .addOnSuccessListener {
                for(doc in it.documents) {
                    database.collection(Constants.KEY_COLLECTION_CONVERSATION).document(doc.id)
                        .update(
                            Constants.KEY_USER_2_NAME,
                            binding.edtNameEdit.text.trim().toString()
                        )
                }
            }

        preferenceManager.putString(Constants.KEY_NAME, binding.edtNameEdit.text.trim().toString())
    }
    private fun loadImage() {
        val bytes = Base64.decode( preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imgProfile.setImageBitmap(bitmap)
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            if(it.data != null){
                val imageUri: Uri? = it.data!!.data
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imgProfile.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)
                } catch ( e: FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String?{
        val scaledWidth = 150
        val scaledHeight = scaledWidth * bitmap.height / bitmap.width
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap,scaledWidth,scaledHeight,false)   //Create a bitmap with scaled resolution
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)      //Nén ảnh bằng định dạng JPEG với 100% chất lượng và ghi vào baos
        val bytes = baos.toByteArray()                      //Chuyển sang dạng byte array
        return Base64.encodeToString(bytes,Base64.DEFAULT)  //Encode bytes to string

    }
}