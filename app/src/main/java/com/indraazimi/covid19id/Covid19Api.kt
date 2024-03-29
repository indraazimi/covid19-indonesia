/*
 * Copyright (c) 2021 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk kelas Pemrograman untuk Perangkat Bergerak 2.
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.covid19id

import com.indraazimi.covid19id.model.Data
import com.indraazimi.covid19id.model.ListProvinsi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

enum class ApiStatus { LOADING, SUCCESS, FAILED }

object Covid19Api {

    private const val BASE_URL = "https://data.covid19.go.id/public/api/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val service : ApiService by lazy { retrofit.create(ApiService::class.java) }

    interface ApiService {
        @GET("update.json")
        suspend fun getData(): Data

        @GET("prov.json")
        suspend fun getProvinsi(): ListProvinsi
    }
}