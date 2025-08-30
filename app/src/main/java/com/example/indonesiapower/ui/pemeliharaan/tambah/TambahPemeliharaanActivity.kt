package com.example.indonesiapower.ui.pemeliharaan.tambah

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Barang
import com.example.indonesiapower.model.Pemeliharaan
import com.example.indonesiapower.model.Petugas
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class TambahPemeliharaanActivity : AppCompatActivity() {

    private lateinit var spinnerKodeBarang: Spinner
    private lateinit var etNamaBarang: TextInputEditText
    private lateinit var etPegawai: TextInputEditText
    private lateinit var etJabatan: TextInputEditText
    private lateinit var etDivisi: TextInputEditText
    private lateinit var spinnerKondisi: Spinner
    private lateinit var spinnerNamaPetugas: Spinner
    private lateinit var etTanggalBarangMasuk: TextInputEditText
    private lateinit var etCatatanTambahan: TextInputEditText
    private lateinit var etTanggalPemeliharaanSelanjutnya: TextInputEditText
    private lateinit var btnSimpan: Button

    private var selectedDate: String = ""
    private lateinit var barangList: List<Barang>
    private lateinit var petugasList: List<Petugas>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pemeliharaan)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        spinnerKodeBarang = findViewById(R.id.spinnerKodeBarang)
        etNamaBarang = findViewById(R.id.etNamaBarang)
        etPegawai = findViewById(R.id.etPegawai)
        etJabatan = findViewById(R.id.etJabatan)
        etDivisi = findViewById(R.id.etDivisi)
        spinnerKondisi = findViewById(R.id.spinnerKondisi)
        spinnerNamaPetugas = findViewById(R.id.spinnerNamaPetugas)
        etTanggalBarangMasuk = findViewById(R.id.etTanggalBarangMasuk)
        etCatatanTambahan = findViewById(R.id.etCatatanTambahan)
        etTanggalPemeliharaanSelanjutnya = findViewById(R.id.etTanggalPemeliharaanSelanjutnya)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnKembali.setOnClickListener {
            finish()
        }

        loadKodeBarang()
        loadNamaPetugas()

        etTanggalBarangMasuk.isFocusable = false
        etTanggalBarangMasuk.isClickable = true
        etTanggalBarangMasuk.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalBarangMasuk.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        etTanggalPemeliharaanSelanjutnya.isFocusable = false
        etTanggalPemeliharaanSelanjutnya.isClickable = true
        etTanggalPemeliharaanSelanjutnya.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPemeliharaanSelanjutnya.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Di onCreate()
        val kondisiList = listOf("Sudah Diperbaiki", "Rusak", "Butuh Perbaikan")
        val adapterKondisi = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kondisiList
        )
        adapterKondisi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKondisi.adapter = adapterKondisi
        spinnerKondisi.setSelection(0, true)

        btnSimpan.setOnClickListener {
            try {
                // Ambil data dari UI
                val kodeBarang = spinnerKodeBarang.selectedItem?.toString()?.trim() ?: ""
                val namaBarang = etNamaBarang.text?.toString()?.trim() ?: ""
                val pegawai = etPegawai.text?.toString()?.trim() ?: ""
                val jabatan = etJabatan.text?.toString()?.trim() ?: ""
                val divisi = etDivisi.text?.toString()?.trim() ?: ""
                val kondisi = spinnerKondisi.selectedItem?.toString()?.trim() ?: ""
                val namaPetugas = spinnerNamaPetugas.selectedItem?.toString()?.trim() ?: ""
                val tanggalBarangMasuk = etTanggalBarangMasuk.text?.toString()?.trim() ?: ""
                val catatanTambahan = etCatatanTambahan.text?.toString()?.trim() ?: ""
                val tanggalPemeliharaanSelanjutnya = etTanggalPemeliharaanSelanjutnya.text?.toString()?.trim() ?: ""

                // Buat JSON string manual
                val jsonString = """
            {
                "kode_barang": "$kodeBarang",
                "nama_barang": "$namaBarang",
                "pegawai": "$pegawai",
                "jabatan": "$jabatan",
                "divisi": "$divisi",
                "kondisi": "$kondisi",
                "nama_petugas": "$namaPetugas",
                "tanggal_barang_masuk": "$tanggalBarangMasuk",
                "catatan_tambahan": "$catatanTambahan",
                "tanggal_pemeliharaan_selanjutnya": "$tanggalPemeliharaanSelanjutnya"
            }
        """.trimIndent()

                // Buat RequestBody dari JSON string
                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonString
                )

                // Panggil API
                RetrofitClient.instance.tambahPemeliharaan(requestBody).enqueue(object : Callback<ApiResponse<Pemeliharaan>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Pemeliharaan>>,
                        response: Response<ApiResponse<Pemeliharaan>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                if (apiResponse.status == true) {
                                    Toast.makeText(this@TambahPemeliharaanActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)
                                    finish()
                                } else {
                                    Toast.makeText(this@TambahPemeliharaanActivity, apiResponse.message ?: "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Toast.makeText(this@TambahPemeliharaanActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                response.errorBody()?.string() ?: "Unknown error"
                            } catch (e: Exception) {
                                "Error reading error body: ${e.message}"
                            }
                            Log.e("TambahPemeliharaanActivity", "API Error: $errorMsg")
                            Toast.makeText(this@TambahPemeliharaanActivity, "Error: ${response.code()} - $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Pemeliharaan>>, t: Throwable) {
                        Log.e("TambahPemeliharaanActivity", "Network error: ${t.message}", t)
                        Toast.makeText(this@TambahPemeliharaanActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } catch (e: Exception) {
                Log.e("TambahPemeliharaanActivity", "Error: ${e.message}", e)
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadKodeBarang() {
        RetrofitClient.instance.riwayatBarang().enqueue(object : Callback<ApiResponse<List<Barang>>> {
            override fun onResponse(call: Call<ApiResponse<List<Barang>>>, response: Response<ApiResponse<List<Barang>>>) {
                if (response.isSuccessful && response.body() != null) {
                    barangList = response.body()!!.data ?: emptyList()

                    // Tambahkan pengecekan jika list kosong
                    if (barangList.isEmpty()) {
                        Toast.makeText(this@TambahPemeliharaanActivity, "Tidak ada data barang", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val kodeBarangList = barangList.map { it.kode_barang.toString() }

                    val adapter = ArrayAdapter(
                        this@TambahPemeliharaanActivity,
                        android.R.layout.simple_spinner_item,
                        kodeBarangList
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerKodeBarang.adapter = adapter

                    spinnerKodeBarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (position >= 0 && position < barangList.size) {
                                val selectedBarang = barangList[position]
                                etNamaBarang.setText(selectedBarang.nama_barang)
                                etPegawai.setText(selectedBarang.pegawai)
                                etJabatan.setText(selectedBarang.jabatan)
                                etDivisi.setText(selectedBarang.divisi)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Tambahkan handling ketika tidak ada yang terpilih
                            Toast.makeText(this@TambahPemeliharaanActivity, "Pilih kode barang", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this@TambahPemeliharaanActivity, "Gagal load data barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {
                Toast.makeText(this@TambahPemeliharaanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadNamaPetugas() {
        RetrofitClient.instance.riwayatPetugas().enqueue(object : Callback<ApiResponse<List<Petugas>>> {
            override fun onResponse(call: Call<ApiResponse<List<Petugas>>>, response: Response<ApiResponse<List<Petugas>>>) {
                if (response.isSuccessful && response.body() != null) {
                    petugasList = response.body()!!.data ?: emptyList()

                    val namaPetugasList = petugasList.map { it.nama.toString() }

                    val adapter = ArrayAdapter(
                        this@TambahPemeliharaanActivity,
                        android.R.layout.simple_spinner_item,
                        namaPetugasList
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerNamaPetugas.adapter = adapter

                    spinnerNamaPetugas.setSelection(0, true)

                } else {
                    Toast.makeText(this@TambahPemeliharaanActivity, "Gagal load data barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Petugas>>>, t: Throwable) {
                Toast.makeText(this@TambahPemeliharaanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
