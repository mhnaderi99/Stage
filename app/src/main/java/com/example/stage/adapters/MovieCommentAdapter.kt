package com.example.stage.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.example.stage.responses.CommentResponse
import com.example.stage.responses.UserResponse


class MovieCommentAdapter(private var dataSet: ArrayList<CommentResponse>, val ctx: Context) :
    RecyclerView.Adapter<MovieCommentAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val username: TextView = view.findViewById(R.id.user_textview)
        val comment: TextView = view.findViewById(R.id.comment_textview)
        val userImage: ImageView = view.findViewById(R.id.imageView)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.comment_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.username.text = dataSet[position].username
        viewHolder.comment.text = dataSet[position].comment_text

        Picasso.get()
            .load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${dataSet[position].user_id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(viewHolder.userImage, object : Callback {
                override fun onSuccess() {
//                    val imageBitmap = (viewHolder.userImage.getDrawable() as BitmapDrawable).bitmap
//                    val imageDrawable =
//                        RoundedBitmapDrawableFactory.create(ctx.resources, imageBitmap)
//                    imageDrawable.isCircular = true
//                    imageDrawable.cornerRadius =
//                        Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
//                    viewHolder.userImage.setImageDrawable(imageDrawable)
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

    }


    fun update(comments: ArrayList<CommentResponse>) {
        dataSet.clear()
        dataSet.addAll(comments)

        notifyDataSetChanged()

    }

    fun addToList(comment: CommentResponse) {
        dataSet.add(0, comment)
        notifyItemInserted(0)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}