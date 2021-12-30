package com.example.stage

import android.app.Application

class Stage: Application(){
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}