package com.example.indonesiapower.api

import com.example.indonesiapower.model.Admin
import com.example.indonesiapower.model.Barang
import com.example.indonesiapower.model.Kategori
import com.example.indonesiapower.model.Pemeliharaan
import com.example.indonesiapower.model.Penerimaan
import com.example.indonesiapower.model.Pengelola
import com.example.indonesiapower.model.Petugas
import com.example.indonesiapower.model.TotalData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    //////////////////// Home ////////////////////
    @GET("get_total_data.php")
    fun totalData(): Call<ApiResponse<TotalData>>


    //////////////////// Login ////////////////////
    @POST("login.php")
    fun loginUser(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Barang CRUD ////////////////////
    @GET("get_all_barang.php")
    fun riwayatBarang(): Call<ApiResponse<List<Barang>>>

    @GET("get_otomatis_kode_barang.php")
    fun kodeBarangOtomatis(): Call<ApiResponse<Barang>>

    @Multipart
    @POST("create_barang.php")
    fun tambahBarang(
        @Part kode_kategori: MultipartBody.Part,
        @Part kode_barang: MultipartBody.Part,
        @Part jenis_barang: MultipartBody.Part,
        @Part nama_barang: MultipartBody.Part,
        @Part tgl_masuk: MultipartBody.Part,
        @Part pegawai: MultipartBody.Part,
        @Part jabatan: MultipartBody.Part,
        @Part divisi: MultipartBody.Part,
        @Part status: MultipartBody.Part,
        @Part merk: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part kondisi: MultipartBody.Part,
        @Part catatan_tambahan: MultipartBody.Part,
        @Part gambar: MultipartBody.Part
    ): Call<ApiResponse<Barang>>
    // BENAR
    @Multipart
    @POST("update_barang.php")
    fun updateBarang(
        @Part id_barang: MultipartBody.Part,
        @Part jenis_barang: MultipartBody.Part,
        @Part nama_barang: MultipartBody.Part,
        @Part tgl_masuk: MultipartBody.Part,
        @Part pegawai: MultipartBody.Part,
        @Part jabatan: MultipartBody.Part,
        @Part divisi: MultipartBody.Part,
        @Part status: MultipartBody.Part,
        @Part merk: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part kondisi: MultipartBody.Part,
        @Part catatan_tambahan: MultipartBody.Part, // Pastikan ini juga benar
        @Part gambar: MultipartBody.Part,         // <--- UBAH MENJADI INI
        @Part kode_kategori: MultipartBody.Part
    ): Call<ApiResponse<Barang>>

    @POST("delete_barang.php")
    fun deleteBarang(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Pemeliharaan CRUD ////////////////////
    @GET("get_all_pemeliharaan.php")
    fun riwayatPemeliharaan(): Call<ApiResponse<List<Pemeliharaan>>>

    @Headers("Content-Type: application/json")
    @POST("create_pemeliharaan.php")
    fun tambahPemeliharaan(@Body requestBody: RequestBody): Call<ApiResponse<Pemeliharaan>>

    @POST("get_barang_by_kode.php")
    fun getBarangByKode(@Body body: RequestBody): Call<ApiResponse<List<Barang>>>

    @Headers("Content-Type: application/json")
    @POST("update_pemeliharaan.php")
    fun editPemeliharaan(@Body requestBody: RequestBody): Call<ApiResponse<Pemeliharaan>>

    @POST("delete_pemeliharaan.php")
    fun deletePemeliharaan(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Penerimaan CRUD ////////////////////
    @GET("get_all_penerimaan.php")
    fun riwayatPenerimaan(): Call<ApiResponse<List<Penerimaan>>>

    // [DIPERBAIKI] Menambahkan anotasi @Headers dan @POST yang hilang
    @Headers("Content-Type: application/json")
    @POST("create_penerimaan.php") // Pastikan nama endpoint ini benar sesuai API Anda
    fun tambahPenerimaan(@Body requestBody: RequestBody): Call<ApiResponse<Penerimaan>>

    @POST("update_penerimaan.php")
    fun editPenerimaan(@Body requestBody: RequestBody): Call<ApiResponse<Penerimaan>>


    @POST("delete_penerimaan.php")
    fun deletePenerimaan(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Kategori CRUD ////////////////////
    @GET("get_all_kategori.php")
    fun riwayatKategori(): Call<ApiResponse<List<Kategori>>>

    @Headers("Content-Type: application/json")
    @POST("create_kategori.php")
    fun tambahKategori(@Body requestBody: RequestBody): Call<ApiResponse<Kategori>>

    @Headers("Content-Tipe: application/json")
    @POST("update_kategori.php")
    fun editKategori(@Body requestBody: RequestBody): Call<ApiResponse<Kategori>>

    @POST("delete_kategori.php")
    fun deleteKategori(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Admin CRUD ////////////////////
    @GET("get_all_admin.php")
    fun riwayatAdmin(): Call<ApiResponse<List<Admin>>>

    @Headers("Content-Type: application/json")
    @POST("create_admin.php")
    fun tambahAdmin(@Body requestBody: RequestBody): Call<ApiResponse<Admin>>

    @Headers("Content-Type: application/json")
    @POST("update_admin.php")
    fun editAdmin(@Body requestBody: RequestBody): Call<ApiResponse<Admin>>

    @POST("delete_admin.php")
    fun deleteAdmin(@Body body: RequestBody): Call<ResponseBody>


    //////////////////// Petugas CRUD ////////////////////
    @GET("get_all_petugas.php")
    fun riwayatPetugas(): Call<ApiResponse<List<Petugas>>>

    @Headers("Content-Type: application/json")
    @POST("create_petugas.php")
    fun tambahPetugas(@Body requestBody: RequestBody): Call<ApiResponse<Petugas>>

    @Headers("Content-Type: application/json")
    @POST("update_petugas.php")
    fun editPetugas(@Body requestBody: RequestBody): Call<ApiResponse<Petugas>>

    @POST("delete_petugas.php")
    fun deletePetugas(@Body body: RequestBody): Call<ResponseBody>


    @GET("get_all_pengelola.php")
    fun riwayatPengelola(): Call<ApiResponse<List<Pengelola>>>

    @Headers("Content-Type: application/json")
    @POST("create_pengelola.php")
    fun tambahPengelola(@Body requestBody: RequestBody): Call<ApiResponse<Pengelola>>

    @Headers("Content-Type: application/json")
    @POST("update_pengelola.php")
    fun editPengelola(@Body requestBody: RequestBody): Call<ApiResponse<Pengelola>>

    @POST("delete_pengelola.php")
    fun deletePengelola(@Body body: RequestBody): Call<ResponseBody>




}