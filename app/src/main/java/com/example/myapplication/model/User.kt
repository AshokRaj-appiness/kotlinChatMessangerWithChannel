package com.example.myapplication.model

data class User(val name:String,
                val bio: String,
                val profileImagePath:String?,
                val firebaseId:MutableList<String>) {
    constructor():this("","",null, mutableListOf())
}
