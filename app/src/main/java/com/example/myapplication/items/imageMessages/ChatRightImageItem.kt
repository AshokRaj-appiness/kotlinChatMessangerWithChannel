package com.example.myapplication.items.imageMessages

import android.content.Context
import android.util.Log
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.model.ImageMessages
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.chat_image_left.*
import kotlinx.android.synthetic.main.chat_image_right.*
import kotlinx.android.synthetic.main.chat_left_row.*
import java.text.SimpleDateFormat

class ChatRightImageItem(val imageMessages: ImageMessages, val context: Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        GlideApp.with(context).load(StorageUtil.getImagePath(imageMessages.imagePath)).placeholder(R.drawable.ic_image_black_24dp).into(viewHolder.iv_chat_image_right)
        setTime(viewHolder)
        setUserImage(viewHolder)
    }
    private fun setTime(viewHolder: GroupieViewHolder){
        val sdf = SimpleDateFormat("hh:mm a")
        val currentDate = sdf.format(imageMessages.time)
        viewHolder.tv_chat_image_right_time.text = currentDate
    }

    private fun setUserImage(viewHolder: GroupieViewHolder){
        FireStoreUtil.getUserImage(imageMessages.senderId){
            GlideApp.with(context).load(StorageUtil.getImagePath(it)).into(viewHolder.iv_chat_user_right_image_icon)
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is ChatRightImageItem)
            return false
        if(this.imageMessages != other.imageMessages)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ChatRightImageItem)
    }

    override fun getLayout(): Int = R.layout.chat_image_right

    fun toString1():String{
        return imageMessages.toString()
    }
}