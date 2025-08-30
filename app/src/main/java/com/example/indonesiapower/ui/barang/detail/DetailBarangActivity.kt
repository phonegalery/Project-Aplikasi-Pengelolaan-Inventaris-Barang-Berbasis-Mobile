package com.example.indonesiapower.ui.barang.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.indonesiapower.R
import com.example.indonesiapower.api.RetrofitClient

class DetailBarangActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_barang)
        supportActionBar?.hide()

        // Set status bar color dan mode light
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Ambil data dari intent
        val idBarang = intent.getIntExtra("id_barang", 0)
        val kodeBarang = intent.getIntExtra("kode_barang", 0)
        val jenisBarang = intent.getStringExtra("jenis_barang")
        val namaBarang = intent.getStringExtra("nama_barang")
        val tanggalMasuk = intent.getStringExtra("tgl_masuk")
        val pegawai = intent.getStringExtra("pegawai")
        val jabatan = intent.getStringExtra("jabatan")
        val divisi = intent.getStringExtra("divisi")
        val status = intent.getStringExtra("status")
        val merk = intent.getStringExtra("merk")
        val type = intent.getStringExtra("type")
        val catatanTambahan = intent.getStringExtra("catatan_tambahan")
        val kondisi = intent.getStringExtra("kondisi")
        val gambarBarang = intent.getStringExtra("gambar_barang")
        val namaKategori = intent.getStringExtra("nama_kategori")

        val tvJudul = findViewById<TextView>(R.id.tvJudul)
        val tvKodeBarang = findViewById<TextView>(R.id.tvKodeBarang)
        val tvJenis = findViewById<TextView>(R.id.tvJenis)
        val tvPegawai = findViewById<TextView>(R.id.tvPegawai)
        val tvTanggalMasuk = findViewById<TextView>(R.id.tvTanggalMasuk)
        val tvJabatan = findViewById<TextView>(R.id.tvJabatan)
        val tvDivisi = findViewById<TextView>(R.id.tvDivisi)
        val tvStatusBarang = findViewById<TextView>(R.id.tvStatusBarang)
        val tvMerk = findViewById<TextView>(R.id.tvMerk)
        val tvType = findViewById<TextView>(R.id.tvType)
        val tvKondisi = findViewById<TextView>(R.id.tvKondisi)
        val tvCatatan = findViewById<TextView>(R.id.tvCatatan)
        val imageView = findViewById<ImageView>(R.id.imageView)

        tvJudul.text = namaBarang
        tvKodeBarang.text = kodeBarang.toString()
        tvJenis.text = jenisBarang
        tvPegawai.text = pegawai
        tvTanggalMasuk.text = tanggalMasuk
        tvJabatan.text = jabatan
        tvDivisi.text = divisi
        tvStatusBarang.text = status
        tvMerk.text = merk
        tvType.text = type
        tvKondisi.text = kondisi
        tvCatatan.text = catatanTambahan

        // Load gambar (gunakan Glide atau Picasso)
        Glide.with(this)
            .load("${RetrofitClient.BASE_URL_UPLOADS}${gambarBarang}")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imageView)
    }
}
