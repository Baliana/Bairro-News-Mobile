package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class NoticiaResponse(
    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,
    val items: Int,
    val noticias: List<NoticiaItem>,
    val mensagem: String?
)