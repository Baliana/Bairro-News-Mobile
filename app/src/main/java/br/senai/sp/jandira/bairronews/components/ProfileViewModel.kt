// app/src/main/java/br.senai.sp.jandira.bairronews.viewmodel/ProfileViewModel.kt
package br.senai.sp.jandira.bairronews.viewmodel

import android.content.Context
import android.net.Uri
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
import br.senai.sp.jandira.bairronews.service.uploadFileToAzure
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

import br.senai.sp.jandira.bairronews.components.DeletionModel
import br.senai.sp.jandira.bairronews.components.DeletionEvent
import br.senai.sp.jandira.bairronews.utils.getFileFromUri


class ProfileViewModel(private val context: Context) : ViewModel() {

    // --- Declarações de Estado ---

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val deletionViewModel = DeletionModel(context)

    var isEditing by mutableStateOf(false)
        private set
    var editedName by mutableStateOf("")
        private set
    var editedBiography by mutableStateOf("")
        private set
    var editedPhotoUrlInput by mutableStateOf("")
        private set
    var selectedPhotoUri by mutableStateOf<Uri?>(null)

    var confirmationPassword by mutableStateOf("")
        private set

    var isUpdating by mutableStateOf(false)
        private set
    var updateErrorMessage by mutableStateOf<String?>(null)
        private set
    var updateSuccessMessage by mutableStateOf<String?>(null)

    var isUploadingPhoto by mutableStateOf(false)
        private set

    // --- Fim das Declarações de Estado ---


    init {
        fetchUserData()
    }

    private fun getUserIdFromPrefs(): Int? {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userIdString = sharedPrefs.getString("user_id", null)
        Log.d("ProfileViewModel", "ID do usuário lido da chave 'user_id': '$userIdString'")

        return userIdString?.toIntOrNull()
    }

    private fun getUserEmailFromPrefs(): String? {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userEmail = sharedPrefs.getString("user_email", null)
        Log.d("ProfileViewModel", "Email do usuário lido da chave 'user_email': '$userEmail'")
        return userEmail
    }

    private fun getUserDataDeNascimentoFromPrefs(): String? {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userDataNascimento = sharedPrefs.getString("user_data_nascimento", null)
        Log.d("ProfileViewModel", "Data de Nascimento do usuário lida da chave 'user_data_nascimento': '$userDataNascimento'")
        return userDataNascimento
    }

