package br.senai.sp.jandira.bairronews.model

data class AuthenticationUser(
    var status: Boolean,
    var status_code: Int,
    var messagem: String,
    var usuario: User? = null
)
