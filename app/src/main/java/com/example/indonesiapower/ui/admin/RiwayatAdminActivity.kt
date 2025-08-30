package com.example.indonesiapower.ui.admin

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
import com.example.indonesiapower.model.Admin
import com.example.indonesiapower.ui.admin.tambah.TambahAdminActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.collections.ArrayList

class RiwayatAdminActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatAdminAdapter

    // [TAMBAHAN] Variabel untuk menyimpan daftar admin lengkap
    private var fullAdminList: List<Admin> = ArrayList()

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
        setContentView(R.layout.activity_riwayat_admin)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi Views
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        val buttonTambah: Button = findViewById(R.id.buttonTambah)
        recyclerView = findViewById(R.id.recyclerViewRiwayatAdmin)
        searchInput = findViewById(R.id.searchInput) // [TAMBAHAN] Inisialisasi search input

        btnKembali.setOnClickListener {
            finish()
        }

        buttonTambah.setOnClickListener {
            val intent = Intent(this@RiwayatAdminActivity, TambahAdminActivity::class.java)
            startForResult.launch(intent)
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatAdminAdapter(emptyList(), startForResult) {
            refreshData()
        }
        recyclerView.adapter = adapter

        // [TAMBAHAN] Panggil fungsi untuk setup fitur pencarian
        setupSearchFeature()

        idUser = getUserIdFromSharedPreferences()
        fetchAdmin()
    }

    // [TAMBAHAN] Fungsi baru untuk mengatur logika pencarian
    private fun setupSearchFeature() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // [MODIFIKASI] Logika filter sekarang ada di sini
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = ArrayList<Admin>()

                if (query.isEmpty()) {
                    filteredList.addAll(fullAdminList)
                } else {
                    for (item in fullAdminList) {
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

    private fun fetchAdmin() {
        RetrofitClient.instance.riwayatAdmin().enqueue(object : Callback<ApiResponse<List<Admin>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Admin>>>,
                response: Response<ApiResponse<List<Admin>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        responseBody.data?.let { data ->
                            // [MODIFIKASI] Simpan data lengkap ke variabel fullAdminList
                            fullAdminList = data
                            // Tampilkan semua data ke adapter saat pertama kali dimuat
                            adapter.updateData(fullAdminList)
                        }
                    } else {
                        Log.e("RiwayatAdmin", "Gagal mendapatkan data: ${responseBody?.message}")
                    }
                } else {
                    Log.e("RiwayatAdmin", "Request gagal dengan kode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Admin>>>, t: Throwable) {
                Log.e("RiwayatAdmin", "Gagal menghubungi server: ${t.localizedMessage}", t)
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
        fetchAdmin()
    }
}