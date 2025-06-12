package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class User(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    @SerializedName("data_nascimento")
    val dataDeNascimento: String,

    val biografia: String? = null,

    @SerializedName("foto_perfil")
    val fotoPerfil: String? = null,

    val noticias: List<NoticiaItem>? = null
)

