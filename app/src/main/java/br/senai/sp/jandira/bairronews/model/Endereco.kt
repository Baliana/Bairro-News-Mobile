package br.senai.sp.jandira.bairronews.model

import com.google.gson.annotations.SerializedName

data class Endereco(
    val id: Int? = null,
    val cep: String,
    val logradouro: String? = null,
    val complemento: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val ibge: String? = null,
    val gia: String? = null,
    val siafi: String? = null,
    @SerializedName("display_name") // Mapeia "display_name" do JSON para "displayName" no Kotlin
    val displayName: String,
    val lat: Double,
    val lon: Double
)
