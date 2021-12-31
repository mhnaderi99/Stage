package com.example.stage.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.stage.R
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.runBlocking
import responses.UserResponse
import java.io.File
import com.squareup.picasso.Picasso


class UserListAdapter(private val context: Context, private val arrayList: java.util.ArrayList<UserResponse>): BaseAdapter() {

    private lateinit var username: TextView
    private lateinit var userImage: ImageView

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
        userImage = convertView.findViewById(R.id.userImage)
        username.text = " " + arrayList[position].username

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${arrayList[position].id}").into(userImage)

        return convertView
    }
}