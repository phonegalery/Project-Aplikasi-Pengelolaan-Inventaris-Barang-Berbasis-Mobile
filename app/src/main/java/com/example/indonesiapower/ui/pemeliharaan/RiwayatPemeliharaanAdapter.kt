package com.example.indonesiapower.ui.pemeliharaan

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.indonesiapower.R
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Pemeliharaan
import com.example.indonesiapower.ui.pemeliharaan.edit.EditPemeliharaanActivity
import com.example.indonesiapower.utils.DateUtils.formatTanggalIndonesia
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPemeliharaanAdapter(
    private var pemeliharaanList: List<Pemeliharaan>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatPemeliharaanAdapter.PemeliharaanViewHolder>() {

    class PemeliharaanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvKodeBarang: TextView = itemView.findViewById(R.id.tvKodeBarang)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvPegawai: TextView = itemView.findViewById(R.id.tvPegawai)
        val tvJabatan: TextView = itemView.findViewById(R.id.tvJabatan)
        val tvDivisi: TextView = itemView.findViewById(R.id.tvDivisi)
        val tvTglMasuk: TextView = itemView.findViewById(R.id.tvTglMasuk)
        val tvTglPemeliharaan: TextView = itemView.findViewById(R.id.tvTglPemeliharaan)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    fun updateData(newList: List<Pemeliharaan>) {
        pemeliharaanList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PemeliharaanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_pemeliharaan, parent, false)
        return PemeliharaanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PemeliharaanViewHolder, position: Int) {
        val pemeliharaan = pemeliharaanList[position]
        val context = holder.itemView.context

        // Set data to views
        holder.tvNamaBarang.text = pemeliharaan.nama_barang ?: "Tanpa Nama"
        holder.tvKodeBarang.text = pemeliharaan.kode_barang.toString()
        holder.tvPegawai.text = pemeliharaan.pegawai ?: "-"
        holder.tvJabatan.text = pemeliharaan.jabatan ?: "-"
        holder.tvDivisi.text = pemeliharaan.divisi ?: "-"
        holder.tvTglMasuk.text = formatTanggalIndonesia(pemeliharaan.tgl_barang_masuk)
        holder.tvTglPemeliharaan.text = formatTanggalIndonesia(pemeliharaan.tgl_pemeliharaan_selanjutnya)

        // Set status with color
        holder.tvStatus.text = pemeliharaan.kondisi ?: "Tidak diketahui"
        val statusColor = when (pemeliharaan.kondisi?.lowercase()) {
            "Sudah Diperbaiki" -> R.color.badge_success
            "Rusak" -> R.color.badge_danger
            "Butuh Perbaikan" -> R.color.badge_warning
            else -> R.color.badge_secondary
        }
        holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, statusColor)

        // Edit button click
        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditPemeliharaanActivity::class.java).apply {
                putExtra("id_pemeliharaan", pemeliharaan.id_pemeliharaan)
                putExtra("kode_barang", pemeliharaan.kode_barang)
                putExtra("nama_barang", pemeliharaan.nama_barang)
                putExtra("jenis_barang", pemeliharaan.jenis_barang)
                putExtra("pegawai", pemeliharaan.pegawai)
                putExtra("jabatan", pemeliharaan.jabatan)
                putExtra("divisi", pemeliharaan.divisi)
                putExtra("tgl_barang_masuk", pemeliharaan.tgl_barang_masuk)
                putExtra("kondisi", pemeliharaan.kondisi)
                putExtra("catatan_tambahan", pemeliharaan.catatan_tambahan)
                putExtra("tgl_pemeliharaan_selanjutnya", pemeliharaan.tgl_pemeliharaan_selanjutnya)
            }
            startForResult.launch(intent)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, pemeliharaan.id_pemeliharaan)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, pemeliharaanId: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Pemeliharaan")
            .setMessage("Apakah Anda yakin ingin menghapus data pemeliharaan ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deletePemeliharaan(pemeliharaanId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deletePemeliharaan(pemeliharaanId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_pemeliharaan", pemeliharaanId)
        }

        val requestBody: RequestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deletePemeliharaan(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pemeliharaan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus pemeliharaan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = pemeliharaanList.size
}