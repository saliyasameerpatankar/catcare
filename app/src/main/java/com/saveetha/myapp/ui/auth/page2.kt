package com.saveetha.myapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.myapp.R
import com.saveetha.myapp.network.AppConfig
import com.saveetha.myapp.page4
import com.saveetha.myapp.Page6
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class page2 : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var signInButton: Button
    private lateinit var signupLink: TextView

    private var isPasswordVisible = false
    private val client = OkHttpClient()
    private val LOGIN_URL = AppConfig.BASE_URL + "login.php"
    private val USER_HOME_URL = AppConfig.BASE_URL + "userhomepage.php"  // For profile check

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page2)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordToggle = findViewById(R.id.passwordToggle)
        signInButton = findViewById(R.id.signInButton)
        signupLink = findViewById(R.id.signupLink)

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.baseline_visibility_24) // visible icon
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.baseline_visibility_24) // hidden icon
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, page1::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder().url(LOGIN_URL).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@page2, "Server error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("page2", "Login failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    try {
                        val jsonResponse = JSONObject(responseData)
                        if (jsonResponse.getString("status") == "success") {
                            val user = jsonResponse.getJSONObject("user")
                            val firstname = user.getString("firstname")
                            val userId = user.getInt("userid")

                            Toast.makeText(this@page2, "Welcome, $firstname!", Toast.LENGTH_SHORT).show()

                            // Check user profiles and redirect accordingly
                            checkUserProfilesAndRedirect(userId)
                        } else {
                            val message = jsonResponse.optString("message", "Login failed")
                            Toast.makeText(this@page2, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@page2, "Invalid server response", Toast.LENGTH_SHORT).show()
                        Log.e("page2", "JSON error: ${e.message}")
                    }
                }
            }
        })
    }

    private fun checkUserProfilesAndRedirect(userId: Int) {
        val json = JSONObject().put("userid", userId)
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder().url(USER_HOME_URL).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@page2, "Error fetching profiles: ${e.message}", Toast.LENGTH_SHORT).show()
                    // On failure, redirect to page4 as fallback
                    val intent = Intent(this@page2, page4::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val catsArray = jsonResponse.optJSONArray("cats") ?: JSONArray()

                        val intent = if (catsArray.length() > 0) {
                            // Has cat profiles → page6
                            Intent(this@page2, Page6::class.java)
                        } else {
                            // No cat profiles → page4
                            Intent(this@page2, page4::class.java)
                        }
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@page2, "Invalid server response", Toast.LENGTH_SHORT).show()
                        Log.e("page2", "JSON error: ${e.message}")
                    }
                }
            }
        })
    }
}
