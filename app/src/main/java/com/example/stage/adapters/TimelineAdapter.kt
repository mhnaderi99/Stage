package com.example.stage.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.FragmentManager
import responses.Post
import com.example.stage.R
import com.example.stage.fragments.MovieFragment
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import responses.CommentResponse

class TimelineAdapter(
    private val context: Context,
    private val arrayList: java.util.ArrayList<CommentResponse>): BaseAdapter(){

    private lateinit var movieName: TextView
    private lateinit var username: TextView
    private lateinit var comment: TextView
    private lateinit var avatar: ImageView
    private lateinit var userAvatar: ImageView




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
        userAvatar = convertView.findViewById(R.id.user_avatar)


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

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${arrayList[position].user_id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(userAvatar, object : Callback {
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