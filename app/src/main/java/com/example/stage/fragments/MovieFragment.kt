package com.example.stage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.adapters.MovieCommentAdapter
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.example.stage.utilities.Utilities.Companion.hideKeyboard
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import com.example.stage.responses.CommentResponse
import com.example.stage.responses.MovieResponse
import com.example.stage.responses.UserResponse


class MovieFragment(val movieId: Int) : Fragment() {

    //private lateinit var movieResponse: MovieResponse
    private lateinit var commentAdapter: MovieCommentAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var length: TextView
    private lateinit var summary: TextView
    private lateinit var genre: TextView
    private lateinit var year: TextView
    private lateinit var title1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchMovieInfo()
        fetchComments()

        val view: View = inflater.inflate(R.layout.fragment_movie, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        commentAdapter = MovieCommentAdapter(ArrayList(), context!!)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = commentAdapter

        title1 = view.findViewById(R.id.title1)
        year = view.findViewById(R.id.year)
        genre = view.findViewById(R.id.genre)
        summary = view.findViewById(R.id.summary)
        length = view.findViewById(R.id.length)

        val send = view.findViewById<ImageButton>(R.id.send_btn)
        val imageView = view.findViewById<ImageView>(R.id.imageView2)
        val textComment = view.findViewById<TextView>(R.id.comment_text)


        Picasso.get()
            .load("${GlobalVariables.getActiveURL()}/downloadMovieImage?id=${movieId}")
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
                json.put("movieId", movieId)
                json.put("commentText", comment)
                Fuel.post("${GlobalVariables.getActiveURL()}/user/sendComment")
                    .authentication()
                    .basic(AppPreferences.email, AppPreferences.password)
                    .jsonBody(json.toString())
                    .response() { request, response, result ->
                        println(result)
                        activity?.runOnUiThread(java.lang.Runnable {
                            //fetchComments()
                            commentAdapter.addToList(CommentResponse(movieId, "", AppPreferences.username, comment.toString(), AppPreferences.password.toInt()))
                            linearLayoutManager.scrollToPositionWithOffset(0, 0);
                        })
                    }
            }
        }


        return view
    }

    private fun fetchComments() {
        var temp: ArrayList<CommentResponse> = ArrayList()
        //GlobalScope.launch {
        Fuel.get("${GlobalVariables.getActiveURL()}/getMovieComments?id=$movieId")
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
        Fuel.get("${GlobalVariables.getActiveURL()}/getMovieById?id=${movieId}")
            .responseObject(MovieResponse.Deserializer()) { request, response, result ->
                val (movie, err) = result
                //Add to ArrayList

                val movieResponse = MovieResponse(movie!![0].id, movie[0].title, movie!![0].director, movie!![0].year, movie!![0].summary, movie!![0].length, movie!![0].category)

                val lenStr = padLeftZeros(
                    (movieResponse.length.toInt() / 60).toString(),
                    2
                ) + ":" + padLeftZeros((movieResponse.length.toInt() % 60).toString(), 2)

                activity?.runOnUiThread(java.lang.Runnable {
                    title1.text = movieResponse.title
                    year.text = "Year: ${movieResponse.year}"
                    genre.text = "Genre: ${movieResponse.category}"
                    summary.text = "Summary: ${movieResponse.summary}"
                    length.text = "Duration: $lenStr"
                })
            }

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