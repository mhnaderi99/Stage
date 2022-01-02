package com.example.stage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.stage.R
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import responses.CommentResponse

class UserCommentAdapter(private val context: Context, private val arrayList: java.util.ArrayList<CommentResponse>): BaseAdapter() {

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
        convertView = LayoutInflater.from(context).inflate(R.layout.user_comment_row, parent, false)

        movieName = convertView.findViewById(R.id.movieName)
        username = convertView.findViewById(R.id.username)
        comment = convertView.findViewById(R.id.comment)
        avatar = convertView.findViewById(R.id.avatar)

        movieName.text = " " + arrayList[position].title
        username.text = " " + arrayList[position].username
        comment.text = arrayList[position].comment_text

        //Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${arrayList[position].id}").into(avatar)

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${arrayList[position].id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(avatar, object : Callback {
                override fun onSuccess() {
                    println("success")
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })


        return convertView
    }
}