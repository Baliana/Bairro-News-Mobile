package br.senai.sp.jandira.bairronews.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
class RetrofitFactory {

    private val BASE_URL = "http://10.107.134.32:8080/v1/bairro-news/"
    private val gson by lazy {
        GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()
    }

    private val retrofitFactory by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getUserService(): UserService {
        return retrofitFactory.create(UserService::class.java)
    }

    fun getNoticiaService(): NoticiaService {
        return retrofitFactory.create(NoticiaService::class.java)
    }
}

