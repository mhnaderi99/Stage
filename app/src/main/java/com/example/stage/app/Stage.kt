package com.example.stage.app

import android.app.Application
import com.example.stage.utilities.AppPreferences

class Stage: Application(){
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}