package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class NoticiaCreatePayload(
    val titulo: String,
    val conteudo: String,

    @SerializedName("tbl_usuario_id")
    val tblUsuarioId: Int,

    @SerializedName("tbl_endereco_id")
    val tblEnderecoId: Int, // ✅ Correção aqui: usamos o ID do endereço, não a string

    @SerializedName("urls_midia")
    val urlsMidia: List<String>? = null,

    val categorias: List<Int>,

    @SerializedName("data_postagem")
    val dataPostagem: String? = null // Formato: YYYY-MM-DD
)
