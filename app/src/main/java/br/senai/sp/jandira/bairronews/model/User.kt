package br.senai.sp.jandira.bairronews.model

data class User(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    var dataDeNascimento: String = "",
    var fotoPerfil: String? = null,
    var biografia: String? = null
)
