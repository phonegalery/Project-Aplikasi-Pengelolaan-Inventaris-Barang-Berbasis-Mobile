package com.example.indonesiapower.ui.admin

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
import com.example.indonesiapower.model.Admin
import com.example.indonesiapower.ui.admin.edit.EditAdminActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatAdminAdapter(
    private var adminList: List<Admin>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatAdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvNip: TextView = itemView.findViewById(R.id.tvNip)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    fun updateData(newList: List<Admin>) {
        adminList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = adminList[position]
        val context = holder.itemView.context

        // Set data to views
        holder.tvNama.text = admin.nama ?: "-"
        holder.tvNip.text = "NIP: ${admin.nip ?: "-"}"
        holder.tvUsername.text = "Username: ${admin.username ?: "-"}"

        // Edit button
        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditAdminActivity::class.java).apply {
                putExtra("id", admin.id)
                putExtra("nip", admin.nip)
                putExtra("nama", admin.nama)
                putExtra("username", admin.username)
                putExtra("password", admin.password)
            }
            startForResult.launch(intent)
        }

        // Delete button
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, admin.id)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, adminId: Int?) {
        if (adminId == null) return

        AlertDialog.Builder(context)
            .setTitle("Hapus Admin")
            .setMessage("Apakah Anda yakin ingin menghapus admin ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deleteAdmin(adminId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deleteAdmin(adminId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_admin", adminId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deleteAdmin(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Admin berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus admin", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = adminList.size
}
