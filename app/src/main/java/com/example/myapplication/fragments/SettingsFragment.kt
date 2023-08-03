package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.utilities.Constants
import com.example.myapplication.utilities.Preference

@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentSettingsBinding
private lateinit var preferenceManager: Preference
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater,container, false)
        preferenceManager = Preference(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListener()
        setUserInfor()
    }

    private fun setListener() {
        binding.layoutLogOut.setOnClickListener {
            preferenceManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK))
            startActivity(intent)
        }

        binding.btnEdit.setOnClickListener {
            val showPopup = DialogFragments()
            showPopup.show(parentFragmentManager, "showPopUp")


        }
    }

    private fun showEditDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_edit)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun loadImage() {
        val bytes = Base64.decode(
            preferenceManager.getString(
                Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.civUser.setImageBitmap(bitmap)
    }

    private fun setUserInfor(){
        loadImage()
        binding.tvUsername.text = preferenceManager.getString(Constants.KEY_NAME)
        binding.tvEmail.text = preferenceManager.getString(Constants.KEY_EMAIL)
    }
}