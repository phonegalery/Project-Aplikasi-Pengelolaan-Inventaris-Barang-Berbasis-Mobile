package com.example.indonesiapower.ui.pemeliharaan

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
import com.example.indonesiapower.model.Pemeliharaan
import com.example.indonesiapower.ui.pemeliharaan.tambah.TambahPemeliharaanActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class RiwayatPemeliharaanActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatPemeliharaanAdapter

    // [KOREKSI] Diubah dari Kategori menjadi Pemeliharaan
    private var fullPemeliharaanList: List<Pemeliharaan> = ArrayList()

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
        setContentView(R.layout.activity_riwayat_pemeliharaan)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi semua view
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        val buttonTambah: Button = findViewById(R.id.buttonTambah)
        recyclerView = findViewById(R.id.recyclerViewRiwayatPemeliharaan)
        searchInput = findViewById(R.id.searchInput) // [TAMBAHAN] Inisialisasi search input

        btnKembali.setOnClickListener {
            finish()
        }

        buttonTambah.setOnClickListener {
            val intent = Intent(this@RiwayatPemeliharaanActivity, TambahPemeliharaanActivity::class.java)
            startForResult.launch(intent)
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatPemeliharaanAdapter(emptyList(), startForResult) {
            refreshData()
        }
        recyclerView.adapter = adapter

        // [TAMBAHAN] Panggil fungsi untuk setup fitur pencarian
        setupSearchFeature()

        idUser = getUserIdFromSharedPreferences()
        fetchPemeliharaan()
    }

    // [TAMBAHAN] Fungsi baru untuk mengatur logika pencarian
    private fun setupSearchFeature() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // [MODIFIKASI] Logika filter sekarang ada di sini
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = ArrayList<Pemeliharaan>()

                if (query.isEmpty()) {
                    filteredList.addAll(fullPemeliharaanList)
                } else {
                    for (item in fullPemeliharaanList) {
                        // SESUAIKAN 'item.nama_barang' dengan properti yang ingin Anda jadikan acuan pencarian
                        // Contoh: bisa juga item.keterangan, item.tanggal, dll.
                        if (item.nama_barang?.lowercase(Locale.getDefault())?.contains(query) == true) {
                            filteredList.add(item)
                        }
                    }
                }
                adapter.updateData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPemeliharaan() {
        RetrofitClient.instance.riwayatPemeliharaan().enqueue(object : Callback<ApiResponse<List<Pemeliharaan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pemeliharaan>>>,
                response: Response<ApiResponse<List<Pemeliharaan>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        responseBody.data?.let { data ->
                            // [MODIFIKASI] Simpan data lengkap ke variabel fullPemeliharaanList
                            fullPemeliharaanList = data
                            // Tampilkan semua data ke adapter saat pertama kali dimuat
                            adapter.updateData(fullPemeliharaanList)
                        }
                    } else {
                        Log.e("RiwayatPemeliharaan", "Gagal mendapatkan data: ${responseBody?.message}")
                    }
                } else {
                    Log.e("RiwayatPemeliharaan", "Request gagal dengan kode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pemeliharaan>>>, t: Throwable) {
                Log.e("RiwayatPemeliharaan", "Gagal menghubungi server: ${t.localizedMessage}", t)
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
        fetchPemeliharaan()
    }
}