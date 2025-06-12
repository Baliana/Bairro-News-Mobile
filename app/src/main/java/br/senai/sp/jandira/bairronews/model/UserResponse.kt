package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,
    val items: Int?,
    val usuarios: List<User>?,
    val mensagem: String?
)
