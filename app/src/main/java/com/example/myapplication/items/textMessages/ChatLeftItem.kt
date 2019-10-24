package com.example.myapplication.items.textMessages

import android.content.Context
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.model.TextMessages
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.chat_left_row.*
import java.text.SimpleDateFormat


class ChatLeftItem(val textMessages: TextMessages, val context: Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_left_message.text = textMessages.text
        setTime(viewHolder)
        setUserImage(viewHolder)
    }

    private fun setTime(viewHolder: GroupieViewHolder){
        val sdf = SimpleDateFormat("hh:mm a")
        val currentDate = sdf.format(textMessages.time)
        viewHolder.tv_left_time.text = currentDate
    }

    private fun setUserImage(viewHolder: GroupieViewHolder){
        FireStoreUtil.getUserImage(textMessages.senderId){
                GlideApp.with(context).load(StorageUtil.getImagePath(it)).into(viewHolder.iv_message_user_icon)
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
       if(other !is ChatLeftItem)
           return false
        if(this.textMessages != other.textMessages)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ChatLeftItem)
    }

    override fun getLayout(): Int = R.layout.chat_left_row

    fun toString1():String{
        return textMessages.toString()
    }
}