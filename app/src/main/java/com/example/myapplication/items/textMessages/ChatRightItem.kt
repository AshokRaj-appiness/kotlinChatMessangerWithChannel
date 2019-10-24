package com.example.myapplication.items.textMessages

import android.content.Context
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.model.TextMessages
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.chat_right_row.*
import java.text.SimpleDateFormat

class ChatRightItem(val textMessages: TextMessages, val context: Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_right_message.text = textMessages.text
        setTime(viewHolder)
        setUserImage(viewHolder)
    }

    private fun setTime(viewHolder: GroupieViewHolder){
        val sdf = SimpleDateFormat("hh:mm a")
        val currentDate = sdf.format(textMessages.time)
        viewHolder.tv_time_right.text = currentDate
    }

    private fun setUserImage(viewHolder: GroupieViewHolder){
        FireStoreUtil.getUserImage(textMessages.senderId){
            GlideApp.with(context).load(StorageUtil.getImagePath(it)).into(viewHolder.iv_message_user_right_icon)
        }
    }

    override fun getLayout(): Int = R.layout.chat_right_row

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is ChatRightItem)
            return false
        if(this.textMessages != other.textMessages)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ChatRightItem)
    }

    fun toString1():String{
        return textMessages.toString()
    }
}