package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class UrlMidiaItem(
    val id: Int? = null,
    @SerializedName("url_midia")
    val urlMidia: String,
    @SerializedName("tbl_noticia_id")
    val tblNoticiaId: Int? = null
)