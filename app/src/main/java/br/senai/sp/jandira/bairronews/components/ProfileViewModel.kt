package br.senai.sp.jandira.bairronews.components

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.senai.sp.jandira.bairronews.model.User
import br.senai.sp.jandira.bairronews.model.UserResponse
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val context: Context) : ViewModel() {

    // Estado para armazenar os dados do usuário
    var currentUser by mutableStateOf<User?>(null)
        private set

    // Estado de carregamento
    var isLoading by mutableStateOf(true)
        private set

    // Estado de erro (para exibir uma mensagem de erro na UI)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Deleção de notícias, reutilizando o ViewModel que já fizemos
    // Você pode ter um ViewModel pai que contenha o DeletionModel, ou instanciá-lo diretamente
    // aqui se ele for usado apenas nesta tela ou se você quiser mantê-lo autocontido.
    // Para simplificar, vou instanciá-lo diretamente aqui para o exemplo.
    val deletionViewModel = DeletionModel(context)


    init {
        fetchUserData()
    }

    private fun getUserIdFromPrefs(): Int? {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        // Certifique-se de que a chave 'id' é a que você usa para salvar o ID do usuário
        val userId = sharedPrefs.getInt("id", -1) // -1 como valor padrão se não encontrar
        return if (userId != -1) userId else null
    }

    fun fetchUserData() {
        isLoading = true
        errorMessage = null
        val userId = getUserIdFromPrefs()

        if (userId == null) {
            errorMessage = "ID do usuário não encontrado nas preferências."
            isLoading = false
            Log.e("ProfileViewModel", "ID do usuário é nulo. Não foi possível buscar dados.")
            return
        }

        viewModelScope.launch {
            val call = RetrofitFactory().getUserService().dataUser(userId)

            call.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null && userResponse.status && !userResponse.usuarios.isNullOrEmpty()) {
                            currentUser = userResponse.usuarios[0] // Assume que 'usuarios' terá apenas um item para o ID específico
                            Log.d("ProfileViewModel", "Dados do usuário carregados: ${currentUser?.nome}")
                        } else {
                            val msg = userResponse?.mensagem ?: "Resposta vazia ou com status falso."
                            errorMessage = msg
                            Log.e("ProfileViewModel", "Erro na resposta da API: $msg")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val msg = "Erro ao carregar perfil: Código ${response.code()} - ${errorBody ?: "Erro desconhecido"}"
                        errorMessage = msg
                        Log.e("ProfileViewModel", "Resposta não bem-sucedida: $msg")
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    val msg = "Erro de rede ao carregar perfil: ${t.message}"
                    errorMessage = msg
                    Log.e("ProfileViewModel", msg)
                    isLoading = false
                }
            })
        }
    }

    // Factory para criar o ViewModel com o Context
    class Factory(private val valContext: Context) : ViewModelProvider.Factory { // Renomeado o parâmetro para evitar conflito com 'context' do ViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(valContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}