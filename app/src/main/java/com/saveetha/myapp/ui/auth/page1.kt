package com.saveetha.myapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.R
import com.saveetha.myapp.network.AppConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class page1 : AppCompatActivity() {

    private val client = OkHttpClient()
    private val TAG = "page1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page1)  // your XML layout

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val firstNameEditText = findViewById<EditText>(R.id.firstnameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastnameEditText)

        val signUpButton = findViewById<Button>(R.id.signupButton)
        val signInClick = findViewById<TextView>(R.id.signinclick)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val firstname = firstNameEditText.text.toString().trim()
            val lastname = lastNameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                signupUser(email, password, firstname, lastname)
            }
        }

        signInClick.setOnClickListener {
            val intent = Intent(this, page2::class.java)
            startActivity(intent)
        }
    }

    private fun signupUser(email: String, password: String, firstname: String, lastname: String) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("firstname", firstname)
            put("lastname", lastname)
        }

        val requestBody = RequestBody.Companion.create(
            "application/json; charset=utf-8".toMediaType(),
            json.toString()
        )

        val request = Request.Builder()
            .url(AppConfig.BASE_URL+"signup.php")  // Your signup.php URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@page1, "Failed to connect to server", Toast.LENGTH_SHORT).show()
                }
                Log.e(TAG, "Signup request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { bodyString ->
                    Log.d(TAG, "Signup response: $bodyString")

                    try {
                        val responseJson = JSONObject(bodyString)
                        val status = responseJson.optString("status", "")
                        val message = responseJson.optString("message", "")
                        val userId = responseJson.optString("user_id", "")

                        runOnUiThread {
                            if (status == "success") {
                                Toast.makeText(this@page1, message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@page1, page2::class.java)
                                intent.putExtra("USER_ID", userId)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@page1, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@page1, "Invalid server response", Toast.LENGTH_SHORT).show()
                        }
                        Log.e(TAG, "JSON parsing error: ${e.message}")
                    }
                } ?: run {
                    runOnUiThread {
                        Toast.makeText(this@page1, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                    Log.e(TAG, "Empty response body")
                }
            }
        })
    }
}
