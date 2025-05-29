package br.senai.sp.jandira.bairronews.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitFactory {

    private const val BASE_URL = "http://10.107.144.9:8080/v1/bairro-news/"

    private val gson = GsonBuilder()
        .serializeNulls()
        .create()

    private val retrofitFactory = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getUserService(): UserService {
        return retrofitFactory.create(UserService::class.java)
    }
}
