package com.example.myapplication.activities

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity : AppCompatActivity() {

    private lateinit var documentReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = Preference(applicationContext)
        val database = FirebaseFirestore.getInstance()
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID).toString())

        checkPermission(Manifest.permission.POST_NOTIFICATIONS, 100)
    }

    fun checkPermission(permission: String, requestCode: Int){
        when{
            ActivityCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity(), permission) -> {showDialog(permission, requestCode)}

            else -> {
                ActivityCompat.requestPermissions(this@BaseActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Refused", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog(permission: String, requestCode: Int){
        val builder = AlertDialog.Builder(applicationContext)

        builder.apply {
            setTitle("Permission Required")
            setMessage("Permission is needed to access to use this app")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@BaseActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }

        builder.create().show()
    }
    override fun onResume() {
        super.onResume()
        documentReference.update(Constants.KEY_AVAILABILITY, 1)
    }

    override fun onPause() {
        super.onPause()
        documentReference.update(Constants.KEY_AVAILABILITY, 0)
    }


}