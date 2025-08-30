package com.example.indonesiapower.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.indonesiapower.databinding.FragmentDashboardBinding
import com.example.indonesiapower.ui.admin.RiwayatAdminActivity
import com.example.indonesiapower.ui.barang.RiwayatBarangActivity
import com.example.indonesiapower.ui.kategori.RiwayatKategoriActivity
import com.example.indonesiapower.ui.pemeliharaan.RiwayatPemeliharaanActivity
import com.example.indonesiapower.ui.penerimaan.RiwayatPenerimaanActivity
import com.example.indonesiapower.ui.pengelola.RiwayatPengelolaActivity
import com.example.indonesiapower.ui.petugas.RiwayatPetugasActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Card View Dashboard
        binding.cardViewBarang.visibility = View.GONE
        binding.cardViewPemeliharaan.visibility = View.GONE
        binding.cardViewKategori.visibility = View.GONE
        binding.cardViewPenerimaan.visibility = View.GONE
        binding.cardViewManajemenAdmin.visibility = View.GONE
        binding.cardViewDataPetugas.visibility = View.GONE
        binding.cardViewDataPengelola.visibility = View.GONE


        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()

        when (level) {
            "admin" -> {
                // Barang
                binding.cardViewBarang.visibility = View.VISIBLE
                binding.cardViewBarang.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatBarangActivity::class.java)
                    startActivity(intent)
                }

                // Pemeliharaan
                binding.cardViewPemeliharaan.visibility = View.VISIBLE
                binding.cardViewPemeliharaan.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPemeliharaanActivity::class.java)
                    startActivity(intent)
                }

                // Kategori
                binding.cardViewKategori.visibility = View.VISIBLE
                binding.cardViewKategori.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatKategoriActivity::class.java)
                    startActivity(intent)
                }

                // Penerimaan
                binding.cardViewPenerimaan.visibility = View.VISIBLE
                binding.cardViewPenerimaan.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPenerimaanActivity::class.java)
                    startActivity(intent)
                }

                // Admin
                binding.cardViewManajemenAdmin.visibility = View.VISIBLE
                binding.cardViewManajemenAdmin.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatAdminActivity::class.java)
                    startActivity(intent)
                }

                // Petugas
                binding.cardViewDataPetugas.visibility = View.VISIBLE
                binding.cardViewDataPetugas.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPetugasActivity::class.java)
                    startActivity(intent)
                }

                // Pengelola
                binding.cardViewDataPengelola.visibility = View.VISIBLE
                binding.cardViewDataPengelola.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPengelolaActivity::class.java)
                    startActivity(intent)
                }
            }
            "petugas" -> {
                // Pemeliharaan
                binding.cardViewPemeliharaan.visibility = View.VISIBLE
                binding.cardViewPemeliharaan.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPemeliharaanActivity::class.java)
                    startActivity(intent)
                }
            }

            "pengelola" -> {
                binding.cardViewBarang.visibility = View.VISIBLE
                binding.cardViewBarang.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatBarangActivity::class.java)
                    startActivity(intent)
                }

                // Kategori
                binding.cardViewKategori.visibility = View.VISIBLE
                binding.cardViewKategori.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatKategoriActivity::class.java)
                    startActivity(intent)
                }

                binding.cardViewPenerimaan.visibility = View.VISIBLE
                binding.cardViewPenerimaan.setOnClickListener {
                    val intent = Intent(requireContext(), RiwayatPenerimaanActivity::class.java)
                    startActivity(intent)
                }

            }

        }

        return root
    }

    private fun getLevelFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level", "0")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}