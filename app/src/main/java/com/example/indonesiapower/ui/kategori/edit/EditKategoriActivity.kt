package com.example.indonesiapower.ui.kategori.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Kategori
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditKategoriActivity : AppCompatActivity() {

    private lateinit var etKodeKategori: EditText
    private lateinit var etNamaKategori: EditText
    private lateinit var etDieditTanggal: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnKembali: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_kategori)
        supportActionBar?.hide()

        // Status bar warna putih dan icon gelap
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi view
        etKodeKategori = findViewById(R.id.etKodeKategori)
        etNamaKategori = findViewById(R.id.etNamaKategori)
        etDieditTanggal = findViewById(R.id.etDieditTanggal)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnKembali = findViewById(R.id.btnKembali)

        btnKembali.setOnClickListener {
            finish()
        }

        // Set tanggal saat ini dengan format dd-MM-yyyy dan disable editing
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        etDieditTanggal.setText(currentDate)
        etDieditTanggal.isEnabled = false

        // Ambil data intent untuk edit
        val idKategori = intent.getIntExtra("id_kategori", 0)
        val kodeKategori = intent.getIntExtra("kode_kategori", 0)
        val namaKategori = intent.getStringExtra("nama_kategori") ?: ""

        etKodeKategori.setText(kodeKategori.toString())
        etNamaKategori.setText(namaKategori)

        btnSimpan.setOnClickListener {
            try {
                val kodeKategoriInput = etKodeKategori.text?.toString()?.trim() ?: ""
                val namaKategoriInput = etNamaKategori.text?.toString()?.trim() ?: ""

                if (kodeKategoriInput.isEmpty() || namaKategoriInput.isEmpty()) {
                    Toast.makeText(this, "Kode dan nama kategori harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Buat JSON string tanpa trailing koma
                val jsonString = """
                    {
                        "id_kategori": "$idKategori",
                        "kode_kategori": "$kodeKategoriInput",
                        "nama_kategori": "$namaKategoriInput"
                    }
                """.trimIndent()

                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonString
                )

                RetrofitClient.instance.editKategori(requestBody).enqueue(object : Callback<ApiResponse<Kategori>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Kategori>>,
                        response: Response<ApiResponse<Kategori>>
                    ) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.status == true) {
                                Toast.makeText(this@EditKategoriActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(this@EditKategoriActivity, apiResponse?.message ?: "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                response.errorBody()?.string() ?: "Unknown error"
                            } catch (e: Exception) {
                                "Error reading error body: ${e.message}"
                            }
                            Log.e("EditKategoriActivity", "API Error: $errorMsg")
                            Toast.makeText(this@EditKategoriActivity, "Error: ${response.code()} - $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Kategori>>, t: Throwable) {
                        Log.e("EditKategoriActivity", "Network error: ${t.message}", t)
                        Toast.makeText(this@EditKategoriActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Log.e("EditKategoriActivity", "Error: ${e.message}", e)
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
