package com.example.stage

import android.content.Intent
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result;
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.json.JSONTokener
import responses.MovieResponse
import responses.UserResponse

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var activeUrl = GlobalVariables.getActiveURL()
    var movies: ArrayList<MovieResponse> = ArrayList()
    var movieAdapter: MovieListAdapter? = null
    var users: ArrayList<UserResponse> = ArrayList()
    var userAdapter: UserListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieAdapter = activity?.let { MovieListAdapter(it, movies) }
        userAdapter = activity?.let { UserListAdapter(it, users) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        val searchBox: EditText = view.findViewById(R.id.search_box)
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)
        val listView: ListView = view.findViewById(R.id.search_list)

        if (tabLayout.selectedTabPosition == 0) {
            listView.adapter = movieAdapter
        }
        else {
            listView.adapter = userAdapter
        }


        searchBox.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                if (tabLayout.selectedTabPosition == 0) {
                    //movies search
                    movieAdapter = activity?.let { MovieListAdapter(it, movies) }
                    listView.adapter = movieAdapter
                } else {
                    //users search
                    userAdapter = activity?.let { UserListAdapter(it, users) }
                    listView.adapter = userAdapter
                }

                true
            } else {
                false
            }
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val username = AppPreferences.email
                val password = AppPreferences.password
                val json = JSONObject()

                GlobalScope.launch {
                    json.put("searchTerm", searchBox.text.toString())
                    println(json)
                    if (tabLayout.selectedTabPosition == 0) {
                        Fuel.post("$activeUrl/user/searchMovies")
                            .authentication()
                            .basic(username, password)
                            .jsonBody(json.toString())
                            .also { print(it) }
                            .responseObject(MovieResponse.Deserializer()) { request, response, result ->
                                val (movie, err) = result
                                //Add to ArrayList
                                movies.clear()

                                movie?.forEach { mv ->
                                    movies.add(mv)
                                }
                            }
                    }
                    else {
                        Fuel.post("$activeUrl/user/searchUsers")
                            .authentication()
                            .basic(username, password)
                            .jsonBody(json.toString())
                            .also { print(it) }
                            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                                val (user, err) = result
                                //Add to ArrayList
                                users.clear()

                                user?.forEach { usr ->
                                    users.add(usr)
                                }

                            }
                    }


                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    //movies tab selected
                }
                else {
                    //users tab selected
                }
                //search(tab.position, textBox.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        return view

    }

}