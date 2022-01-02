package com.example.stage.fragments

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.stage.*
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import com.example.stage.responses.MovieResponse
import com.example.stage.responses.UserResponse
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.adapters.*
import androidx.recyclerview.widget.DividerItemDecoration

import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import com.example.stage.utilities.Utilities.Companion.hideKeyboard


class SearchFragment : Fragment(R.layout.fragment_search) {


    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var movieSearchAdapter: MovieSearchAdapter
    lateinit var userSearchAdapter: UserSearchAdapter
    lateinit var layout: ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.search_movie_list)
        movieSearchAdapter = MovieSearchAdapter(ArrayList())
        userSearchAdapter = UserSearchAdapter(ArrayList())
        layout = view.findViewById(R.id.searchLayout)
        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val searchBox: EditText = view.findViewById(R.id.search_box)
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)

        if (tabLayout.selectedTabPosition == 0) {
            recyclerView.adapter = movieSearchAdapter
            searchBox.hint = "Search movies"
        } else {
            recyclerView.adapter = userSearchAdapter
            searchBox.hint = "Search users"
        }

        backButton.setOnClickListener {
            hideKeyboard()
        }

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            //r will be populated with the coordinates of your view that area still visible.
            view.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = view.rootView.height - (r.bottom - r.top)
            if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                // keyboard is opened
                println("Keyboard")
                backButton.visibility = View.VISIBLE

            } else {
                // keyboard is closed
                println("No")
                backButton.visibility = View.GONE
            }
        }

        searchBox.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                fetchUsers(searchBox.text.toString())
                if (! searchBox.text.toString().isEmpty()) {
                    hideKeyboard()
                }

                true
            } else {
                false
            }
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val searchTerm = searchBox.text.toString()
                if (tabLayout.selectedTabPosition == 0) {
                    fetchMovies(searchTerm)
                } else {
                    fetchUsers(searchTerm)

                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    //movies tab selected
                    //searchBox.setText("");
                    recyclerView.adapter = movieSearchAdapter
                    searchBox.hint = "Search movies"
                    fetchMovies(searchBox.text.toString())
                } else {
                    //users tab selected
                    //searchBox.setText("");
                    recyclerView.adapter = userSearchAdapter
                    searchBox.hint = "Search users"
                    fetchUsers(searchBox.text.toString())
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return view

    }


    private fun fetchMovies(searchTerm: String) {
        if (searchTerm.isEmpty()) {
            movieSearchAdapter.update(ArrayList())
            userSearchAdapter.update(ArrayList())
            return
        }
        var temp: ArrayList<MovieResponse> = ArrayList()
        val json = JSONObject()
        json.put("searchTerm", searchTerm)
        Fuel.post("${GlobalVariables.getActiveURL()}/user/searchMovies")
            .authentication()
            .basic(AppPreferences.email, AppPreferences.password)
            .jsonBody(json.toString())
            .also { print(it) }
            .responseObject(MovieResponse.Deserializer()) { request, response, result ->
                val (movie, err) = result
                //Add to ArrayList
                temp.clear()

                movie?.forEach { mv ->
                    temp.add(mv)
                }

                println(temp)

                activity?.runOnUiThread(java.lang.Runnable {
                    movieSearchAdapter.update(temp)
                })

            }
    }


    private fun fetchUsers(searchTerm: String) {
        if (searchTerm.isEmpty()) {
            movieSearchAdapter.update(ArrayList())
            userSearchAdapter.update(ArrayList())
            return
        }
        var temp: ArrayList<UserResponse> = ArrayList()
        val json = JSONObject()
        json.put("searchTerm", searchTerm)
        Fuel.post("${GlobalVariables.getActiveURL()}/user/searchUsers")
            .authentication()
            .basic(AppPreferences.email, AppPreferences.password)
            .jsonBody(json.toString())
            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                val (user, err) = result
                //Add to ArrayList
                temp.clear()

                user?.forEach { usr ->
                    temp.add(usr)
                }

                println(temp)

                activity?.runOnUiThread(java.lang.Runnable {
                    userSearchAdapter.update(temp)
                })

            }
    }


}