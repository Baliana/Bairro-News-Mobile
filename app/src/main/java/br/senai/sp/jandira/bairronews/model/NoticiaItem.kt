package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class NoticiaItem(
    val id: Int? = null,
    val titulo: String,
    val categorias: List<Categoria>, // Retorna objetos Categoria
    @SerializedName("urls_midia")
    val urlsMidia: List<UrlMidiaItem>? = null, // Retorna objetos UrlMidiaItem
    val conteudo: String,
    @SerializedName("data_postagem")
    val dataPostagem: String,
    @SerializedName("User")
    val user: User? = null, // Pode retornar o objeto User completo
    @SerializedName("tbl_usuario_id")
    val tblUsuarioId: Int? = null,
    val endereco: Endereco?,
    val comentarios: List<Comentario>? = null
)