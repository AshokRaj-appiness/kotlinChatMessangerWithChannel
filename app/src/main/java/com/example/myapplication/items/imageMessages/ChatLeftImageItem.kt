package com.example.myapplication.items.imageMessages

import android.content.Context
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.model.ImageMessages
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.chat_image_left.*
import kotlinx.android.synthetic.main.chat_left_row.*
import java.text.SimpleDateFormat

class ChatLeftImageItem(val imageMessages: ImageMessages, val context: Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        GlideApp.with(context).load(StorageUtil.getImagePath(imageMessages.imagePath)).placeholder(R.drawable.ic_image_black_24dp).into(viewHolder.iv_chat_image_left)
        setTime(viewHolder)
        setUserImage(viewHolder)
    }
    private fun setTime(viewHolder: GroupieViewHolder){
        val sdf = SimpleDateFormat("hh:mm a")
        val currentDate = sdf.format(imageMessages.time)
        viewHolder.tv_chat_image_left_time.text = currentDate
    }

    private fun setUserImage(viewHolder: GroupieViewHolder){
        FireStoreUtil.getUserImage(imageMessages.senderId){
            GlideApp.with(context).load(StorageUtil.getImagePath(it)).into(viewHolder.iv_chat_user_left_image_icon)
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is ChatLeftImageItem)
            return false
        if(this.imageMessages != other.imageMessages)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? ChatLeftImageItem)
    }

    override fun getLayout(): Int = R.layout.chat_image_left

    fun toString1():String{
        return imageMessages.toString()
    }
}