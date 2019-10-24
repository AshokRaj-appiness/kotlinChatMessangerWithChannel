package com.example.myapplication.items.textMessages

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.model.TextMessages
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class TextMessageItem(val textMessages: TextMessages,val context:Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int = R.layout.message_item
}