package br.senai.sp.jandira.bairronews.components // Novo pacote

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.NoticiaResponse
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf // <--- ADICIONAR ESTA IMPORTAÇÃO
import androidx.compose.runtime.getValue // <--- ADICIONAR ESTA IMPORTAÇÃO
import androidx.compose.runtime.setValue // <--- ADICIONAR ESTA IMPORTAÇÃO

// Usaremos uma classe para representar eventos de UI para o Toast
sealed class DeletionEvent {
    data class Success(val message: String) : DeletionEvent()
    data class Error(val message: String) : DeletionEvent()
}

class DeletionModel(private val context: Context) : ViewModel() {

    // Estado para controlar o AlertDialog de confirmação
    var showDeleteConfirmationDialog by mutableStateOf(false)
        private set

    // Estado para armazenar o ID da notícia a ser deletada
    var newsIdToDelete by mutableStateOf<Int?>(null)
        private set

    // SharedFlow para enviar eventos de sucesso/erro para a UI (para Toast)
    private val _deletionEvents = MutableSharedFlow<DeletionEvent>()
    val deletionEvents: SharedFlow<DeletionEvent> = _deletionEvents

    /**
     * Inicia o processo de deleção, exibindo o dialog de confirmação.
     * @param id O ID da notícia a ser deletada.
     */
    fun confirmDeletion(id: Int) {
        newsIdToDelete = id
        showDeleteConfirmationDialog = true
    }

    /**
     * Cancela o dialog de confirmação de deleção.
     */
    fun dismissDeleteConfirmation() {
        showDeleteConfirmationDialog = false
        newsIdToDelete = null
    }

    /**
     * Executa a deleção da notícia após a confirmação.
     * @param onNewsRemoved Uma função de callback para atualizar a lista de notícias na UI.
     */
    fun executeDelete(onNewsRemoved: (Int) -> Unit) {
        newsIdToDelete?.let { idToDelete ->
            viewModelScope.launch { // Usando corrotinas para chamadas de rede
                val call = RetrofitFactory().getNoticiaService().deleteNoticia(idToDelete)

                call.enqueue(object : Callback<NoticiaResponse> {
                    override fun onResponse(call: Call<NoticiaResponse>, response: Response<NoticiaResponse>) {
                        if (response.isSuccessful) {
                            Log.d("DeletionModel", "Notícia ID $idToDelete deletada com sucesso.") // Nome do Log atualizado
                            _deletionEvents.tryEmit(DeletionEvent.Success("Notícia deletada com sucesso!"))
                            onNewsRemoved(idToDelete) // Notifica a UI para remover a notícia da lista
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = errorBody ?: "Erro desconhecido"
                            Log.e("DeletionModel", "Falha ao deletar notícia: ${response.code()} - $errorMessage") // Nome do Log atualizado
                            _deletionEvents.tryEmit(DeletionEvent.Error("Erro ao deletar notícia: $errorMessage"))
                        }
                        dismissDeleteConfirmation() // Sempre esconde o dialog após a resposta
                    }

                    override fun onFailure(call: Call<NoticiaResponse>, t: Throwable) {
                        Log.e("DeletionModel", "Erro de rede ao deletar notícia: ${t.message}") // Nome do Log atualizado
                        _deletionEvents.tryEmit(DeletionEvent.Error("Erro de rede ao deletar notícia: ${t.message}"))
                        dismissDeleteConfirmation() // Sempre esconde o dialog
                    }
                })
            }
        } ?: run {
            Log.e("DeletionModel", "newsIdToDelete é nulo, não foi possível deletar.") // Nome do Log atualizado
            _deletionEvents.tryEmit(DeletionEvent.Error("Erro: ID da notícia para deletar não encontrado."))
            dismissDeleteConfirmation()
        }
    }

    // Factory para criar o ViewModel com o Context (necessário para Toast/SharedPreferences se o ViewModel for usar)
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeletionModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DeletionModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}