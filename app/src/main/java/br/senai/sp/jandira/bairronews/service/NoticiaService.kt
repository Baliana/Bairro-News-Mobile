package br.senai.sp.jandira.bairronews.service

import br.senai.sp.jandira.bairronews.model.CategoriaResponse
import br.senai.sp.jandira.bairronews.model.Comentario
import br.senai.sp.jandira.bairronews.model.NoticiaCreatePayload
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.NoticiaResponse // Para listar notícias
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface NoticiaService {

    @Headers("Content-Type: application/json")
    @POST("noticia")
    fun saveNoticia(@Body payload: NoticiaCreatePayload): Call<NoticiaItem>

    // Faz uma requisição GET para o endpoint para retorna todos as noticias disponíveis na API
    @GET("noticia")
    fun listAllNoticias(): Call<NoticiaResponse>

    @GET("noticia/{id}")
    fun listNoticia(@Path("id") id: Int): Call<NoticiaResponse>

    @GET("categoria")
    fun listAllCategorias(): Call<CategoriaResponse>


    @Headers("Content-Type: application/json")
    @POST("noticia/{noticiaId}/comentario") // Ajuste o endpoint conforme sua API
    fun addComentario(
        @Path("noticiaId") noticiaId: Int,
        @Body comentario: Comentario
    ): Call<Comentario>

}