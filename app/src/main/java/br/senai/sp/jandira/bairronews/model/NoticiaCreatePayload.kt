package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class NoticiaCreatePayload(
    val titulo: String,
    val conteudo: String,
    @SerializedName("tbl_usuario_id")
    val tblUsuarioId: Int,
    val endereco: Endereco, // Use o Endereco que criamos
    @SerializedName("urls_midia")
    val urlsMidia: List<String>? = null, // Array de Strings de URL para o envio
    val categorias: List<Int>, // Array de IDs de categorias
    @SerializedName("data_postagem")
    val dataPostagem: String? = null // YYYY-MM-DD
)