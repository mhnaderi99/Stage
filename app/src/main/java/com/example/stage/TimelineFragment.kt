package com.example.stage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment

class TimelineFragment: Fragment(R.layout.fragment_timeline) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillPostList()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_timeline, container, false)
        val listView: ListView = view.findViewById(R.id.posts)
        val logoutButton: Button = view.findViewById(R.id.logout)

        logoutButton.setOnClickListener {
            logout()
        }

        listView.adapter = activity?.let { TimelineAdapter(it, fillPostList()) }

        return view

    }


    private fun logout() {

        val intent = Intent(activity,LoginActivity::class.java)
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.isLogin = false
        startActivity(intent)
    }

    private fun fillPostList(): ArrayList<Post> {

        var postList: ArrayList<Post> = ArrayList()

        val p1 = Post("saber", "The Dark Knight", "This is an amazing movie! I would highly recommend it to everyone.", 5, R.drawable.darkknight)
        val p2 = Post("naderi", "Fight Club", "Everything in this movie is so complicated. You have to watch it twice in order to fully understand it.", 4, R.drawable.fightclub)
        val p3 = Post("safdel", "The Shining", "I didn't like this movie at all. Actually, I am disappointed.", 2, R.drawable.shining)

        postList.add(p1)
        postList.add(p2)
        postList.add(p3)

        return postList
    }
}