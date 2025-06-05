package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class Comentario(
    val id: Int? = null,
    val conteudo: String,
    @SerializedName("tbl_usuario_id")
    val tblUsuarioId: Int? = null,
    @SerializedName("tbl_noticia_id")
    val tblNoticiaId: Int? = null,
    @SerializedName("data_postagem")
    val dataPostagem: String
)
