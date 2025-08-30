package com.example.indonesiapower.ui.barang

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
import com.example.indonesiapower.model.Barang
import com.example.indonesiapower.ui.barang.tambah.TambahBarangActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class RiwayatBarangActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatBarangAdapter

    // [TAMBAHAN] Untuk menyimpan daftar barang lengkap
    private var fullBarangList: List<Barang> = ArrayList()

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
        setContentView(R.layout.activity_riwayat_barang)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi semua view
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        val buttonTambah: Button = findViewById(R.id.buttonTambah)
        recyclerView = findViewById(R.id.recyclerViewRiwayatBarang)
        searchInput = findViewById(R.id.searchInput) // [TAMBAHAN] Inisialisasi search input

        btnKembali.setOnClickListener {
            finish()
        }

        buttonTambah.setOnClickListener {
            val intent = Intent(this@RiwayatBarangActivity, TambahBarangActivity::class.java)
            startForResult.launch(intent)
        }

        // Setup RecyclerView (tidak ada yang berubah di sini)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatBarangAdapter(emptyList(), startForResult) {
            refreshData()
        }
        recyclerView.adapter = adapter

        // [TAMBAHAN] Panggil fungsi untuk setup fitur pencarian
        setupSearchFeature()

        idUser = getUserIdFromSharedPreferences()
        fetchBarang()
    }

    // [TAMBAHAN] Fungsi baru untuk mengatur logika pencarian
    private fun setupSearchFeature() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // [MODIFIKASI] Logika filter sekarang ada di sini
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = ArrayList<Barang>()

                if (query.isEmpty()) {
                    // Jika query kosong, tampilkan semua data dari daftar lengkap
                    filteredList.addAll(fullBarangList)
                } else {
                    // Jika ada query, filter daftar lengkap
                    for (item in fullBarangList) {
                        if (item.nama_barang?.lowercase(Locale.getDefault())?.contains(query) == true) {
                            filteredList.add(item)
                        }
                    }
                }
                // Kirim hasil filter ke adapter untuk ditampilkan
                adapter.updateData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun fetchBarang() {
        RetrofitClient.instance.riwayatBarang().enqueue(object : Callback<ApiResponse<List<Barang>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Barang>>>,
                response: Response<ApiResponse<List<Barang>>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        responseBody.data?.let { data ->
                            // [MODIFIKASI] Simpan data lengkap ke variabel fullBarangList
                            fullBarangList = data
                            // Tampilkan semua data ke adapter saat pertama kali dimuat
                            adapter.updateData(fullBarangList)
                        }
                    } else {
                        Log.e("RiwayatBarang", "Gagal mendapatkan data: ${responseBody?.message}")
                    }
                } else {
                    Log.e("RiwayatBarang", "Request gagal dengan kode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {
                Log.e("RiwayatBarang", "Gagal menghubungi server: ${t.localizedMessage}", t)
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
        fetchBarang()
    }
}