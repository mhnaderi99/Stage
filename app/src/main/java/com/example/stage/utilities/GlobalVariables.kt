package com.example.stage.utilities

class GlobalVariables {

    companion object {
        val GLOBAL_URL = "http://109.122.245.55:3030"
        val LOCAL_URL = "http://localhost:3030"
        val EMULATOR_URL = "http://10.0.2.2:3030"

        fun getActiveURL(): String {return GLOBAL_URL
        }
    }


}