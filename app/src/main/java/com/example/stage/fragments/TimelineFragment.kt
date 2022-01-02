package com.example.stage.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.R
import com.example.stage.adapters.TimelinePostAdapter
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.example.stage.responses.CommentResponse
import android.graphics.drawable.GradientDrawable







class TimelineFragment : Fragment(R.layout.fragment_timeline) {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var commentAdapter: TimelinePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fetchComments()
        val view: View = inflater.inflate(R.layout.fragment_timeline, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

//        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        val drawable = GradientDrawable(
//            GradientDrawable.Orientation.BOTTOM_TOP,
//            intArrayOf(-0x80809, -0x80809)
//        )
//        drawable.setSize(1, 1)
//        itemDecoration.setDrawable(drawable)
//        recyclerView.addItemDecoration(itemDecoration)

        commentAdapter = TimelinePostAdapter(ArrayList(), context!!)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = commentAdapter
        return view

    }


    private fun fetchComments(){

        var temp: ArrayList<CommentResponse> = ArrayList()
        Fuel.get("${GlobalVariables.getActiveURL()}/user/getTimelineComments")
            .authentication()
            .basic(AppPreferences.email, AppPreferences.password)
            .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                val (comment, err) = result
                comment?.forEach { cmt ->
                    temp.add(cmt)
                }
                println(temp)

                activity?.runOnUiThread(java.lang.Runnable {
                    commentAdapter.update(temp)
                })
            }
    }
}