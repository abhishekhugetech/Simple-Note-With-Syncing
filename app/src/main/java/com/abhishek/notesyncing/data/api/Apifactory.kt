package com.abhishek.notesyncing.data.api

import com.abhishek.notesyncing.data.model.SupportInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object Apifactory{

    val inter = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    //OkHttpClient for building http request url
    private val noteClient = OkHttpClient().newBuilder()
        .addInterceptor(inter)
        .build()


    fun retrofit() : Retrofit = Retrofit.Builder()
        .client(noteClient)
        .baseUrl("http://YOUR_API_URL:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val notesService : MagtappService = retrofit().create(MagtappService::class.java)

}
