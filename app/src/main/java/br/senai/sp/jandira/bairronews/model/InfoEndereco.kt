package br.senai.sp.jandira.bairronews.model

import br.senai.sp.jandira.bairronews.service.AddressDetails
import com.google.gson.annotations.SerializedName

data class InfoEndereco(
    val lat: Number,
    val lon: Number,
    val name: String?,
    @SerializedName("display_name")
    val displayName: String,
    val cep: String
)