    // Função para buscar os dados do usuário da API
    fun fetchUserData() {
        isLoading = true
        errorMessage = null
        val userId = getUserIdFromPrefs()

        if (userId == null) {
            errorMessage = "ID do usuário não encontrado ou inválido nas preferências. Faça login novamente."
            isLoading = false
            Log.e("ProfileViewModel", "ERRO FATAL: ID do usuário é nulo ou inválido. Não foi possível buscar dados. Valor de userId: $userId")
            return
        }

        Log.d("ProfileViewModel", "ENVIANDO PARA API: Tentando buscar dados para o ID do usuário: $userId")

        viewModelScope.launch {
            val call = RetrofitFactory().getUserService().dataUser(userId)

            call.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    Log.d("ProfileViewModel", "RESPOSTA DA API (raw): ${response.raw()}")
                    Log.d("ProfileViewModel", "RESPOSTA DA API (corpo): ${response.body()}")

                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null && userResponse.status && !userResponse.usuarios.isNullOrEmpty()) {
                            currentUser = userResponse.usuarios[0]
                            // Preencher os campos editáveis com os dados atuais do usuário
                            currentUser?.let { user ->
                                editedName = user.nome
                                editedBiography = user.biografia ?: ""
                                // Ao carregar, o URL da foto vai para o campo de input
                                editedPhotoUrlInput = user.fotoPerfil ?: ""
                                selectedPhotoUri = null // Limpa o Uri selecionado da galeria
                            }
                            Log.d("ProfileViewModel", "Dados do usuário carregados com sucesso: ${currentUser?.nome}. Notícias: ${currentUser?.noticias?.size ?: 0}")
                        } else {
                            val msg = if (userResponse?.status == false) {
                                "Erro na lógica da API (status falso)."
                            } else if (userResponse?.usuarios.isNullOrEmpty()) {
                                "Nenhum dado de usuário encontrado para o ID $userId."
                            } else {
                                "Resposta do servidor inesperada para o ID $userId."
                            }
                            errorMessage = msg
                            Log.e("ProfileViewModel", "Erro lógico na resposta da API para ID $userId: $msg. Resposta completa (parcial): $userResponse")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val msg = "Erro ao carregar perfil: Código ${response.code()} - ${errorBody ?: "Erro desconhecido"}"
                        errorMessage = msg
                        Log.e("ProfileViewModel", "Resposta da API NÃO BEM-SUCEDIDA para ID $userId: $msg")
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    val msg = "Erro de rede ao carregar perfil para ID $userId: ${t.message}"
                    errorMessage = msg
                    Log.e("ProfileViewModel", "FALHA DE REDE para ID $userId: $msg", t)
                    isLoading = false
                }
            })
        }
    }

    // *** Funções para o modo de edição ***
    fun toggleEditMode() {
        isEditing = !isEditing
        // Ao sair do modo de edição, limpar a senha de confirmação e mensagens
        if (!isEditing) {
            confirmationPassword = ""
            updateErrorMessage = null
            updateSuccessMessage = null
            // Limpa também as fotos selecionadas/digitadas se for sair do modo de edição sem salvar
            selectedPhotoUri = null
            editedPhotoUrlInput = currentUser?.fotoPerfil ?: ""
        } else {
            // Ao entrar no modo de edição, preencher com os dados atuais
            currentUser?.let { user ->
                editedName = user.nome
                editedBiography = user.biografia ?: ""
                editedPhotoUrlInput = user.fotoPerfil ?: ""
                selectedPhotoUri = null // Garante que a URI da galeria está limpa ao entrar no modo de edição
            }
        }
    }

    fun updateEditedName(name: String) {
        editedName = name
    }

    fun updateEditedBiography(bio: String) {
        editedBiography = bio
    }

    fun updateEditedPhotoUrlInput(url: String) {
        editedPhotoUrlInput = url
        selectedPhotoUri = null // Se o usuário digita uma URL, desmarca a imagem da galeria
    }

    fun updateSelectedPhotoUri(uri: Uri?) {
        selectedPhotoUri = uri
        editedPhotoUrlInput = "" // Se o usuário seleciona da galeria, limpa o campo de URL digitado
    }

    fun updateConfirmationPassword(password: String) {
        confirmationPassword = password
    }

    // Função para enviar os dados atualizados para a API
    fun saveProfileChanges() {
        isUpdating = true
        updateErrorMessage = null
        updateSuccessMessage = null
        isUploadingPhoto = false // Resetar o estado de upload

        val userId = getUserIdFromPrefs()
        val userEmail = getUserEmailFromPrefs()
        val userDataDeNascimento = getUserDataDeNascimentoFromPrefs()

        Log.d("ProfileViewModel", "--- Tentando salvar alterações do perfil ---")
        Log.d("ProfileViewModel", "isEditing: ${isEditing}")
        Log.d("ProfileViewModel", "currentUser ID: ${currentUser?.id}, Nome: ${currentUser?.nome}")
        Log.d("ProfileViewModel", "Valores lidos para verificação:")
        Log.d("ProfileViewModel", "  userId: $userId")
        Log.d("ProfileViewModel", "  userEmail: $userEmail")
        Log.d("ProfileViewModel", "  userDataDeNascimento: $userDataDeNascimento")
        Log.d("ProfileViewModel", "confirmationPassword.value: '${confirmationPassword}' (length: ${confirmationPassword.length})")


        if (userId == null || userEmail == null || userDataDeNascimento == null) {
            updateErrorMessage = "Erro: Dados essenciais do usuário (ID, Email ou Data de Nascimento) não encontrados para atualização."
            isUpdating = false
            Log.e("ProfileViewModel", "ABORTADO: Dados essenciais do usuário (ID, Email, DataNasc) estão nulos ou inválidos. ID: $userId, Email: $userEmail, DataNasc: $userDataDeNascimento")
            return
        }

        if (confirmationPassword.isBlank()) {
            updateErrorMessage = "Por favor, digite sua senha para confirmar as alterações."
            isUpdating = false
            Log.e("ProfileViewModel", "ABORTADO: Senha de confirmação está em branco.")
            return
        }

        viewModelScope.launch {
            var finalPhotoUrl: String? = null

            // Priorize a imagem da galeria
            if (selectedPhotoUri != null) {
                isUploadingPhoto = true
                // ATENÇÃO: Substitua pelo seu SAS token VÁLIDO. Este é um token de exemplo e provavelmente não funcionará.
                // Gerar um novo SAS token com permissões de 'write' e 'list' (e talvez 'read') para o seu container de BLOBs
                val sasToken = "sp=racwl&st=2025-06-10T02:35:11Z&se=2025-06-10T10:35:11Z&sv=2024-11-04&sr=c&sig=dvVA1a55li0hkl9MGNYZxwwIhneiVD%2F2yup3Zi%2BO2PU%3D"
                val storageAccount = "imagensevideos"
                val containerName = "imagens"

                val file = getFileFromUri(context, selectedPhotoUri!!)
                if (file != null) {
                    val uploadedUrl = uploadFileToAzure(file, storageAccount, sasToken, containerName)
                    if (uploadedUrl != null) {
                        finalPhotoUrl = uploadedUrl
                        Log.d("ProfileViewModel", "Uploaded profile photo: $uploadedUrl")
                    } else {
                        updateErrorMessage = "Falha ao carregar a nova foto de perfil."
                        isUpdating = false
                        isUploadingPhoto = false
                        Log.e("ProfileViewModel", "ABORTADO: Falha no upload da foto para o Azure.")
                        return@launch
                    }
                } else {
                    updateErrorMessage = "Não foi possível obter o arquivo da URI da foto de perfil."
                    isUpdating = false
                    isUploadingPhoto = false
                    Log.e("ProfileViewModel", "ABORTADO: Não foi possível obter o arquivo da URI.")
                    return@launch
                }
                isUploadingPhoto = false
            } else if (editedPhotoUrlInput.isNotBlank()) {
                finalPhotoUrl = editedPhotoUrlInput.trim()
            } else {
                // Se nenhum for fornecido, a foto de perfil deve ser null (se a API aceitar)
                // Ou, se você quiser manter a foto existente, use currentUser?.fotoPerfil
                finalPhotoUrl = currentUser?.fotoPerfil // Mantém a foto atual se não houver alteração
            }

            // Criar o objeto User para enviar para a API
            val userToUpdate = User(
                id = userId,
                nome = editedName,
                email = userEmail,
                senha = confirmationPassword,
                dataDeNascimento = userDataDeNascimento,
                biografia = editedBiography.ifBlank { null },
                fotoPerfil = finalPhotoUrl,
                noticias = null // Não envie notícias na atualização de perfil, a API deve lidar com isso separadamente
            )

            Log.d("ProfileViewModel", "--- Dados PREPARADOS para a API: ---")
            Log.d("ProfileViewModel", "ID do usuário (enviado): ${userToUpdate.id}")
            Log.d("ProfileViewModel", "Nome (enviado): '${userToUpdate.nome}'")
            Log.d("ProfileViewModel", "Biografia (enviada): '${userToUpdate.biografia}'")
            Log.d("ProfileViewModel", "URL da Foto (final): '${userToUpdate.fotoPerfil}'")
            Log.d("ProfileViewModel", "Email (enviado): '${userToUpdate.email}'")
            Log.d("ProfileViewModel", "Data de Nascimento (enviada): '${userToUpdate.dataDeNascimento}'")
            Log.d("ProfileViewModel", "Senha de Confirmação (enviada): ${userToUpdate.senha.length} caracteres (não exibida por segurança)")
            Log.d("ProfileViewModel", "--- Fim dos dados PREPARADOS ---")


            Log.d("ProfileViewModel", "Iniciando chamada à API para atualizar perfil...")
            val call = RetrofitFactory().getUserService().updateUser(userId, userToUpdate)

            call.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    Log.d("ProfileViewModel", "RESPOSTA DA API (UPDATE raw): ${response.raw()}")
                    Log.d("ProfileViewModel", "RESPOSTA DA API (UPDATE corpo): ${response.body()}")

                    if (response.isSuccessful) {
                        val updateResponse = response.body()
                        if (updateResponse != null && updateResponse.status) {
                            updateSuccessMessage = "Perfil atualizado com sucesso!"
                            fetchUserData() // Recarrega os dados do perfil após a atualização
                            toggleEditMode() // Sai do modo de edição
                        } else {
                            val msg = updateResponse?.let {
                                if (!it.status) "Falha na atualização: ${it.statusCode}"
                                else "Resposta do servidor inesperada."
                            } ?: "Resposta nula na atualização."
                            updateErrorMessage = msg
                            Log.e("ProfileViewModel", "Erro lógico na resposta da API (UPDATE) para ID $userId: $msg. Resposta completa (parcial): $updateResponse")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val msg = "Erro ao atualizar perfil: Código ${response.code()} - ${errorBody ?: "Erro desconhecido"}"
                        updateErrorMessage = msg
                        Log.e("ProfileViewModel", "Resposta da API NÃO BEM-SUCEDIDA (UPDATE) para ID $userId: $msg")
                    }
                    isUpdating = false
                    confirmationPassword = "" // Limpa a senha após a tentativa
                    Log.d("ProfileViewModel", "--- Fim da tentativa de salvar alterações do perfil ---")
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    val msg = "Erro de rede ao atualizar perfil para ID $userId: ${t.message}"
                    updateErrorMessage = msg
                    Log.e("ProfileViewModel", "FALHA DE REDE (UPDATE) para ID $userId: $msg", t)
                    isUpdating = false
                    confirmationPassword = "" // Limpa a senha após a tentativa
                    Log.d("ProfileViewModel", "--- Fim da tentativa de salvar alterações do perfil ---")
                }
            })
        }
    }

    // Factory para criar o ViewModel com o Context
    class Factory(private val valContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(valContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}