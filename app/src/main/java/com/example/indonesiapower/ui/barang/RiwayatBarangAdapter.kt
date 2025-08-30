package com.example.indonesiapower.ui.barang

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
import com.bumptech.glide.Glide
import com.example.indonesiapower.R
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Barang
import com.example.indonesiapower.ui.barang.detail.DetailBarangActivity
import com.example.indonesiapower.ui.barang.edit.EditBarangActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatBarangAdapter(
    private var barangList: List<Barang>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatBarangAdapter.BarangViewHolder>() {

    class BarangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvPegawai: TextView = itemView.findViewById(R.id.tvPegawai)
        val ivMedia: ImageView = itemView.findViewById(R.id.ivMedia)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
        val cardView: View = itemView.findViewById(R.id.cardView)
    }

    fun updateData(newList: List<Barang>) {
        barangList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_barang, parent, false)
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = barangList[position]
        val context = holder.itemView.context

        holder.tvJudul.text = barang.nama_barang ?: "Tanpa Nama"
        holder.tvKategori.text = "Kategori: ${barang.nama_kategori ?: "Tidak Diketahui"}"
        holder.tvPegawai.text = "Pegawai: ${barang.pegawai ?: "Tidak Diketahui"}"
        holder.tvStatus.text = barang.kondisi ?: "Tidak diketahui"

        val statusColor = when (barang.kondisi?.lowercase()) {
            "baik" -> R.color.badge_success
            "rusak" -> R.color.badge_warning
            else -> R.color.badge_secondary
        }
        holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, statusColor)

        val imageUrl = barang.gambar_barang?.let {
            "${RetrofitClient.BASE_URL_UPLOADS}$it"
        }

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.ivMedia)

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditBarangActivity::class.java).apply {
                putExtra("id_barang", barang.id)
                putExtra("kode_barang", barang.kode_barang)
                putExtra("jenis_barang", barang.jenis_barang)
                putExtra("nama_barang", barang.nama_barang)
                putExtra("tgl_masuk", barang.tgl_masuk)
                putExtra("pegawai", barang.pegawai)
                putExtra("jabatan", barang.jabatan)
                putExtra("divisi", barang.divisi)
                putExtra("status", barang.status)
                putExtra("merk", barang.merk)
                putExtra("type", barang.type)
                putExtra("catatan_tambahan", barang.catatan_tambahan)
                putExtra("kondisi", barang.kondisi)
                putExtra("gambar_barang", barang.gambar_barang)
                putExtra("kode_kategori", barang.kode_kategori)
                putExtra("nama_kategori", barang.nama_kategori)
            }
            startForResult.launch(intent)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, barang.id)
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(context, DetailBarangActivity::class.java).apply {
                putExtra("id_barang", barang.id)
                putExtra("kode_barang", barang.kode_barang)
                putExtra("jenis_barang", barang.jenis_barang)
                putExtra("nama_barang", barang.nama_barang)
                putExtra("tgl_masuk", barang.tgl_masuk)
                putExtra("pegawai", barang.pegawai)
                putExtra("jabatan", barang.jabatan)
                putExtra("divisi", barang.divisi)
                putExtra("status", barang.status)
                putExtra("merk", barang.merk)
                putExtra("type", barang.type)
                putExtra("catatan_tambahan", barang.catatan_tambahan)
                putExtra("kondisi", barang.kondisi)
                putExtra("gambar_barang", barang.gambar_barang)
                putExtra("kode_kategori", barang.kode_kategori)
                putExtra("nama_kategori", barang.nama_kategori)
            }
            startForResult.launch(intent)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, barangId: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Barang")
            .setMessage("Apakah Anda yakin ingin menghapus barang ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteBarang(barangId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteBarang(barangId: Int, context: Context) {
        val jsonObject = JSONObject()
        jsonObject.put("id_barang", barangId)

        val requestBody: RequestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deleteBarang(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Barang berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = barangList.size
}
