package com.example.stage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.adapters.MovieCommentAdapter
import com.example.stage.adapters.MovieSearchAdapter
import com.example.stage.adapters.NewMovieCommentAdapter
import com.example.stage.adapters.TimelineAdapter
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.example.stage.utilities.Utilities
import com.example.stage.utilities.Utilities.Companion.hideKeyboard
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import responses.CommentResponse
import responses.MovieResponse
import responses.UserResponse


class MovieFragment(val movieResponse: MovieResponse) : Fragment() {

    private lateinit var commentAdapter: NewMovieCommentAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var length: TextView
    private lateinit var summary: TextView
    private lateinit var genre: TextView
    private lateinit var year: TextView
    private lateinit var title1: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchComments()
        val view: View = inflater.inflate(R.layout.fragment_movie, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        commentAdapter = NewMovieCommentAdapter(ArrayList(), context!!)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = commentAdapter

        title1 = view.findViewById(R.id.title1)
        year = view.findViewById(R.id.year)
        genre = view.findViewById(R.id.genre)
        summary = view.findViewById(R.id.summary)
        length = view.findViewById(R.id.length)

        val send = view.findViewById<Button>(R.id.send_btn)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)
        val textComment = view.findViewById<TextView>(R.id.comment_text)



        val lenStr = padLeftZeros(
            (movieResponse.length.toInt() / 60).toString(),
            2
        ) + ":" + padLeftZeros((movieResponse.length.toInt() % 60).toString(), 2)


        title1.text = movieResponse.title
        year.text = "Year: ${movieResponse.year}"
        genre.text = "Genre: ${movieResponse.category}"
        summary.text = "Summary: ${movieResponse.summary}"
        length.text = "Duration: $lenStr"

        Picasso.get()
            .load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${movieResponse.id}")
            .placeholder(R.color.yellow)
            .error(R.drawable.user_image)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    println("success")
                }

                override fun onError(e: Exception?) {
                    println(e?.message)
                }
            })

        send.setOnClickListener {
            val comment = textComment.text
            if (!comment.toString().isEmpty()) {
                textComment.text = ""
                hideKeyboard()
                val json = JSONObject()
                json.put("movieId", movieResponse.id)
                json.put("commentText", comment)
                Fuel.post("${GlobalVariables.getActiveURL()}/user/sendComment")
                    .authentication()
                    .basic(AppPreferences.email, AppPreferences.password)
                    .jsonBody(json.toString())
                    .response() { request, response, result ->
                        println(result)
                        fetchComments()
                    }
            }
        }


        return view
    }

    private fun fetchComments() {
        var temp: ArrayList<CommentResponse> = ArrayList()
        val movie_id = movieResponse.id
        //GlobalScope.launch {
        Fuel.get("${GlobalVariables.getActiveURL()}/getMovieComments?id=$movie_id")
            .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                val (comment, err) = result
                //Add to ArrayList
                temp.clear()

                comment?.forEach { cmt ->
                    temp.add(cmt)
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    commentAdapter.update(temp)
                })
            }
    }

    private fun fetchMovieInfo() {
        val movieId = movieResponse.id

    }

    private fun padLeftZeros(inputString: String, length: Int): String? {
        if (inputString.length >= length) {
            return inputString
        }
        val sb = StringBuilder()
        while (sb.length < length - inputString.length) {
            sb.append('0')
        }
        sb.append(inputString)
        return sb.toString()
    }
}