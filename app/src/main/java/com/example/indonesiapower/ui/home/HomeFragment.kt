package com.example.indonesiapower.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.indonesiapower.databinding.FragmentHomeBinding
import com.example.indonesiapower.model.TotalData
import com.example.indonesiapower.api.ApiResponse
import com.example.indonesiapower.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Variabel namaUser tidak lagi diperlukan di sini, kita akan tangani langsung di onViewCreated

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Kode yang sebelumnya ada di sini tidak akan berjalan karena berada setelah 'return'.
        // onCreateView HANYA boleh berisi inisialisasi binding dan return view-nya.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === PERUBAHAN DIMULAI DI SINI ===

        // 1. Panggil fungsi untuk mengambil nama dari SharedPreferences.
        val namaDariPrefs = getNamaFromSharedPreferences()

        // 2. Buat pesan selamat datang yang lengkap.
        val welcomeMessage = "Selamat Datang, $namaDariPrefs"

        // 3. Atur teks pada TextView. Ini adalah tempat yang benar untuk memanipulasi view.
        binding.tvNama.text = welcomeMessage

        // === AKHIR PERUBAHAN ===


        // Panggil fungsi untuk mengambil data rekap dari API (kode Anda yang sudah ada).
        fetchTotalData()
    }

    private fun fetchTotalData() {
        RetrofitClient.instance.totalData().enqueue(object : Callback<ApiResponse<TotalData>> {
            override fun onResponse(
                call: Call<ApiResponse<TotalData>>,
                response: Response<ApiResponse<TotalData>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()?.data
                    data?.let {
                        binding.tvJumlahAdmin.text = it.total_admin.toString()
                        binding.tvJumlahPetugas.text = it.total_petugas.toString()

                        binding.tvJumlahPengelola.text = it.total_pengelola.toString()
                        binding.tvJumlahBarang.text = it.total_barang.toString()
                        binding.tvJumlahKategori.text = it.total_kategori.toString()
                        binding.tvJumlahPemeliharaan.text = it.total_pemeliharaan.toString()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data rekap", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<TotalData>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getNamaFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        // Saya ubah nilai default dari "0" menjadi "Pengguna" agar lebih baik jika nama tidak ditemukan.
        // Operator '?:' memastikan kita tidak pernah mengembalikan nilai null.
        return sharedPreferences.getString("nama", "Pengguna") ?: "Pengguna"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}