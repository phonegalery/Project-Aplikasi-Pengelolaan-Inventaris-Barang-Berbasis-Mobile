package com.example.indonesiapower.ui.penerimaan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Penerimaan
import com.example.indonesiapower.ui.penerimaan.tambah.TambahPenerimaanActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

// [DIHAPUS] Tidak perlu lagi implementasi interface
class RiwayatPenerimaanActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatPenerimaanAdapter
    private var fullPenerimaanList: List<Penerimaan> = ArrayList()
    private lateinit var searchInput: TextInputEditText

    // [DISESUAIKAN] Nama variabel disamakan dengan contoh
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Memuat ulang data...", Toast.LENGTH_SHORT).show()
            refreshData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_penerimaan)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi Views
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        recyclerView = findViewById(R.id.recyclerViewRiwayatPenerimaan)
        searchInput = findViewById(R.id.searchInput)
        val buttonTambah: Button = findViewById(R.id.buttonTambah)

        btnKembali.setOnClickListener {
            finish()
        }

        buttonTambah.setOnClickListener {
            val intent = Intent(this, TambahPenerimaanActivity::class.java)
            startForResult.launch(intent)
        }

        // [DIUBAH] Inisialisasi Adapter disesuaikan dengan format baru
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatPenerimaanAdapter(emptyList(), startForResult) {
            // Ini adalah lambda onDataChanged. Akan dipanggil dari adapter
            // untuk merefresh data setelah ada perubahan (misal: hapus).
            refreshData()
        }
        recyclerView.adapter = adapter

        setupSearchFeature()

        idUser = getUserIdFromSharedPreferences()
        if (idUser != -1) {
            fetchPenerimaan()
        } else {
            Toast.makeText(this, "ID User tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    // [DIHAPUS] Fungsi onEditClicked dan onDeleteClicked tidak lagi ada di Activity

    private fun setupSearchFeature() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = fullPenerimaanList.filter { penerimaan ->
                    penerimaan.nama_barang?.lowercase(Locale.getDefault())?.contains(query) == true
                }
                adapter.updateData(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPenerimaan() {
        RetrofitClient.instance.riwayatPenerimaan().enqueue(object : Callback<ApiResponse<List<Penerimaan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Penerimaan>>>,
                response: Response<ApiResponse<List<Penerimaan>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    response.body()?.data?.let { data ->
                        fullPenerimaanList = data
                        adapter.updateData(fullPenerimaanList)
                    }
                } else {
                    Toast.makeText(this@RiwayatPenerimaanActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Penerimaan>>>, t: Throwable) {
                Toast.makeText(this@RiwayatPenerimaanActivity, "Kesalahan jaringan: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun refreshData() {
        searchInput.text?.clear()
        fetchPenerimaan()
    }
}