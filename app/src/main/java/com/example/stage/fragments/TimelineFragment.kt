package com.example.stage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import responses.Post
import com.example.stage.R
import com.example.stage.adapters.MovieListAdapter
import com.example.stage.adapters.TimelineAdapter
import com.example.stage.adapters.UserListAdapter
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import responses.CommentResponse
import responses.MovieResponse
import responses.UserResponse

class TimelineFragment: Fragment(R.layout.fragment_timeline) {

    private var activeUrl = GlobalVariables.getActiveURL()
    var comments: ArrayList<CommentResponse> = ArrayList()
    var commentAdapter: TimelineAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillCommentList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_timeline, container, false)
        val listView: ListView = view.findViewById(R.id.posts)

        commentAdapter = activity?.let { TimelineAdapter(it, comments) }
        listView.adapter = commentAdapter

        return view

    }


    private fun fillCommentList() {

        GlobalScope.launch {
            Fuel.get("$activeUrl/getAllComments")
                .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                    val (comment, err) = result
                    //Add to ArrayList
                    comments.clear()

                    comment?.forEach { cmt ->
                        comments.add(cmt)
                    }
                }
        }
    }
}