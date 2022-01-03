package com.example.stage.adapters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.activities.TimelineActivity
import com.example.stage.fragments.MovieFragment
import com.example.stage.fragments.ProfileFragment
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.example.stage.responses.CommentResponse
import com.example.stage.responses.MovieResponse

class TimelinePostAdapter(private var dataSet: ArrayList<CommentResponse>, val ctx: Context) :
    RecyclerView.Adapter<TimelinePostAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val userLayout: LinearLayout = view.findViewById(R.id.userLayout)
        val commentLayout: LinearLayout = view.findViewById(R.id.commentLayout)
        val username: TextView = view.findViewById(R.id.username)
        val movieTitle: TextView = view.findViewById(R.id.movieName)
        val comment: TextView = view.findViewById(R.id.comment)
        val userImage: ImageView = view.findViewById(R.id.user_avatar)
        val movieImage: ImageView = view.findViewById(R.id.avatar)


        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.timeline_row, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.username.text = dataSet[position].username
        viewHolder.movieTitle.text = dataSet[position].title
        viewHolder.comment.text = dataSet[position].comment_text

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${dataSet[position].id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(viewHolder.movieImage, object : Callback {
                override fun onSuccess() {
                    println("success")
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${dataSet[position].user_id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(viewHolder.userImage, object : Callback {
                override fun onSuccess() {
                    println("success")
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

        viewHolder.commentLayout.setOnClickListener {
            val manager = (ctx as TimelineActivity).supportFragmentManager
            val movieFragment = MovieFragment(dataSet[position].id)
            manager.beginTransaction().replace(R.id.flFragment, movieFragment)
                .addToBackStack("xyz")
                .commit()
        }

        viewHolder.userLayout.setOnClickListener {
            val manager = (ctx as TimelineActivity).supportFragmentManager
            val profileFragment = ProfileFragment(dataSet[position].user_id)
            manager.beginTransaction().replace(R.id.flFragment, profileFragment)
                .addToBackStack("xyz")
                .commit()
        }

//        with(viewHolder.itemView) {
//            setOnClickListener {
//                val manager = (context as TimelineActivity).supportFragmentManager
//                val movieFragment = MovieFragment(dataSet[position].id)
//                manager.beginTransaction().replace(R.id.flFragment, movieFragment)
//                    .addToBackStack("xyz")
//                    .commit()
//            }
//        }
    }

    fun update(comments: ArrayList<CommentResponse>){
        dataSet.clear()
        dataSet.addAll(comments)

        notifyDataSetChanged()

    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}