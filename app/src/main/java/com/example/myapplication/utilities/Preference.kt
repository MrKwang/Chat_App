package com.example.myapplication.utilities
import android.content.Context
import android.content.SharedPreferences

private lateinit var sharedPreferences: SharedPreferences
class Preference(){
    constructor(context: Context) : this() {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun putBoolean(key:String, value: Boolean){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean{
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String, value: String?){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clear(){
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}