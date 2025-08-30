package com.example.indonesiapower.model

data class Penerimaan(
    val id: Int,
    val no_terima: String?,
    val nama_barang: String?,
    val jenis_barang: String?,
    val jumlah: Int?,
    val tgl_terima: String?,
    val jam_terima: String?,
    val supplier: String?,
    val catatan_tambahan: String? 
)
