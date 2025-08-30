package com.example.indonesiapower.ui.barang.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.indonesiapower.R
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Kategori
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.indonesiapower.model.Barang
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditBarangActivity : AppCompatActivity() {

    // Deklarasi View
    private lateinit var etKodeBarang: TextInputEditText
    private lateinit var spinnerJenisBarang: Spinner
    private lateinit var etNamaBarang: TextInputEditText
    private lateinit var etTanggalMasuk: TextInputEditText
    private lateinit var etPegawai: TextInputEditText
    private lateinit var spinnerJabatan: Spinner
    private lateinit var spinnerDivisi: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var etMerk: TextInputEditText
    private lateinit var etType: TextInputEditText
    private lateinit var spinnerKondisi: Spinner
    private lateinit var etCatatanTambahan: TextInputEditText
    private lateinit var tvNamaFileGambar: TextView
    private lateinit var imageView: ImageView
    private lateinit var btnSimpan: Button

    // Data dari intent
    private var idBarang: Int = 0
    private var kodeBarang: Int = 0
    private var jenisBarang: String = ""
    private var namaBarang: String = ""
    private var tanggalMasuk: String = ""
    private var pegawai: String = ""
    private var jabatan: String = ""
    private var divisi: String = ""
    private var status: String = ""
    private var merk: String = ""
    private var type: String = ""
    private var kondisi: String = ""
    private var catatanTambahan: String = ""
    private var gambarBarang: String? = null
    private var namaKategori: String? = null

    private var selectedKodeKategori: Int? = null
    private var selectedGambarUri: Uri? = null
    private lateinit var selectGambarLauncher: ActivityResultLauncher<Intent>
    private var kategoriList: List<Kategori> = listOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_barang)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        findViewById<ImageButton>(R.id.btnKembali).setOnClickListener {
            finish()
        }

        getIntentData()
        initViews()
        setupSpinners()
        fillDataToForms()
        setupImagePicker()
        loadKategori()

        btnSimpan.setOnClickListener {
            updateBarang()
        }
    }

    private fun getIntentData() {
        idBarang = intent.getIntExtra("id_barang", 0)
        kodeBarang = intent.getIntExtra("kode_barang", 0)
        jenisBarang = intent.getStringExtra("jenis_barang") ?: ""
        namaBarang = intent.getStringExtra("nama_barang") ?: ""
        tanggalMasuk = intent.getStringExtra("tgl_masuk") ?: ""
        pegawai = intent.getStringExtra("pegawai") ?: ""
        jabatan = intent.getStringExtra("jabatan") ?: ""
        divisi = intent.getStringExtra("divisi") ?: ""
        status = intent.getStringExtra("status") ?: ""
        merk = intent.getStringExtra("merk") ?: ""
        type = intent.getStringExtra("type") ?: ""
        kondisi = intent.getStringExtra("kondisi") ?: ""
        catatanTambahan = intent.getStringExtra("catatan_tambahan") ?: ""
        gambarBarang = intent.getStringExtra("gambar_barang")
        namaKategori = intent.getStringExtra("nama_kategori")
        selectedKodeKategori = intent.getIntExtra("kode_kategori", 0)
    }

    private fun initViews() {
        etKodeBarang = findViewById(R.id.etKodeBarang)
        spinnerJenisBarang = findViewById(R.id.spinnerJenisBarang)
        etNamaBarang = findViewById(R.id.etNamaBarang)
        etTanggalMasuk = findViewById(R.id.etTanggalMasuk) // ID diperbaiki
        etPegawai = findViewById(R.id.etPegawai)
        spinnerJabatan = findViewById(R.id.spinnerJabatan)
        spinnerDivisi = findViewById(R.id.spinnerDivisi)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        etMerk = findViewById(R.id.etMerk)
        etType = findViewById(R.id.etType)
        spinnerKondisi = findViewById(R.id.spinnerKondisi)
        etCatatanTambahan = findViewById(R.id.etCatatanTambahan)
        tvNamaFileGambar = findViewById(R.id.tvNamaFileGambar)
        imageView = findViewById(R.id.imageView)
        btnSimpan = findViewById(R.id.btnSimpan)

        etTanggalMasuk.isFocusable = false
        etTanggalMasuk.isFocusableInTouchMode = false
        etTanggalMasuk.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun fillDataToForms() {
        etKodeBarang.setText(kodeBarang.toString())
        etNamaBarang.setText(namaBarang)
        etTanggalMasuk.setText(tanggalMasuk)
        etPegawai.setText(pegawai)
        etMerk.setText(merk)
        etType.setText(type)
        etCatatanTambahan.setText(catatanTambahan)

        val imageUrl = "${RetrofitClient.BASE_URL_UPLOADS}${gambarBarang}"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(imageView)
    }

    private fun setupSpinners() {
        val jabatanList = arrayOf("staf", "senior officer", "junior officer", "manager")
        val divisiList = arrayOf("Sistem Informasi", "Pemeliharaan", "Enjiniring", "Humas", "Operasi", "Logistik", "Sistem Informasi dan Keuangan")
        val statusList = arrayOf("sewa", "milik dinas")
        val kondisiList = arrayOf("baik", "rusak")

        setupSpinnerWithSelection(spinnerJabatan, jabatanList, jabatan)
        setupSpinnerWithSelection(spinnerDivisi, divisiList, divisi)
        setupSpinnerWithSelection(spinnerStatus, statusList, status)
        setupSpinnerWithSelection(spinnerKondisi, kondisiList, kondisi)
    }

    private fun setupSpinnerWithSelection(spinner: Spinner, data: Array<String>, selection: String) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val position = data.indexOf(selection)
        if (position >= 0) {
            spinner.setSelection(position)
        }
    }

    private fun updateBarang() {
        val updatedNamaBarang = etNamaBarang.text.toString()
        val updatedTanggalMasuk = etTanggalMasuk.text.toString()
        val updatedPegawai = etPegawai.text.toString()
        val updatedMerk = etMerk.text.toString()
        val updatedType = etType.text.toString()
        val updatedCatatan = etCatatanTambahan.text.toString()

        val updatedJenisBarang = spinnerJenisBarang.selectedItem.toString()
        val updatedJabatan = spinnerJabatan.selectedItem.toString()
        val updatedDivisi = spinnerDivisi.selectedItem.toString()
        val updatedStatus = spinnerStatus.selectedItem.toString()
        val updatedKondisi = spinnerKondisi.selectedItem.toString()

        if (updatedNamaBarang.isEmpty() || updatedTanggalMasuk.isEmpty()) {
            if (updatedNamaBarang.isEmpty()) etNamaBarang.error = "Nama barang tidak boleh kosong"
            if (updatedTanggalMasuk.isEmpty()) etTanggalMasuk.error = "Pilih tanggal masuk"
            return
        }

        Toast.makeText(this, "Menyimpan perubahan...", Toast.LENGTH_SHORT).show()

        // Logika pembuatan gambarPart yang sudah diperbaiki
        val gambarPart: MultipartBody.Part = if (selectedGambarUri != null) {
            getFilePart(selectedGambarUri!!) ?: run {
                Toast.makeText(this, "Gagal memproses gambar, silakan coba lagi.", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            MultipartBody.Part.createFormData("gambar", "")
        }

        val idBarangPart = MultipartBody.Part.createFormData("id_barang", idBarang.toString())
        val jenisBarangPart = MultipartBody.Part.createFormData("jenis_barang", updatedJenisBarang)
        val namaBarangPart = MultipartBody.Part.createFormData("nama_barang", updatedNamaBarang)
        val tanggalMasukPart = MultipartBody.Part.createFormData("tgl_masuk", updatedTanggalMasuk)
        val pegawaiPart = MultipartBody.Part.createFormData("pegawai", updatedPegawai)
        val jabatanPart = MultipartBody.Part.createFormData("jabatan", updatedJabatan)
        val divisiPart = MultipartBody.Part.createFormData("divisi", updatedDivisi)
        val statusPart = MultipartBody.Part.createFormData("status", updatedStatus)
        val merkPart = MultipartBody.Part.createFormData("merk", updatedMerk)
        val typePart = MultipartBody.Part.createFormData("type", updatedType)
        val kondisiPart = MultipartBody.Part.createFormData("kondisi", updatedKondisi)
        val catatanPart = MultipartBody.Part.createFormData("catatan_tambahan", updatedCatatan)
        val kodeKategoriPart = MultipartBody.Part.createFormData("kode_kategori", (selectedKodeKategori ?: 0).toString())

        RetrofitClient.instance.updateBarang(
            idBarangPart, jenisBarangPart, namaBarangPart, tanggalMasukPart, pegawaiPart,
            jabatanPart, divisiPart, statusPart, merkPart, typePart, kondisiPart,
            catatanPart, gambarPart, kodeKategoriPart
        ).enqueue(object : Callback<ApiResponse<Barang>> {
            override fun onResponse(call: Call<ApiResponse<Barang>>, response: Response<ApiResponse<Barang>>) {
                val result = response.body()
                if (response.isSuccessful && result != null && result.status) {
                    Toast.makeText(this@EditBarangActivity, "Berhasil memperbarui barang", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = result?.message ?: "Terjadi kesalahan"
                    Log.e("UpdateBarang", "Gagal update barang. Error: $errorBody")
                    Toast.makeText(this@EditBarangActivity, "Gagal memperbarui barang: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Barang>>, t: Throwable) {
                Log.e("UpdateBarang", "Gagal menghubungi server: ${t.message}", t)
                Toast.makeText(this@EditBarangActivity, "Gagal menghubungi server!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etTanggalMasuk.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).show()
    }

    private fun setupImagePicker() {
        selectGambarLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedGambarUri = uri
                    tvNamaFileGambar.text = getFileName(uri)
                    try {
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            imageView.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun selectGambar(view: View) {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        selectGambarLauncher.launch(intent)
    }

    private fun loadKategori() {
        RetrofitClient.instance.riwayatKategori().enqueue(object : Callback<ApiResponse<List<Kategori>>> {
            override fun onResponse(call: Call<ApiResponse<List<Kategori>>>, response: Response<ApiResponse<List<Kategori>>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    kategoriList = response.body()?.data ?: emptyList()
                    val kategoriNamaList = kategoriList.map { it.nama_kategori ?: "-" }
                    val adapter = ArrayAdapter(this@EditBarangActivity, android.R.layout.simple_spinner_item, kategoriNamaList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerJenisBarang.adapter = adapter

                    val jenisBarangPosition = kategoriNamaList.indexOf(namaKategori)
                    if (jenisBarangPosition >= 0) {
                        spinnerJenisBarang.setSelection(jenisBarangPosition)
                    }

                    spinnerJenisBarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (kategoriList.isNotEmpty() && position < kategoriList.size) {
                                selectedKodeKategori = kategoriList[position].kode_kategori
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                } else {
                    Toast.makeText(this@EditBarangActivity, "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Kategori>>>, t: Throwable) {
                Toast.makeText(this@EditBarangActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFileName(uri: Uri): String {
        var result = "Unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        }
        return result
    }

    private fun getFilePart(uri: Uri): MultipartBody.Part? {
        return try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return null
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val file = File(cacheDir, getFileName(uri))
            file.outputStream().use { output -> inputStream.copyTo(output) }
            val mimeType = contentResolver.getType(uri) ?: "image/*"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gambar", file.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}