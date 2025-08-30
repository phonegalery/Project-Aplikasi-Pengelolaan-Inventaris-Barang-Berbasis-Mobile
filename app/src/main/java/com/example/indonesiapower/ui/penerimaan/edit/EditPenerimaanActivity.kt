    package com.example.indonesiapower.ui.penerimaan.edit

    import android.annotation.SuppressLint
    import android.app.Activity
    import android.app.DatePickerDialog
    import android.app.TimePickerDialog
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Button
    import android.widget.ImageButton
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.example.indonesiapower.R
    import com.example.indonesiapower.api.ApiResponse
    import com.example.indonesiapower.api.RetrofitClient
    import com.example.indonesiapower.model.Barang
    import com.example.indonesiapower.model.Penerimaan
    import com.google.android.material.textfield.TextInputEditText
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import okhttp3.RequestBody
    import okhttp3.RequestBody.Companion.toRequestBody
    import org.json.JSONObject
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import java.text.SimpleDateFormat
    import java.util.Calendar
    import java.util.Locale

    class EditPenerimaanActivity : AppCompatActivity() {

        // Deklarasi view sesuai dengan layout
        private lateinit var etNoTerima: TextInputEditText
        private lateinit var etNamaBarang: TextInputEditText
        private lateinit var etJenisBarang: TextInputEditText
        private lateinit var etJumlah: TextInputEditText
        private lateinit var etTanggalTerima: TextInputEditText
        private lateinit var etJamTerima: TextInputEditText
        private lateinit var etSupplier: TextInputEditText
        private lateinit var etCatatanTambahan: TextInputEditText
        private lateinit var btnSimpan: Button
        private lateinit var btnKembali: ImageButton

        // Variabel untuk menyimpan data
        private var selectedDate: String = ""
        private var selectedTime: String = ""
        private var penerimaanId: Int = -1

        // [KODE YANG BENAR] - Salin dan tempel seluruh fungsi ini

        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edit_penerimaan)
            supportActionBar?.hide()

            window.statusBarColor = resources.getColor(R.color.white, theme)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            // Inisialisasi semua view
            btnKembali = findViewById(R.id.btnKembali)
            etNoTerima = findViewById(R.id.etNoTerima)
            etNamaBarang = findViewById(R.id.etNamaBarang)
            etJenisBarang = findViewById(R.id.etJenisBarang)
            etJumlah = findViewById(R.id.etJumlah)
            etTanggalTerima = findViewById(R.id.etTanggalTerima)
            etJamTerima = findViewById(R.id.etJamTerima)
            etSupplier = findViewById(R.id.etSupplier)
            etCatatanTambahan = findViewById(R.id.etCatatanTambahan)
            btnSimpan = findViewById(R.id.btnSimpan)

            // ================== PERUBAHAN UTAMA DI SINI ==================

            // 1. Ambil ID dari intent dan simpan ke variabel class 'this.penerimaanId'
            // Tidak ada lagi deklarasi 'var' di sini.
            this.penerimaanId = intent.getIntExtra("penerimaanId", -1)

            // 2. Ambil data lainnya dari intent
            val noTerima = intent.getStringExtra("no_terima")
            val namaBarang = intent.getStringExtra("nama_barang")
            val jenisBarang = intent.getStringExtra("jenis_barang")
            val jumlah = intent.getIntExtra("jumlah", 0)
            val tglTerima = intent.getStringExtra("tgl_terima")
            val jamTerima = intent.getStringExtra("jam_terima")
            val supplier = intent.getStringExtra("supplier")
            val catatan = intent.getStringExtra("catatan_tambahan")

            // 3. Lakukan validasi menggunakan variabel class 'this.penerimaanId'
            if (this.penerimaanId == -1) {
                Toast.makeText(this, "Gagal memuat data: ID tidak diterima.", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            // Baris 'penerimaanId = penerimaanId' yang salah sudah DIHAPUS.
            // =============================================================

            // Isi form dengan data yang diterima
            etNoTerima.setText(noTerima)
            etNamaBarang.setText(namaBarang)
            etJenisBarang.setText(jenisBarang)
            etJumlah.setText(jumlah.toString())
            etTanggalTerima.setText(tglTerima)
            etJamTerima.setText(jamTerima)
            etSupplier.setText(supplier)
            etCatatanTambahan.setText(catatan)

            setupListeners()
        }

        private fun setupListeners() {
            btnKembali.setOnClickListener {
                finish()
            }
            etTanggalTerima.isFocusable = false
            etTanggalTerima.isClickable = true
            etTanggalTerima.setOnClickListener {
                showDatePicker()
            }
            etJamTerima.isFocusable = false
            etJamTerima.isClickable = true
            etJamTerima.setOnClickListener {
                showTimePicker()
            }
            btnSimpan.setOnClickListener {
                updatePenerimaanData()
            }
        }

        private fun showDatePicker() {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etTanggalTerima.setText(selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        private fun showTimePicker() {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this, { _, hourOfDay, minute ->
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hourOfDay, minute, 0)
                    etJamTerima.setText(selectedTime)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        private fun updatePenerimaanData() {
            try {
                val noTerima = etNoTerima.text.toString().trim()
                val namaBarang = etNamaBarang.text.toString().trim()
                val jenisBarang = etJenisBarang.text.toString().trim()
                val jumlah = etJumlah.text.toString().trim()
                val tglTerima = etTanggalTerima.text.toString().trim()
                val jamTerima = etJamTerima.text.toString().trim()
                val supplier = etSupplier.text.toString().trim()
                val catatan = etCatatanTambahan.text.toString().trim()

                if (namaBarang.isEmpty() || jumlah.isEmpty()) {
                    Toast.makeText(this, "Nama Barang dan Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return
                }
                val jsonString = """
                    {
                        "penerimaanId": "$penerimaanId",
                        "no_terima": "$noTerima",
                        "nama_barang": "$namaBarang",
                        "jenis_barang": "$jenisBarang",
                        "jumlah": "$jumlah",
                        "tgl_terima": "$tglTerima",
                        "jam_terima": "$jamTerima",
                        "supplier": "$supplier",
                        "catatan_tambahan": "$catatan"
                    }
                """.trimIndent()
                val requestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(), jsonString
                )
                RetrofitClient.instance.editPenerimaan(requestBody).enqueue(object : Callback<ApiResponse<Penerimaan>> {
                    override fun onResponse(call: Call<ApiResponse<Penerimaan>>, response: Response<ApiResponse<Penerimaan>>) {
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                if (apiResponse.status == true) {
                                    Toast.makeText(this@EditPenerimaanActivity, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                } else {
                                    Toast.makeText(this@EditPenerimaanActivity, apiResponse.message ?: "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Toast.makeText(this@EditPenerimaanActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                response.errorBody()?.string() ?: "Unknown error"
                            } catch (e: Exception) {
                                "Error reading error body: ${e.message}"
                            }
                            Log.e("EditPenerimaanActivity", "API Error: $errorMsg")
                            Toast.makeText(this@EditPenerimaanActivity, "Error: ${response.code()} - $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse<Penerimaan>>, t: Throwable) {
                        Log.e("EditPenerimaanActivity", "Network error: ${t.message}", t)
                        Toast.makeText(this@EditPenerimaanActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: Exception) {
                Log.e("EditPenerimaanActivity", "Error: ${e.message}", e)
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // [DITAMBAHKAN] Fungsi fetchBarangByKode dari permintaan Anda
        private fun fetchBarangByKode(selectedKode: String) {
            val jsonObject = JSONObject()
            jsonObject.put("kode_barang", selectedKode)
            val jsonBody = jsonObject.toString()
            val requestBody: RequestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

            RetrofitClient.instance.getBarangByKode(requestBody)
                .enqueue(object : Callback<ApiResponse<List<Barang>>> {
                    override fun onResponse(
                        call: Call<ApiResponse<List<Barang>>>,
                        response: Response<ApiResponse<List<Barang>>>
                    ) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            val dataBarang = response.body()?.data
                            if (!dataBarang.isNullOrEmpty()) {
                                val barang = dataBarang[0]
                                // Mengisi data yang relevan
                                etNamaBarang.setText(barang.nama_barang)
                                etJenisBarang.setText(barang.jenis_barang)

                                // TODO: Kolom di bawah ini tidak ada di layout EditPenerimaan.
                                // Hapus atau sesuaikan dengan kebutuhan Anda.
                                // etPegawai.setText(barang.pegawai)
                                // etJabatan.setText(barang.jabatan)
                                // etDivisi.setText(barang.divisi)

                            } else {
                                clearRelevantEditTexts()
                            }
                        } else {
                            clearRelevantEditTexts()
                            Toast.makeText(this@EditPenerimaanActivity, "Data barang tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {
                        clearRelevantEditTexts()
                        Toast.makeText(this@EditPenerimaanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // [DITAMBAHKAN] Fungsi clearEditTexts yang disesuaikan untuk activity ini
        private fun clearRelevantEditTexts() {
            // Hanya mengosongkan field yang mungkin diisi oleh fetchBarangByKode
            etNamaBarang.setText("")
            etJenisBarang.setText("")
        }
    }