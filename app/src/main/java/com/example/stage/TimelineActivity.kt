package com.example.stage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout

class TimelineActivity: AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var logoutButton: Button
    private lateinit var bottomNav: NavigationBarView

    var postList: ArrayList<Post> = ArrayList()
    var postAdapter: TimelineAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppPreferences.isLogin) {
            setContentView(R.layout.activity_timeline)

            //listView = findViewById(R.id.posts)
            //logoutButton = findViewById(R.id.logout)
            bottomNav = findViewById(R.id.bottom_nav)

            val timelineFragment = TimelineFragment()
            val searchFragment = SearchFragment()
            val profileFragment = ProfileFragment()

            setCurrentFragment(timelineFragment)

            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.page_home->setCurrentFragment(timelineFragment)
                    R.id.page_search->setCurrentFragment(searchFragment)
                    R.id.page_profile->setCurrentFragment(profileFragment)

                }
                true
            }
        } else {
            logout()
        }
    }

    private fun logout() {

        val intent = Intent(this@TimelineActivity,LoginActivity::class.java)
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.email = ""
        AppPreferences.isLogin = false
        startActivity(intent)
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

}