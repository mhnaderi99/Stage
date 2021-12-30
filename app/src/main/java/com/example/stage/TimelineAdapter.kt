package com.example.stage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class TimelineAdapter(private val context: Context, private val arrayList: java.util.ArrayList<Post>): BaseAdapter() {

    private lateinit var movieName: TextView
    private lateinit var username: TextView
    private lateinit var comment: TextView
    private lateinit var avatar: ImageView

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
        convertView = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false)

        movieName = convertView.findViewById(R.id.movieName)
        username = convertView.findViewById(R.id.username)
        comment = convertView.findViewById(R.id.comment)
        avatar = convertView.findViewById(R.id.avatar)

        movieName.text = " " + arrayList[position].movieName
        username.text = " " + arrayList[position].username
        comment.text = arrayList[position].comment
        avatar.setBackgroundResource(arrayList[position].avatar)
        return convertView
    }
}