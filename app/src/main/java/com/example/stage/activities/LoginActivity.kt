package com.example.stage.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import com.example.stage.utilities.AppPreferences
import com.example.stage.utilities.GlobalVariables
import com.example.stage.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.json.JSONTokener


class LoginActivity : AppCompatActivity() {

    private lateinit var submitEmail: Button
    private lateinit var loginButton: Button
    private lateinit var emailText: EditText
    private lateinit var OTPText: EditText
    private lateinit var emailAddress: String

    private var activeUrl = GlobalVariables.getActiveURL();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        setTheme(R.style.Theme_Stage)

        if (AppPreferences.isLogin) {
            println(AppPreferences.username)
            println(AppPreferences.email)
            println(AppPreferences.password)
            login(AppPreferences.email, AppPreferences.password)
        } else {
            setContentView(R.layout.activity_login)
            submitEmail = findViewById(R.id.submitEmail)
            loginButton = findViewById(R.id.login)
            emailText = findViewById(R.id.emailAddress)
            OTPText = findViewById(R.id.otpCode)

            loginButton.setOnClickListener {
                Fuel.get("$activeUrl/validateOTP",
                    parameters = listOf("email" to emailAddress, "code" to OTPText.text.toString() ))
                    .response { request, response, result ->
                        val (bytes, error) = result
                        if (bytes != null) {
                            val res = String(bytes)
                            val jsonResult = JSONTokener(res).nextValue() as JSONObject
                            println(jsonResult)
                            if (jsonResult.getString("message") == "correct") {
                                //correct code -> login
                                val json = JSONObject()
                                json.put("email", emailAddress)

                                Fuel.get("$activeUrl/check_email",
                                    parameters = listOf("email" to emailAddress ))
                                    .response { request, response, result ->
                                        val (bytes, error) = result
                                        if (bytes != null) {
                                            val res = String(bytes)
                                            val jsonResult = JSONTokener(res).nextValue() as JSONObject
                                            if (jsonResult.getBoolean("found")) {
                                                //has already registered
                                                val userId = jsonResult.getInt("id")
                                                val userName = jsonResult.getString("username")
                                                println(jsonResult)

                                                val jjson = JSONObject()
                                                jjson.put("email", emailAddress)
                                                Fuel.post("$activeUrl/deleteOTP")
                                                    .jsonBody(jjson.toString())
                                                    .response { result ->
                                                        val (bytes, error) = result
                                                        if (bytes != null) {
                                                            val ress = String(bytes)
                                                            println(ress)
                                                        }
                                                    }

                                                AppPreferences.isLogin = true
                                                AppPreferences.email = emailAddress
                                                AppPreferences.username = userName
                                                AppPreferences.password = userId.toString()
                                                login(AppPreferences.email, AppPreferences.password)
                                            }
                                            else {
                                                //hasn't registered
                                                val intent = Intent(this@LoginActivity,
                                                    CompleteProfileActivity::class.java)
                                                intent.putExtra("email", emailAddress)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                            }
                            else {
                                //incorrect code
                                runOnUiThread(java.lang.Runnable {
                                    toast("Incorrect Code")
                                })
                            }
                        }
                    }
            }

            submitEmail.setOnClickListener {
                emailAddress = emailText.text.toString()
                if(isValidEmail(emailAddress)) {
                    OTPText.visibility = View.VISIBLE
                    submitEmail.visibility = View.GONE
                    loginButton.visibility = View.VISIBLE
                    emailText.isEnabled = false

                    val json = JSONObject()
                    json.put("email", emailAddress)
                    Fuel.post("$activeUrl/generateOTP")
                        .jsonBody(json.toString())
                        .also { print(it) }
                        .response { result ->
                            val (bytes, error) = result
                            if (bytes != null) {
                                val res = String(bytes)
                                val jsonResult = JSONTokener(res).nextValue() as JSONObject
                                println(jsonResult)
                            }
                        }
                }
                else {
                    //email is not valid

                    toast("Invalid Email Address")
                }

            }
        }


    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun login(email: String, userid: String) {
        val intent = Intent(this@LoginActivity, TimelineActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("email", email)
        startActivity(intent)
    }

}