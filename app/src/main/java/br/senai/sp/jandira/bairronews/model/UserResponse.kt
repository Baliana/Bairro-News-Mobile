package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,
    val items: Int?, // Este campo 'items' parece não estar na sua resposta de API. Se não for usado, pode remover ou manter como nullable.
    @SerializedName("user") // <--- *** CORREÇÃO AQUI ***
    val usuarios: List<User>? // Nome da variável Kotlin continua 'usuarios' mas mapeia para 'user' do JSON
)