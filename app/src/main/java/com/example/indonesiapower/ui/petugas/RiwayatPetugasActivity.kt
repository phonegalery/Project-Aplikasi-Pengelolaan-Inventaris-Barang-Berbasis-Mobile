package com.example.indonesiapower.ui.petugas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Petugas
import com.example.indonesiapower.ui.petugas.tambah.TambahPetugasActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.collections.ArrayList

class RiwayatPetugasActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatPetugasAdapter

    // [TAMBAHAN] Variabel untuk menyimpan daftar petugas lengkap
    private var fullPetugasList: List<Petugas> = ArrayList()

    // [TAMBAHAN] Referensi ke input pencarian
    private lateinit var searchInput: TextInputEditText

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_petugas)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi Views
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        val buttonTambah: Button = findViewById(R.id.buttonTambah)
        recyclerView = findViewById(R.id.recyclerViewRiwayatPetugas)
        searchInput = findViewById(R.id.searchInput) // [TAMBAHAN] Inisialisasi search input

        btnKembali.setOnClickListener {
            finish()
        }

        buttonTambah.setOnClickListener {
            val intent = Intent(this@RiwayatPetugasActivity, TambahPetugasActivity::class.java)
            startForResult.launch(intent)
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatPetugasAdapter(emptyList(), startForResult) {
            refreshData()
        }
        recyclerView.adapter = adapter

        // [TAMBAHAN] Panggil fungsi untuk setup fitur pencarian
        setupSearchFeature()

        idUser = getUserIdFromSharedPreferences()
        fetchPetugas()
    }

    // [TAMBAHAN] Fungsi baru untuk mengatur logika pencarian
    private fun setupSearchFeature() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // [MODIFIKASI] Logika filter sekarang ada di sini
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = ArrayList<Petugas>()

                if (query.isEmpty()) {
                    filteredList.addAll(fullPetugasList)
                } else {
                    for (item in fullPetugasList) {
                        // Pencarian bisa berdasarkan nama atau username
                        val namaMatches = item.nama?.lowercase(Locale.getDefault())?.contains(query) == true
                        val usernameMatches = item.username?.lowercase(Locale.getDefault())?.contains(query) == true

                        if (namaMatches || usernameMatches) {
                            filteredList.add(item)
                        }
                    }
                }
                adapter.updateData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun fetchPetugas() {
        RetrofitClient.instance.riwayatPetugas().enqueue(object : Callback<ApiResponse<List<Petugas>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Petugas>>>,
                response: Response<ApiResponse<List<Petugas>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        responseBody.data?.let { data ->
                            // [MODIFIKASI] Simpan data lengkap ke variabel fullPetugasList
                            fullPetugasList = data
                            // Tampilkan semua data ke adapter saat pertama kali dimuat
                            adapter.updateData(fullPetugasList)
                        }
                    } else {
                        Log.e("RiwayatPetugas", "Gagal mendapatkan data: ${responseBody?.message}")
                    }
                } else {
                    Log.e(
                        "RiwayatPetugas",
                        "Request gagal dengan kode: ${response.code()}, pesan: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Petugas>>>, t: Throwable) {
                Log.e("RiwayatPetugas", "Gagal menghubungi server: ${t.localizedMessage}", t)
            }
        })
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun refreshData() {
        // [MODIFIKASI] Kosongkan input pencarian saat refresh
        searchInput.text?.clear()
        fetchPetugas()
    }
}