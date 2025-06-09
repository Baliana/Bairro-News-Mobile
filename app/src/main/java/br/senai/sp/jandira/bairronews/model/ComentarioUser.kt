package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class ComentarioUser(
    val nome: String,
    @SerializedName("foto_perfil")
    val fotoPerfil: String
)
