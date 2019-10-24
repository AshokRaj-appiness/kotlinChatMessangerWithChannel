package com.example.myapplication.items.userListDisplay

import android.content.Context
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.example.myapplication.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person: User,val userId:String,val context: Context): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tv_name.text = person.name
        viewHolder.tv_bio.text = person.bio
        if(person.profileImagePath!=null)
            GlideApp.with(context).load(StorageUtil.getImagePath(person.profileImagePath)).into(viewHolder.iv_profile)
    }

    override fun getLayout(): Int = R.layout.item_person
}