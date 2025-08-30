package com.example.indonesiapower.model

data class Barang(
    val id: Int,
    val kode_barang: Int,
    val jenis_barang: String?,
    val nama_barang: String?,
    val tgl_masuk: String?,
    val pegawai: String?,
    val jabatan: String?,
    val divisi: String?,
    val status: String,
    val merk: String?,
    val type: String?,
    val catatan_tambahan: String?,
    val kondisi: String?,
    val gambar_barang: String?,
    val kode_kategori: Int?,
    val nama_kategori: String?
)