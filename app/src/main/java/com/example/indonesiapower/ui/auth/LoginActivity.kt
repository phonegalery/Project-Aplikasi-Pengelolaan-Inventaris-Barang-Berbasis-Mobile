package com.example.indonesiapower.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.MainActivity
import com.example.indonesiapower.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var buttonLogin: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        usernameEditText = findViewById(R.id.et_username)
        passwordEditText = findViewById(R.id.et_password)
        buttonLogin = findViewById(R.id.buttonLogin)
        val levelRadioGroup = findViewById<RadioGroup>(R.id.rg_user_type) // Ambil RadioGroup
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonLogin.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Ambil ID radio button yang dipilih
            val selectedRadioButtonId = levelRadioGroup.checkedRadioButtonId

            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Pilih level pengguna", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil teks dari RadioButton yang dipilih
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val level = selectedRadioButton.text.toString().lowercase()

            if (TextUtils.isEmpty(username)) {
                usernameEditText.error = "Username is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            loginUser(username, password, level)
        }
    }

    private fun loginUser(username: String, password: String, level: String) {
        val requestData = JSONObject()
        requestData.put("username", username)
        requestData.put("password", password)
        requestData.put("level", level)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), requestData.toString())
        val call = RetrofitClient.instance.loginUser(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()?.string()
                        Log.d("LoginActivity", "Response: $result")  // Log the entire response

                        val jsonResponse = JSONObject(result)

                        // Check if the response has the "status" and proceed with parsing
                        if (jsonResponse.getBoolean("status")) {
                            val userId = jsonResponse.getString("id_user").toInt()
                            val userUsername = jsonResponse.getString("username")
                            val userLevel = jsonResponse.getString("level")
                            val userNama = jsonResponse.getString("nama")
                            val userNIP = jsonResponse.getString("nip")

                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt("id_user", userId)
                            editor.putString("username", userUsername)
                            editor.putString("level", userLevel)
                            editor.putString("nama", userNama)
                            editor.putString("nip", userNIP)
                            editor.apply()

                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Gagal: ${jsonResponse.getString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("LoginActivity", "JSON Error: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error Parsing Response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("LoginActivity", "Response Error: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginActivity", "Request Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}