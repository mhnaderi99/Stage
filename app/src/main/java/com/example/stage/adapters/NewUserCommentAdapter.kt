package com.example.stage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.json.JSONTokener
import responses.MovieResponse
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.runBlocking
import responses.UserResponse
import android.content.Context

import androidx.fragment.app.FragmentActivity
import com.example.stage.activities.TimelineActivity
import com.example.stage.fragments.ProfileFragment
import androidx.appcompat.app.AppCompatActivity
import com.example.stage.R
import responses.CommentResponse


class NewUserCommentAdapter(private var dataSet: ArrayList<CommentResponse>) :
    RecyclerView.Adapter<NewUserCommentAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val username: TextView = view.findViewById(R.id.username)
        val movieName: TextView = view.findViewById(R.id.movieName)
        val comment: TextView = view.findViewById(R.id.comment)
        val movieImage: ImageView = view.findViewById(R.id.avatar)


        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_comment_row, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.username.text = dataSet[position].username
        viewHolder.comment.text = dataSet[position].comment_text
        viewHolder.movieName.text = dataSet[position].title

        Picasso.get()
            .load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${dataSet[position].id}")
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

    }


    fun update(comments: ArrayList<CommentResponse>) {
        dataSet.clear()
        dataSet.addAll(comments)

        notifyDataSetChanged()

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}