package com.example.stage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import responses.MovieResponse

class MovieListAdapter(private val context: Context, private val arrayList: java.util.ArrayList<MovieResponse>): BaseAdapter() {

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

        movieTitle = convertView.findViewById(R.id.movieName)
        movieYear = convertView.findViewById(R.id.movieYear)
        director = convertView.findViewById(R.id.director)
        movieTitle.text = " " + arrayList[position].title
        movieYear.text = " " + arrayList[position].year
        director.text = arrayList[position].director
        return convertView
    }
}