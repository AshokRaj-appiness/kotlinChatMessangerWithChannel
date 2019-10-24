package com.example.myapplication.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.ChatActivity

import com.example.myapplication.R
import com.example.myapplication.items.userListDisplay.PersonItem
import com.example.myapplication.utils.AppConstants
import com.example.myapplication.utils.FireStoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_peoples.*
import org.jetbrains.anko.support.v4.startActivity

class PeoplesFragment : Fragment() {

    private lateinit var userAddedListener:ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var userSection:Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_peoples, container, false)
        val group_adapter = GroupAdapter<GroupieViewHolder>()
        userAddedListener = FireStoreUtil.userAddedListener(this.context!!){
            if(shouldInitRecyclerView){
                userSection = Section(it)
                group_adapter.add(userSection)
                rv_user.adapter = group_adapter
                rv_user.addItemDecoration(DividerItemDecoration(this.context,DividerItemDecoration.VERTICAL))
                shouldInitRecyclerView = false
            }else userSection.update(it)

        }
        group_adapter.setOnItemClickListener { item, view ->
            if(item is PersonItem)
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.person.name,
                AppConstants.USER_ID to item.userId
            )
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        FireStoreUtil.removeListener(userAddedListener)
        shouldInitRecyclerView = true
    }


}
