package com.example.myapplication.model

data class User (
    var name: String,
    var id: String,
    //var image: String,
    var token: String
) : java.io.Serializable