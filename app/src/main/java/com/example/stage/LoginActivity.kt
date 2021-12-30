package com.example.stage

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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.URL


class LoginActivity : AppCompatActivity() {

    private lateinit var submitEmail: Button
    private lateinit var loginButton: Button
    private lateinit var emailText: EditText
    private lateinit var OTPText: EditText
    private lateinit var emailAddress: String
    private var emulator_url: String = "http://10.0.2.2:3030"
    private var url: String = "http://localhost:3030"
    private var global_url: String = "https://sour-sheep-76.loca.lt"

    private var active_url = global_url;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppPreferences.isLogin) {
            println(AppPreferences.username)
            println(AppPreferences.password)
            login(AppPreferences.username, AppPreferences.password)
        } else {
            setContentView(R.layout.activity_login)

            submitEmail = findViewById(R.id.submitEmail)
            loginButton = findViewById(R.id.login)
            emailText = findViewById(R.id.emailAddress)
            OTPText = findViewById(R.id.otpCode)

            loginButton.setOnClickListener {
                Fuel.get("$active_url/validateOTP",
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

                                Fuel.get("$active_url/check_email",
                                    parameters = listOf("email" to emailAddress ))
                                    .response { request, response, result ->
                                        val (bytes, error) = result
                                        if (bytes != null) {
                                            val res = String(bytes)
                                            val jsonResult = JSONTokener(res).nextValue() as JSONObject
                                            if (jsonResult.getBoolean("found")) {
                                                //has already registered
                                                val userId = jsonResult.getInt("id")
                                                println(userId)

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
                                                AppPreferences.password = userId.toString()
                                                login(AppPreferences.username, AppPreferences.password)
                                            }
                                            else {
                                                //hasn't registered
                                                val intent = Intent(this@LoginActivity,CompleteProfileActivity::class.java)
                                                intent.putExtra("email", emailAddress)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                            }
                            else {
                                //incorrect code
                                toast("Incorrect Code")
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
                    Fuel.post("$active_url/generateOTP")
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
        val intent = Intent(this@LoginActivity,TimelineActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("email", email)
        startActivity(intent)
    }

}