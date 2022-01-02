package com.example.stage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.activities.TimelineActivity
import com.example.stage.fragments.MovieFragment
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.example.stage.responses.MovieResponse

class MovieSearchAdapter(private var dataSet: ArrayList<MovieResponse>) :
    RecyclerView.Adapter<MovieSearchAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val director: TextView = view.findViewById(R.id.director)
        val year: TextView = view.findViewById(R.id.movieYear)
        val movieTitle: TextView = view.findViewById(R.id.movieName)
        val movieImage: ImageView = view.findViewById(R.id.avatar)


        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.movie_row, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.movieTitle.text = dataSet[position].title
        viewHolder.director.text = dataSet[position].director
        viewHolder.year.text = dataSet[position].year.toString()

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

        with(viewHolder.itemView) {
            setOnClickListener {
                val manager = (context as TimelineActivity).supportFragmentManager
                val movieFragment = MovieFragment(dataSet[position])
                manager.beginTransaction().replace(R.id.flFragment, movieFragment)
                    .addToBackStack("xyz")
                    .commit()
            }
        }
    }

    fun update(movies: ArrayList<MovieResponse>){
        dataSet.clear()
        dataSet.addAll(movies)

        notifyDataSetChanged()

    }




    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}