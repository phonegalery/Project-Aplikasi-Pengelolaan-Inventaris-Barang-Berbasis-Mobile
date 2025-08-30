package com.example.indonesiapower.ui.admin.edit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Admin
import com.example.indonesiapower.model.Pemeliharaan
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAdminActivity : AppCompatActivity() {

    private lateinit var btnKembali: ImageButton
    private lateinit var btnSimpan: Button
    private lateinit var etNama: TextInputEditText
    private lateinit var etNip: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_admin)
        supportActionBar?.hide()

        // Status bar light mode
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi komponen
        btnKembali = findViewById(R.id.btnKembali)
        btnSimpan = findViewById(R.id.btnSimpan)
        etNama = findViewById(R.id.etNama)
        etNip = findViewById(R.id.etNip)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)

        // Tombol kembali
        btnKembali.setOnClickListener {
            finish()
        }

        // Ambil data dari Intent
        val id = intent.getIntExtra("id", -1)
        val nip = intent.getIntExtra("nip", 0)
        val nama = intent.getStringExtra("nama") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        // Isi field dengan data dari intent
        etNama.setText(nama)
        etNip.setText(nip.toString())
        etUsername.setText(username)
        etPassword.setText(password)

        // Tombol simpan
        btnSimpan.setOnClickListener {
            try {
                // Ambil data dari UI
                val nip = etNip.text?.toString()?.trim() ?: ""
                val nama = etNama.text?.toString()?.trim() ?: ""
                val username = etUsername.text?.toString()?.trim() ?: ""
                val password = etPassword.text?.toString()?.trim() ?: ""
                val id = intent.getIntExtra("id", -1)

                // Validasi ID
                if (id == -1) {
                    Log.e("EditAdminActivity", "ID admin tidak valid: $id")
                    Toast.makeText(this, "ID admin tidak valid", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Buat JSON string manual
                val jsonString = """
            {
                "id": $id,
                "nip": "$nip",
                "nama": "$nama",
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()

                // Log isi JSON yang dikirim
                Log.d("EditAdminActivity", "JSON to be sent: $jsonString")

                // Buat RequestBody dari JSON string
                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonString
                )

                // Panggil API
                RetrofitClient.instance.editAdmin(requestBody).enqueue(object : Callback<ApiResponse<Admin>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Admin>>,
                        response: Response<ApiResponse<Admin>>
                    ) {
                        Log.d("EditAdminActivity", "Response code: ${response.code()}")

                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                Log.d("EditAdminActivity", "Response body: $apiResponse")

                                if (apiResponse.status == true) {
                                    Toast.makeText(this@EditAdminActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    Log.w("EditAdminActivity", "Gagal menyimpan data: ${apiResponse.message}")
                                    Toast.makeText(this@EditAdminActivity, apiResponse.message ?: "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Log.e("EditAdminActivity", "Response body is null")
                                Toast.makeText(this@EditAdminActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                response.errorBody()?.string() ?: "Unknown error"
                            } catch (e: Exception) {
                                "Error reading error body: ${e.message}"
                            }
                            Log.e("EditAdminActivity", "API Error (${response.code()}): $errorMsg")
                            Toast.makeText(this@EditAdminActivity, "Error: ${response.code()} - $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Admin>>, t: Throwable) {
                        Log.e("EditAdminActivity", "Network error: ${t.message}", t)
                        Toast.makeText(this@EditAdminActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Log.e("EditAdminActivity", "Exception during request: ${e.message}", e)
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
