package com.example.stage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class TimelineActivity: AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var logoutButton: Button

    var postList: ArrayList<Post> = ArrayList()
    var postAdapter: TimelineAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppPreferences.isLogin) {
            setContentView(R.layout.activity_timeline)

            listView = findViewById(R.id.posts)
            logoutButton = findViewById(R.id.logout)

            logoutButton.setOnClickListener {
                logout()
            }

            val p1 = Post("saber", "The Dark Knight", "This is an amazing movie! I would highly recommend it to everyone.", 5, R.drawable.darkknight)
            val p2 = Post("naderi", "Fight Club", "Everything in this movie is so complicated. You have to watch it twice in order to fully understand it.", 4, R.drawable.fightclub)
            val p3 = Post("safdel", "The Shining", "I didn't like this movie at all. Actually, I am disappointed.", 2, R.drawable.shining)


            postList.add(p1)
            postList.add(p2)
            postList.add(p3)
            listView.adapter = TimelineAdapter(this, postList)

        } else {
            logout()
        }
    }

    private fun logout() {

        val intent = Intent(this@TimelineActivity,LoginActivity::class.java)
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.isLogin = false
        startActivity(intent)
    }

}