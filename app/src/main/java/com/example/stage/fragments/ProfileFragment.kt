package com.example.stage.fragments

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stage.utilities.AppPreferences
import com.example.stage.activities.LoginActivity
import com.example.stage.R
import com.example.stage.adapters.*
import com.example.stage.utilities.GlobalVariables
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.example.stage.responses.CommentResponse
import com.example.stage.responses.UserResponse
import com.squareup.picasso.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment(
    val selfProfile: Boolean,
    val userId: Int,
    val username: String,
    var followed: Boolean
) : Fragment(R.layout.fragment_profile) {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var commentsAdapter: UserCommentAdapter
    lateinit var followersAdapter: UserSearchAdapter
    lateinit var followingsAdapter: UserSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        commentsAdapter = UserCommentAdapter(ArrayList())
        followersAdapter = UserSearchAdapter(ArrayList())
        followingsAdapter = UserSearchAdapter(ArrayList())

        linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        val logoutButton: Button = view.findViewById(R.id.logout)
        val followButton: Button = view.findViewById(R.id.follow)
        val unfollowButton: Button = view.findViewById(R.id.unfollow)
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val tabLayout: TabLayout = view.findViewById(R.id.profileTabLayout)
        val usernameLabel: TextView = view.findViewById(R.id.usernameLabel)

        when (tabLayout.selectedTabPosition) {
            0 -> {
                fetchUserComments(userId)
                recyclerView.adapter = commentsAdapter
            }
            1 -> {
                fetchFollowers(userId)
                recyclerView.adapter = followersAdapter
            }
            2 -> {
                fetchFollowings(userId)
                recyclerView.adapter = followingsAdapter
            }
        }

        if (selfProfile) {
            logoutButton.visibility = View.VISIBLE
            followButton.visibility = View.GONE
            unfollowButton.visibility = View.GONE
            Picasso.get()
                .load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${AppPreferences.password}")
                .fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage)
            usernameLabel.text = AppPreferences.username

        } else {
            logoutButton.visibility = View.GONE
            if (followed) {
                followButton.visibility = View.GONE
                unfollowButton.visibility = View.VISIBLE
            } else {
                unfollowButton.visibility = View.GONE
                followButton.visibility = View.VISIBLE
            }


            Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${userId}")
                .fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage, object : Callback {
                    override fun onSuccess() {
                        println("success")
                    }

                    override fun onError(e: Exception?) {
                        println(e?.message)
                    }
                })
            usernameLabel.text = username
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                println(tab.position)
                when (tab.position) {
                    0 -> {
                        fetchUserComments(userId)
                        recyclerView.adapter = commentsAdapter
                    }
                    1 -> {
                        fetchFollowers(userId)
                        recyclerView.adapter = followersAdapter
                    }
                    2 -> {
                        fetchFollowings(userId)
                        recyclerView.adapter = followingsAdapter
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        logoutButton.setOnClickListener {
            logout()
        }

        followButton.setOnClickListener {
            Fuel.post("${GlobalVariables.getActiveURL()}/user/follow?id=${userId}")
                .authentication()
                .basic(AppPreferences.email, AppPreferences.password)
                .response() { request, response, result ->
                    println(result)

                    activity?.runOnUiThread(java.lang.Runnable {
                        followButton.visibility = View.GONE
                        unfollowButton.visibility = View.VISIBLE

                        if (tabLayout.selectedTabPosition == 1) {
                            //update followers list

                            GlobalScope.launch {
                                followersAdapter.addToList(
                                    UserResponse(
                                        AppPreferences.username,
                                        AppPreferences.password.toInt()
                                    )
                                )
                            }
                            //fetchFollowers(userId)
                        }

                    })
                }
        }

        unfollowButton.setOnClickListener {
            Fuel.post("${GlobalVariables.getActiveURL()}/user/unfollow?id=${userId}")
                .authentication()
                .basic(AppPreferences.email, AppPreferences.password)
                .response() { request, response, result ->
                    println(result)

                    activity?.runOnUiThread(java.lang.Runnable {
                        unfollowButton.visibility = View.GONE
                        followButton.visibility = View.VISIBLE

                        if (tabLayout.selectedTabPosition == 1) {

                            val deletedUserPosition = followersAdapter.getDataSet().indexOf(
                                followersAdapter.getDataSet().filter { s -> s.id == AppPreferences.password.toInt() }.get(0)
                            )
                            followersAdapter.removeFromList(deletedUserPosition)
                        }
                    })
                }
        }

        return view
    }

    private fun fetchUserComments(userId: Int) {

        var temp: ArrayList<CommentResponse> = ArrayList()
        Fuel.get("${GlobalVariables.getActiveURL()}/getUserComments?id=${userId}")
            .responseObject(CommentResponse.Deserializer()) { request, response, result ->
                val (comment, err) = result
                //Add to ArrayList
                temp.clear()

                comment?.forEach { cmt ->
                    temp.add(cmt)
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    commentsAdapter.update(temp)
                })
            }
    }


    private fun fetchFollowers(userId: Int) {

        var temp: ArrayList<UserResponse> = ArrayList()
        Fuel.get("${GlobalVariables.getActiveURL()}/user/getFollowers?id=${userId}")
            .authentication()
            .basic(AppPreferences.email, AppPreferences.password)
            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                val (follower, err) = result
                //Add to ArrayList
                temp.clear()

                follower?.forEach { flw ->
                    temp.add(flw)
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    followersAdapter.update(temp)
                })
            }
    }

    private fun fetchFollowings(userId: Int) {

        var temp: ArrayList<UserResponse> = ArrayList()
        Fuel.get("${GlobalVariables.getActiveURL()}/user/getFollowings?id=${userId}")
            .authentication()
            .basic(AppPreferences.email, AppPreferences.password)
            .responseObject(UserResponse.Deserializer()) { request, response, result ->
                val (following, err) = result
                //Add to ArrayList
                temp.clear()

                following?.forEach { flw ->
                    temp.add(flw)
                }

                activity?.runOnUiThread(java.lang.Runnable {
                    followingsAdapter.update(temp)
                })
            }
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