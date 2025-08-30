package com.example.indonesiapower.model

data class Pemeliharaan(
    val id_pemeliharaan: Int,
    val kode_barang: Int,
    val nama_barang: String?,
    val jenis_barang: String?,
    val nama_kategori: String?,
    val pegawai: String?,
    val jabatan: String?,
    val divisi: String?,
    val tgl_barang_masuk: String?,
    val kondisi_perangkat_terakhir: String?,
    val catatan_tambahan: String?,
    val petugas: String?,
    val tgl_pemeliharaan_selanjutnya: String?,
    val status: String,
    val merk: String?,
    val type: String?,
    val kondisi: String?,
    val gambar_barang: String?
)
