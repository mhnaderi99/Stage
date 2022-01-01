package com.example.stage.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stage.utilities.AppPreferences
import responses.Post
import com.example.stage.R
import com.example.stage.adapters.TimelineAdapter
import com.example.stage.fragments.ProfileFragment
import com.example.stage.fragments.SearchFragment
import com.example.stage.fragments.TimelineFragment
import com.google.android.material.navigation.NavigationBarView

class TimelineActivity: AppCompatActivity() {

    private var currentFragment: Int = 0
    private lateinit var listView: ListView
    private lateinit var logoutButton: Button
    private lateinit var bottomNav: NavigationBarView

    var postList: ArrayList<Post> = ArrayList()
    var postAdapter: TimelineAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Stage)
        if (AppPreferences.isLogin) {
            setContentView(R.layout.activity_timeline)

            //listView = findViewById(R.id.posts)
            //logoutButton = findViewById(R.id.logout)
            bottomNav = findViewById(R.id.bottom_nav)

            val timelineFragment = TimelineFragment()
            val searchFragment = SearchFragment()
            val profileFragment = ProfileFragment(true, AppPreferences.password.toInt(), AppPreferences.username, false)

            setCurrentFragment(timelineFragment, 0)

            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.page_home ->{
                        setCurrentFragment(timelineFragment, 0-currentFragment)
                        currentFragment = 0
                    }
                    R.id.page_search -> {
                        setCurrentFragment(searchFragment, 1-currentFragment)
                        currentFragment = 1
                    }
                    R.id.page_profile -> {
                        setCurrentFragment(profileFragment, 2-currentFragment)
                        currentFragment = 2
                    }

                }
                true
            }
        } else {
            logout()
        }
    }

    private fun logout() {

        val intent = Intent(this@TimelineActivity, LoginActivity::class.java)
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.email = ""
        AppPreferences.isLogin = false
        startActivity(intent)
    }

    private fun setCurrentFragment(fragment: Fragment, direction: Int)=
        when(direction) {
            -2 -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.flFragment,fragment)
                    addToBackStack("xyz")
                    commit()
                }
            }
            -1 -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.flFragment,fragment)
                    addToBackStack("xyz")
                    commit()
                }

            }
            0 -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment,fragment)
                    addToBackStack("xyz")
                    commit()
                }
            }
            1 -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.flFragment,fragment)
                    addToBackStack("xyz")
                    commit()
                }
            }
            2 -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.flFragment,fragment)
                    addToBackStack("xyz")
                    commit()
                }
            }
            else -> {
                null
            }
        }



}