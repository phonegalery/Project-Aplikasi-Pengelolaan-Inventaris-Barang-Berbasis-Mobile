package com.example.indonesiapower.ui.pengelola

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.indonesiapower.R
import com.example.indonesiapower.api.RetrofitClient
import com.example.indonesiapower.model.Pengelola
import com.example.indonesiapower.ui.pengelola.edit.EditPengelolaActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPengelolaAdapter(
    private var pengelolaList: List<Pengelola>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatPengelolaAdapter.PengelolaViewHolder>() {

    class PengelolaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvNip: TextView = itemView.findViewById(R.id.tvNip)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    fun updateData(newList: List<Pengelola>) {
        pengelolaList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengelolaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_pengelola, parent, false)
        return PengelolaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PengelolaViewHolder, position: Int) {
        val pengelola = pengelolaList[position]
        val context = holder.itemView.context

        holder.tvNama.text = pengelola.nama ?: "-"
        holder.tvNip.text = "NIP: ${pengelola.nip ?: "-"}"
        holder.tvUsername.text = "Username: ${pengelola.username ?: "-"}"

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditPengelolaActivity::class.java).apply {
                putExtra("id", pengelola.id)
                putExtra("nip", pengelola.nip)
                putExtra("nama", pengelola.nama)
                putExtra("username", pengelola.username)
                putExtra("password", pengelola.password)
            }
            startForResult.launch(intent)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, pengelola.id)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, pengelolaId: Int?) {
        if (pengelolaId == null) return

        AlertDialog.Builder(context)
            .setTitle("Hapus Pengelola")
            .setMessage("Apakah Anda yakin ingin menghapus pengelola ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deletePengelola(pengelolaId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deletePengelola(pengelolaId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_pengelola", pengelolaId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deletePengelola(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pengelola berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus pengelola", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = pengelolaList.size
}
