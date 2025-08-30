package com.example.indonesiapower.ui.penerimaan

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
import androidx.recyclerview.widget.RecyclerView
import com.example.indonesiapower.R
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Penerimaan // Pastikan lokasi model Penerimaan benar
import com.example.indonesiapower.ui.penerimaan.edit.EditPenerimaanActivity
import com.example.indonesiapower.utils.DateUtils.formatTanggalIndonesia
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


// Lokasi: file ui/penerimaan/RiwayatPenerimaanAdapter.kt
class RiwayatPenerimaanAdapter(
    private var penerimaanList: List<Penerimaan>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatPenerimaanAdapter.PenerimaanViewHolder>() {

    class PenerimaanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvNoTerima: TextView = itemView.findViewById(R.id.tvNoTerima)
        val tvJenisBarang: TextView = itemView.findViewById(R.id.tvJenisBarang)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlah)
        val tvTglTerima: TextView = itemView.findViewById(R.id.tvTglTerima)
        val tvJamTerima: TextView = itemView.findViewById(R.id.tvJamTerima)
        val tvSupplier: TextView = itemView.findViewById(R.id.tvSupplier)
        val tvCatatanTambahan: TextView = itemView.findViewById(R.id.tvCatatanTambahan)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    fun updateData(newList: List<Penerimaan>) {
        penerimaanList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PenerimaanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_penerimaan, parent, false)
        return PenerimaanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PenerimaanViewHolder, position: Int) {
        val penerimaan = penerimaanList[position]
        val context = holder.itemView.context

        // Set data to views
        holder.tvNamaBarang.text = penerimaan.nama_barang ?: "Tanpa Nama"
        holder.tvNoTerima.text = penerimaan.no_terima ?: "-"
        holder.tvJenisBarang.text = penerimaan.jenis_barang ?: "-"
        holder.tvJumlah.text = penerimaan.jumlah?.toString() ?: "-"
        holder.tvTglTerima.text = formatTanggalIndonesia(penerimaan.tgl_terima)
        holder.tvJamTerima.text = penerimaan.jam_terima ?: "-"
        holder.tvSupplier.text = penerimaan.supplier ?: "-"
        holder.tvCatatanTambahan.text = penerimaan.catatan_tambahan ?: "-"

        // [PERUBAHAN DI SINI] Edit button click dengan validasi ID
        holder.btnEdit.setOnClickListener {
            val penerimaanId = penerimaan.id
            // Cek apakah ID null atau tidak valid (misal: 0)
            if (penerimaanId == null || penerimaanId <= 0) {
                Toast.makeText(context, "ID data tidak valid, tidak bisa diedit.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Hentikan eksekusi jika ID tidak valid
            }

            // Jika ID valid, lanjutkan proses
            val intent = Intent(context, EditPenerimaanActivity::class.java).apply {
                putExtra("penerimaanId", penerimaanId) // Gunakan ID yang sudah divalidasi
                putExtra("no_terima", penerimaan.no_terima)
                putExtra("nama_barang", penerimaan.nama_barang)
                putExtra("jenis_barang", penerimaan.jenis_barang)
                putExtra("jumlah", penerimaan.jumlah)
                putExtra("tgl_terima", penerimaan.tgl_terima)
                putExtra("jam_terima", penerimaan.jam_terima)
                putExtra("supplier", penerimaan.supplier)
                putExtra("catatan_tambahan", penerimaan.catatan_tambahan)
            }
            startForResult.launch(intent)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, penerimaan.id)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, penerimaanId: Int?) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Penerimaan")
            .setMessage("Apakah Anda yakin ingin menghapus data penerimaan ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                penerimaanId?.let { id ->
                    deletePenerimaan(id, context)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deletePenerimaan(penerimaanId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id", penerimaanId)
        }

        val requestBody: RequestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deletePenerimaan(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Penerimaan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus penerimaan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = penerimaanList.size
}