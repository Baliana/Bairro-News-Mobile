package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class Comentario(
    val id: Int? = null,
    val conteudo: String,
    @SerializedName("data_postagem")
    val dataPostagem: String
)
