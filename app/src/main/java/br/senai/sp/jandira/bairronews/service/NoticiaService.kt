package br.senai.sp.jandira.bairronews.service

import br.senai.sp.jandira.bairronews.model.CategoriaResponse
import br.senai.sp.jandira.bairronews.model.NoticiaCreatePayload
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.NoticiaResponse // Para listar notícias
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface NoticiaService {

    @Headers("Content-Type: application/json")
    @POST("noticia")
    fun saveNoticia(@Body payload: NoticiaCreatePayload): Call<NoticiaItem>

    @GET("noticia")
    fun listAllNoticias(): Call<NoticiaResponse>
    @GET("categoria")
    fun listAllCategorias(): Call<CategoriaResponse>
    @GET("/status")
    fun pingApi(): Call<Void>
}