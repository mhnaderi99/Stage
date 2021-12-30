package com.example.stage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject
import org.json.JSONTokener

class CompleteProfileActivity: AppCompatActivity() {

    private lateinit var submitButton: Button
    private lateinit var usernameText: EditText
    private var emulator_url: String = "http://10.0.2.2:3030"
    private var url: String = "http://localhost:3030"
    private var global_url: String = "http://109.122.245.55:3030"

    private var active_url = global_url;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        submitButton = findViewById(R.id.signup)
        usernameText = findViewById(R.id.usernameText)

        val emailAddress:String = intent.getStringExtra("email").toString()

        submitButton.setOnClickListener {
            val username = usernameText.text.toString()
            val json = JSONObject()
            json.put("email", emailAddress)
            json.put("username", username)
            Fuel.post("$active_url/signup")
                .jsonBody(json.toString())
                .response { result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        val res = String(bytes)
                        val jsonResult = JSONTokener(res).nextValue() as JSONObject

                        val jjson = JSONObject()
                        jjson.put("email", emailAddress)
                        Fuel.post("$active_url/deleteOTP")
                            .jsonBody(jjson.toString())
                            .response { result ->
                                val (bytes, error) = result
                                if (bytes != null) {
                                    val ress = String(bytes)
                                    println(ress)
                                }
                            }

                        AppPreferences.isLogin = true
                        AppPreferences.username = emailAddress
                        AppPreferences.password = jsonResult.getInt("id").toString()
                        login(jsonResult.getString("email"), jsonResult.getInt("id").toString())
                    }
                }

        }

    }

    fun login(email: String, userid: String) {
        val intent = Intent(this@CompleteProfileActivity,TimelineActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("email", email)
        startActivity(intent)
    }
}