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
import com.example.stage.R
import com.example.stage.adapters.MovieCommentAdapter
import com.example.stage.adapters.TimelineAdapter
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
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


class MovieFragment(val movieResponse: MovieResponse) : Fragment() {
    var comments: ArrayList<CommentResponse> = ArrayList()
    private lateinit var commentAdapter: MovieCommentAdapter
    private lateinit var length: TextView
    private lateinit var summary: TextView
    private lateinit var genre: TextView
    private lateinit var year: TextView
    private lateinit var title1: TextView
    private var activeUrl = GlobalVariables.getActiveURL()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillCommentList()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_movie, container, false)

        commentAdapter = activity?.let { MovieCommentAdapter(it, comments) }!!
        view.findViewById<ListView>(R.id.video_comments).adapter = commentAdapter

        val lenStr = padLeftZeros(
            (movieResponse.length.toInt() / 60).toString(),
            2
        ) + ":" + padLeftZeros((movieResponse.length.toInt() % 60).toString(), 2)

        title1 = view.findViewById(R.id.title1)
        year = view.findViewById(R.id.year)
        genre = view.findViewById(R.id.genre)
        summary = view.findViewById(R.id.summary)
        length = view.findViewById(R.id.length)
        var send = view.findViewById<Button>(R.id.send_btn)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)
        val textComment = view.findViewById<TextView>(R.id.comment_text)

        title1.text = movieResponse.title
        year.text = "year: " + movieResponse.year.toString()
        genre.text = "genre: "+movieResponse.category
        summary.text = "summary: "+movieResponse.summary
        length.text = "duration: $lenStr"

        Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${movieResponse.id}")
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
            val json = JSONObject()
            json.put("movieId", movieResponse.id)
            json.put("commentText", textComment.text)
            Fuel.post("$activeUrl/user/sendComment")
                .authentication()
                .basic(AppPreferences.email, AppPreferences.password)
                .jsonBody(json.toString())
                .response() { request, response, result ->
                    println(result)
                    fillCommentList()
                }
        }

        fillCommentList()

        return view
    }

    fun padLeftZeros(inputString: String, length: Int): String? {
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
    private fun fillCommentList() {
        val movie_id=movieResponse.id
        GlobalScope.launch {
            Fuel.get("$activeUrl/getMovieComments?id=$movie_id")
                .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                    val (comment, err) = result
                    //Add to ArrayList
                    comments.clear()

                    comment?.forEach { cmt ->
                        comments.add(cmt)
                    }
                    commentAdapter.notifyDataSetChanged()
                }
        }
    }
}