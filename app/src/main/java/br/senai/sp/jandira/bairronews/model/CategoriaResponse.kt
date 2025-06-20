package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class CategoriaResponse(
    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,
    val items: Int,
    val messagem: String,
    val categorias: List<Categoria>
)
