package com.example.stage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import responses.Post
import com.example.stage.R
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import responses.CommentResponse
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

import androidx.core.graphics.drawable.RoundedBitmapDrawable

import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap




class MovieCommentAdapter(private val context: Context, private val arrayList: java.util.ArrayList<CommentResponse>): BaseAdapter() {


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
        convertView = LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false)

        val usernameTextView: TextView = convertView.findViewById(R.id.user_textview)
        val commentTextView: TextView = convertView.findViewById(R.id.comment_textview)
        val imageview: ImageView = convertView.findViewById(R.id.imageView)

        val comment =  arrayList[position]
        commentTextView.text = comment.comment_text
        usernameTextView.text = comment.username

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${comment.user_id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(imageview, object : Callback {
                override fun onSuccess() {
                    val imageBitmap = (imageview.getDrawable() as BitmapDrawable).bitmap
                    val imageDrawable =
                        RoundedBitmapDrawableFactory.create(context.resources, imageBitmap)
                    imageDrawable.isCircular = true
                    imageDrawable.cornerRadius =
                        Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
                    imageview.setImageDrawable(imageDrawable)
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

        return convertView
    }
}