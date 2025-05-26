package br.senai.sp.jandira.bairronews.model

data class ResultUser(
    var status: Boolean,
    var status_code: Int,
    var messagem: String,
    var results: List<User>? = null
)
