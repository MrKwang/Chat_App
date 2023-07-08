package com.example.myapplication.interfaces

interface OnItemCLickListener {
    fun onClick(position: Int)

    fun onLongClick(position: Int): Boolean
}