package com.example.stage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class UserListAdapter(private val context: Context, private val arrayList: java.util.ArrayList<User>): BaseAdapter() {

    private lateinit var username: TextView

    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false)

        username = convertView.findViewById(R.id.username)
        username.text = " " + arrayList[position].username
        return convertView
    }
}