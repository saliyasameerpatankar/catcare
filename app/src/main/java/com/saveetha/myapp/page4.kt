package com.saveetha.myapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class page4 : AppCompatActivity() {

    private lateinit var btnCreate: Button
    private lateinit var tvNoProfile: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var container: LinearLayout
    private val client = OkHttpClient()
    private val apiUrl = "http://172.23.53.39/check_cat_profiles.php"
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page4)

        btnCreate = findViewById(R.id.create_profilebutton)
        tvNoProfile = findViewById(R.id.no_profilestext)
        tvSubtitle = findViewById(R.id.subtitl)
        ivIcon = findViewById(R.id.caticon)
        tvAppName = findViewById(R.id.appname)
        container = findViewById(R.id.dynamic_content_container)

        userId = intent.getIntExtra("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Invalid user session", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnCreate.setOnClickListener {
            // Navigate to profile creation page
            val intent = Intent(this, Page5::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        // Show loading state initially (optional)
        showNoProfileUI()

        // Fetch profiles from server
        fetchProfiles()
    }

    override fun onResume() {
        super.onResume()
        // Refresh profile data every time page4 becomes visible,
        // this helps show newly created profiles immediately after returning from page5
        fetchProfiles()
    }

    private fun fetchProfiles() {
        val json = JSONObject().put("userid", userId)
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        client.newCall(Request.Builder().url(apiUrl).post(body).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("page4", "API call failure: ${e.message}")
                    runOnUiThread { showNoProfileUI() }
                }

                override fun onResponse(call: Call, response: Response) {
                    val resp = response.body?.string().orEmpty()
                    Log.d("page4", resp)
                    runOnUiThread {
                        try {
                            val jsonResp = JSONObject(resp)
                            if (jsonResp.getString("status") == "success") {
                                val profiles = jsonResp.getJSONArray("profiles")
                                if (profiles.length() > 0) {
                                    // Show first profile only (you can extend this to show all)
                                    val p = profiles.getJSONObject(0)
                                    showProfile(
                                        p.getString("catname"),
                                        p.getString("age"),
                                        p.getString("breed"),
                                        p.getString("gender"),
                                        p.optString("photo", "")
                                    )
                                } else {
                                    // No profiles - show UI for creating one
                                    showNoProfileUI()
                                }
                            } else {
                                showNoProfileUI()
                            }
                        } catch (ex: Exception) {
                            Log.e("page4", "Parse error: ${ex.message}")
                            showNoProfileUI()
                        }
                    }
                }
            })
    }

    private fun showNoProfileUI() {
        container.removeAllViews()
        container.visibility = View.GONE
        btnCreate.visibility = View.VISIBLE
        tvNoProfile.visibility = View.VISIBLE
        tvSubtitle.visibility = View.VISIBLE
        ivIcon.visibility = View.VISIBLE
        tvAppName.visibility = View.VISIBLE
    }

    private fun showProfile(name: String, age: String, breed: String, gender: String, photoUrl: String) {
        btnCreate.visibility = View.GONE
        tvNoProfile.visibility = View.GONE
        tvSubtitle.visibility = View.GONE
        ivIcon.visibility = View.GONE
        tvAppName.visibility = View.GONE

        container.removeAllViews()
        container.visibility = View.VISIBLE

        val views = listOf(
            TextView(this).apply {
                text = "🐾 Cat Profile"
                textSize = 22f
                setPadding(0, 0, 0, 10)
            },
            ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(400, 400)
            },
            TextView(this).apply { text = "Name: $name" },
            TextView(this).apply { text = "Age: $age" },
            TextView(this).apply { text = "Breed: $breed" },
            TextView(this).apply { text = "Gender: $gender" },
            Button(this).apply {
                text = "Edit Profile"
                setOnClickListener {
                    val intent = Intent(this@page4, Page5::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
        )

        views.forEach { container.addView(it) }

        if (photoUrl.isNotEmpty()) {
            Thread {
                try {
                    val bmp = BitmapFactory.decodeStream(URL(photoUrl).openStream())
                    runOnUiThread { (views[1] as ImageView).setImageBitmap(bmp) }
                } catch (e: Exception) {
                    Log.e("page4", "Image load failed: ${e.message}")
                }
            }.start()
        }
    }
}
