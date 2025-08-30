package com.example.indonesiapower.ui.petugas

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
import com.example.indonesiapower.model.Petugas
import com.example.indonesiapower.ui.petugas.edit.EditPetugasActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatPetugasAdapter(
    private var petugasList: List<Petugas>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onDeleteSuccess: () -> Unit
) : RecyclerView.Adapter<RiwayatPetugasAdapter.PetugasViewHolder>() {

    class PetugasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvNip: TextView = itemView.findViewById(R.id.tvNip)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    fun updateData(newList: List<Petugas>) {
        petugasList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetugasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_petugas, parent, false)
        return PetugasViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetugasViewHolder, position: Int) {
        val petugas = petugasList[position]
        val context = holder.itemView.context

        holder.tvNama.text = petugas.nama ?: "-"
        holder.tvNip.text = "NIP: ${petugas.nip ?: "-"}"
        holder.tvUsername.text = "Username: ${petugas.username ?: "-"}"

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditPetugasActivity::class.java).apply {
                putExtra("id", petugas.id)
                putExtra("nip", petugas.nip)
                putExtra("nama", petugas.nama)
                putExtra("username", petugas.username)
                putExtra("password", petugas.password)
            }
            startForResult.launch(intent)
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(context, petugas.id)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, petugasId: Int?) {
        if (petugasId == null) return

        AlertDialog.Builder(context)
            .setTitle("Hapus Petugas")
            .setMessage("Apakah Anda yakin ingin menghapus petugas ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deletePetugas(petugasId, context)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun deletePetugas(petugasId: Int, context: Context) {
        val jsonObject = JSONObject().apply {
            put("id_petugas", petugasId)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        RetrofitClient.instance.deletePetugas(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Petugas berhasil dihapus", Toast.LENGTH_SHORT).show()
                    onDeleteSuccess.invoke()
                } else {
                    Toast.makeText(context, "Gagal menghapus petugas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = petugasList.size
}
