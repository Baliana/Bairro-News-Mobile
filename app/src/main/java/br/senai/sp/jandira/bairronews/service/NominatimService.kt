package br.senai.sp.jandira.bairronews.service

import br.senai.sp.jandira.bairronews.model.Endereco
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Interface para a API Nominatim
interface NominatimApiService {
    @GET("search?format=json&addressdetails=1&limit=1")
    suspend fun searchAddress(
        @Query("q") query: String,
        @Query("accept-language") lang: String = "pt-BR"
    ): List<NominatimResponseItem>
}

// Data class para o item de resposta do Nominatim
data class NominatimResponseItem(
    val lat: String,
    val lon: String,
    val name: String?,
    @SerializedName("display_name") val display_name: String,
    val address: AddressDetails? // Detalhes do endereço para extrair CEP
)

data class AddressDetails(
    val postcode: String?
)

// Função auxiliar para extrair CEP
fun extrairCEP(displayName: String): String? {
    val regex = "\\b\\d{5}-\\d{3}\\b".toRegex()
    val match = regex.find(displayName)
    return match?.value
}

// Cliente Retrofit para Nominatim
object NominatimRetrofit {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Aumenta o timeout de conexão
        .readTimeout(30, TimeUnit.SECONDS)    // Aumenta o timeout de leitura
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Adiciona o OkHttpClient configurado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: NominatimApiService by lazy {
        retrofit.create(NominatimApiService::class.java)
    }
}

// Função para buscar coordenadas e detalhes do endereço
suspend fun buscarCoordenadasComEndereco(enderecoString: String): Endereco? {
    try {
        val response = NominatimRetrofit.apiService.searchAddress(enderecoString)
        if (response.isNotEmpty()) {
            val item = response[0]
            val lat = item.lat.toDoubleOrNull() ?: return null
            val lon = item.lon.toDoubleOrNull() ?: return null
            val cep = item.address?.postcode ?: extrairCEP(item.display_name)

            return Endereco(
                cep = cep ?: "", // Pode ser vazio se não encontrar
                displayName = item.display_name,
                lat = lat,
                lon = lon,
                // Outros campos de endereço podem ser nulos ou preenchidos se disponíveis no 'address'
                logradouro = null,
                complemento = null,
                bairro = null,
                localidade = null,
                uf = null,
                ibge = null,
                gia = null,
                siafi = null
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Erro ao buscar coordenadas com endereço: ${e.message}")
    }
    return null
}