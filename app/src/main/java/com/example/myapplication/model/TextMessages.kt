package com.example.myapplication.model

import com.example.myapplication.`interface`.MessageType
import com.example.myapplication.`interface`.Messages
import java.util.*

data class TextMessages(val text: String,
                       val time: Date,
                       val senderId: String,
                       val type: String = MessageType.TEXT){
    constructor():this("",Date(0),"","")
}