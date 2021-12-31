package com.example.stage.fragments

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.stage.utilities.AppPreferences
import com.example.stage.activities.LoginActivity
import com.example.stage.R
import com.example.stage.utilities.GlobalVariables
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class ProfileFragment(val selfProfile: Boolean, val userId: Int, val username: String) : Fragment(R.layout.fragment_profile) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val logoutButton: Button = view.findViewById(R.id.logout)
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val tabLayout: TabLayout = view.findViewById(R.id.profileTabLayout)
        val usernameLabel: TextView = view.findViewById(R.id.usernameLabel)

        if (selfProfile) {
            Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${AppPreferences.password}").fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage)
            usernameLabel.text = AppPreferences.username

        } else {
            logoutButton.visibility = View.GONE
            Picasso.get().load("${GlobalVariables.getActiveURL()}/downloadUserImage?id=${userId}").fit().centerCrop()
                .placeholder(R.color.yellow)
                .error(R.drawable.user_image)
                .into(userImage)
            usernameLabel.text = username
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                println(tab.position)
                when (tab.position) {
                    0 -> {
                    }
                    1 -> {
                    }
                    2 -> {

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