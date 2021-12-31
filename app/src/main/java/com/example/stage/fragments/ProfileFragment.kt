package com.example.stage.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.stage.utilities.AppPreferences
import com.example.stage.activities.LoginActivity
import com.example.stage.R
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment(R.layout.fragment_profile) {

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
        val tabLayout: TabLayout = view.findViewById(R.id.profileTabLayout)

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

        val usernameLabel: TextView = view.findViewById(R.id.usernameLabel)

        usernameLabel.text = AppPreferences.username

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