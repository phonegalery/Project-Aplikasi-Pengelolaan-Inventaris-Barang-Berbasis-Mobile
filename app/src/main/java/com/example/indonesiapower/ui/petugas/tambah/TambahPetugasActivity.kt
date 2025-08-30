package com.example.indonesiapower.ui.petugas.tambah

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Petugas
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahPetugasActivity : AppCompatActivity() {

    private lateinit var btnKembali: ImageButton
    private lateinit var btnSimpan: Button
    private lateinit var etNama: TextInputEditText
    private lateinit var etNip: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_petugas)
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

        // Tombol simpan
        btnSimpan.setOnClickListener {
            try {
                // Ambil data dari UI
                val nip = etNip.text?.toString()?.trim() ?: ""
                val nama = etNama.text?.toString()?.trim() ?: ""
                val username = etUsername.text?.toString()?.trim() ?: ""
                val password = etPassword.text?.toString()?.trim() ?: ""

                // Buat JSON string manual
                val jsonString = """
            {
                "nip": "$nip",
                "nama": "$nama",
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()

                // Log isi JSON yang akan dikirim
                Log.d("TambahPetugasActivity", "JSON to be sent: $jsonString")

                // Buat RequestBody dari JSON string
                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonString
                )

                // Panggil API
                RetrofitClient.instance.tambahPetugas(requestBody).enqueue(object : Callback<ApiResponse<Petugas>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Petugas>>,
                        response: Response<ApiResponse<Petugas>>
                    ) {
                        Log.d("TambahPetugasActivity", "Response code: ${response.code()}")
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                Log.d("TambahPetugasActivity", "Response body: $apiResponse")

                                if (apiResponse.status == true) {
                                    Toast.makeText(this@TambahPetugasActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    Log.w("TambahPetugasActivity", "Gagal menyimpan data: ${apiResponse.message}")
                                    Toast.makeText(this@TambahPetugasActivity, apiResponse.message ?: "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Log.e("TambahPetugasActivity", "Response body is null")
                                Toast.makeText(this@TambahPetugasActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                response.errorBody()?.string() ?: "Unknown error"
                            } catch (e: Exception) {
                                "Error reading error body: ${e.message}"
                            }
                            Log.e("TambahPetugasActivity", "API Error (${response.code()}): $errorMsg")
                            Toast.makeText(this@TambahPetugasActivity, "Error: ${response.code()} - $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Petugas>>, t: Throwable) {
                        Log.e("TambahPetugasActivity", "Network error: ${t.message}", t)
                        Toast.makeText(this@TambahPetugasActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Log.e("TambahPetugasActivity", "Exception during request: ${e.message}", e)
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}