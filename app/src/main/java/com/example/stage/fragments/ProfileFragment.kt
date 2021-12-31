package com.example.stage.fragments

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.stage.utilities.AppPreferences
import com.example.stage.activities.LoginActivity
import com.example.stage.R
import com.example.stage.adapters.MovieListAdapter
import com.example.stage.adapters.TimelineAdapter
import com.example.stage.adapters.UserListAdapter
import com.example.stage.adapters.userCommentAdapter
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import responses.CommentResponse
import responses.MovieResponse
import responses.UserResponse

class ProfileFragment(val selfProfile: Boolean, val userId: Int, val username: String) : Fragment(R.layout.fragment_profile) {

    private var activeUrl = GlobalVariables.getActiveURL()
    var followers: ArrayList<UserResponse> = ArrayList()
    var followerAdapter: UserListAdapter? = null
    var followings: ArrayList<UserResponse> = ArrayList()
    var followingAdapter: UserListAdapter? = null
    var comments: ArrayList<CommentResponse> = ArrayList()
    var commentsAdapter: userCommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followerAdapter = activity?.let { UserListAdapter(it, followers) }
        followingAdapter = activity?.let { UserListAdapter(it, followings) }
        commentsAdapter = activity?.let { userCommentAdapter(it, comments) }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val logoutButton: Button = view.findViewById(R.id.logout)
        val followButton: Button = view.findViewById(R.id.follow)
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val tabLayout: TabLayout = view.findViewById(R.id.profileTabLayout)
        val usernameLabel: TextView = view.findViewById(R.id.usernameLabel)
        val listView: ListView = view.findViewById(R.id.profile_list)

        if (selfProfile) {
            logoutButton.visibility = View.VISIBLE
            followButton.visibility = View.GONE
            Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${AppPreferences.password}").fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage)
            usernameLabel.text = AppPreferences.username

        } else {
            logoutButton.visibility = View.GONE
            followButton.visibility = View.VISIBLE
            Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${userId}").fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage)
            usernameLabel.text = username
        }

        listView.adapter = commentsAdapter

        GlobalScope.launch {
            Fuel.get("$activeUrl/getUserComments?id=${userId}")
                .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                    val (comment, err) = result
                    //Add to ArrayList
                    comments.clear()

                    println(comment)
                    comment?.forEach { cmt ->
                        comments.add(cmt)
                    }
                }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                println(tab.position)
                when (tab.position) {
                    0 -> {
                        listView.adapter = commentsAdapter
                    }
                    1 -> {
                        Fuel.get("$activeUrl/user/getFollowers?id=${userId}")
                            .authentication()
                            .basic(AppPreferences.email, AppPreferences.password)
                            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                                val (follower, err) = result
                                //Add to ArrayList
                                followers.clear()

                                follower?.forEach { flw ->
                                    followers.add(flw)
                                }
                            }
                        listView.adapter = followerAdapter
                    }
                    2 -> {
                        Fuel.get("$activeUrl/user/getFollowings?id=${userId}")
                            .authentication()
                            .basic(AppPreferences.email, AppPreferences.password)
                            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                                val (following, err) = result
                                //Add to ArrayList
                                followings.clear()

                                following?.forEach { flw ->
                                    followings.add(flw)
                                }
                            }
                        listView.adapter = followingAdapter
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        logoutButton.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {

        val intent = Intent(activity, LoginActivity::class.java)
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.email = ""
        AppPreferences.isLogin = false
        startActivity(intent)
    }

}