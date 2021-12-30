package com.example.stage

import android.R.id
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import android.text.Editable

import android.text.TextWatcher
import android.R.id.tabs
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var textBox: EditText
    private lateinit var tabLayout: TabLayout

    var movieList: ArrayList<Movie> = ArrayList()
    var userList: ArrayList<User> = ArrayList()
    var movieAdapter: MovieListAdapter? = null
    var userAdapter: UserListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        textBox = findViewById(R.id.search_box)
        tabLayout = findViewById(R.id.tabs)

        textBox.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                search(tabLayout.selectedTabPosition, textBox.text.toString())
                true
            } else {
                false
            }
        }

        textBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                search(tabLayout.selectedTabPosition, textBox.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                search(tab.position, textBox.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val m1 = Movie("The Godfather", 1972, "Francis Ford Coppola")
        val m2 = Movie("Fight Club", 1999, "David Fincher")
        val m3 = Movie("The Dark Knight", 2008, "Christopher Nolan")
        val m4 = Movie("The Shining", 1980, "Stanley Kubrick")

        val u1 = User("naderi")
        val u2 = User("safdel")
        val u3 = User("saber")

        movieList.add(m1)
        movieList.add(m2)
        movieList.add(m3)
        movieList.add(m4)

        userList.add(u1)
        userList.add(u2)
        userList.add(u3)

        listView = findViewById<ListView>(R.id.search_list)

    }

    fun search(tabPosition: Int, searchTerm: String) {

        if (searchTerm.isEmpty()) {
            listView.adapter = null
        }
        else if (tabPosition == 0) {
            val results = movieList.filter { it.name.contains(searchTerm, true) || it.director.contains(searchTerm, true)}
            movieAdapter = MovieListAdapter(this, results.toCollection(ArrayList()))
            listView.adapter = movieAdapter
        } else if (tabPosition == 1){
            val results = userList.filter { it.username.contains(searchTerm, true)}
            userAdapter = UserListAdapter(this, results.toCollection(ArrayList()))
            listView.adapter = userAdapter
        }
    }

}

