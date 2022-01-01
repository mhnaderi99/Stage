package com.example.stage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.stage.R
import com.example.stage.fragments.MovieFragment
import com.example.stage.utilities.GlobalVariables
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import responses.MovieResponse
import java.util.ArrayList

class MovieListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<MovieResponse>,
    private val requireFragmentManager: FragmentManager
): BaseAdapter() {

    private lateinit var avatar: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieYear: TextView
    private lateinit var director: TextView

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
        convertView = LayoutInflater.from(context).inflate(R.layout.movie_row, parent, false)

        avatar = convertView.findViewById(R.id.avatar)
        movieTitle = convertView.findViewById(R.id.movieName)
        movieYear = convertView.findViewById(R.id.movieYear)
        director = convertView.findViewById(R.id.director)
        movieTitle.text = " " + arrayList[position].title
        movieYear.text = " " + arrayList[position].year
        director.text = arrayList[position].director

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

        convertView.setOnClickListener {
            val ft = requireFragmentManager.beginTransaction()
            ft.replace(R.id.flFragment, MovieFragment(arrayList[position]), "NewFragmentTag")
            ft.addToBackStack("xyz");
            ft.commit()
        }
        return convertView
    }
}