package com.example.indonesiapower.ui.kategori

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
import com.example.indonesiapower.model.Kategori
import com.example.indonesiapower.ui.kategori.edit.EditKategoriActivity
import com.example.indonesiapower.utils.DateUtils.formatTanggalIndonesia
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatKategoriAdapter(
    private var kategoriList: List<Kategori>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatKategoriAdapter.KategoriViewHolder>() {

    class KategoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaKategori: TextView = itemView.findViewById(R.id.tvNamaKategori)
        val tvKodeKategori: TextView = itemView.findViewById(R.id.tvKodeKategori)
        val tvJumlahKategori: TextView = itemView.findViewById(R.id.tvJumlahKategori)
        val tvTanggalKategori: TextView = itemView.findViewById(R.id.tvTanggalKategori)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEditKategori)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteKategori)
    }

    fun updateData(newList: List<Kategori>) {
        kategoriList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_kategori, parent, false)
        return KategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val kategori = kategoriList[position]
        val context = holder.itemView.context

        holder.tvNamaKategori.text = kategori.nama_kategori ?: "Tanpa Nama"
        holder.tvKodeKategori.text = kategori.kode_kategori?.let { "KTG-$it" } ?: "-"
        holder.tvJumlahKategori.text = kategori.jumlah?.toString() ?: "0"
        holder.tvTanggalKategori.text = kategori.dibuat_tgl?.let { formatTanggalIndonesia(it) } ?: "-"

        // Tombol Edit
        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditKategoriActivity::class.java).apply {
                putExtra("id_kategori", kategori.id)
                putExtra("kode_kategori", kategori.kode_kategori)
                putExtra("nama_kategori", kategori.nama_kategori)
                putExtra("jumlah", kategori.jumlah)
                putExtra("dibuat_tgl", kategori.dibuat_tgl)
            }
            startForResult.launch(intent)
        }

        // Tombol Hapus
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, kategori.id)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, kategoriId: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteKategori(kategoriId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deleteKategori(kategoriId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_kategori", kategoriId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deleteKategori(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = kategoriList.size
}
