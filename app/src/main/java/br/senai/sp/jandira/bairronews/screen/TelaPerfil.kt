// app/src/main/java/br.senai.sp.jandira.bairronews.screen/TelaPerfil.kt
package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
// REMOVA ESTE IMPORT: import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// REMOVA ESTE IMPORT: import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn // ADICIONE ESTE IMPORT PARA USAR LazyColumn
import androidx.compose.foundation.lazy.items // ADICIONE ESTE IMPORT PARA USAR items em LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // MANTENHA ESTE SE VOCÊ USAR UM COLUMN ROLÁVEL NA RAIZ
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import br.senai.sp.jandira.bairronews.components.DeletionEvent
import br.senai.sp.jandira.bairronews.components.NoticiaCard
import br.senai.sp.jandira.bairronews.viewmodel.ProfileViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions // MANTENHA ESTE IMPORT
import androidx.compose.ui.text.style.TextAlign // Importar TextAlign

// Função para sair de perfil
fun fazerLogout(context: Context, navController: NavController) {
    val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.clear()
    editor.apply()

    navController.navigate("login") {
        popUpTo("home") { inclusive = true }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(navController: NavController?) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(context)
    )

    // Launcher para selecionar imagem da galeria
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileViewModel.updateSelectedPhotoUri(uri)
    }

    // Coletar eventos de deleção (do DeletionModel)
    LaunchedEffect(Unit) {
        profileViewModel.deletionViewModel.deletionEvents.collect { event ->
            when (event) {
                is DeletionEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    profileViewModel.fetchUserData()
                }
                is DeletionEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Coletar mensagens de atualização do perfil
    LaunchedEffect(profileViewModel.updateSuccessMessage) {
        profileViewModel.updateSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            // profileViewModel.clearUpdateSuccessMessage()
        }
    }

    LaunchedEffect(profileViewModel.updateErrorMessage) {
        profileViewModel.updateErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            // profileViewModel.clearUpdateErrorMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fundo com gradiente
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))
                    )
                )
        )
        // Botão de Sair
        IconButton(
            onClick = { navController?.let { fazerLogout(context, it) } },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(26.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Sair",
                tint = Color.White
            )
        }

        // Botão de Editar/Salvar Perfil
        IconButton(
            onClick = {
                if (profileViewModel.isEditing) {
                    profileViewModel.saveProfileChanges()
                } else {
                    profileViewModel.toggleEditMode()
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(26.dp)
        ) {
            Icon(
                imageVector = if (profileViewModel.isEditing) Icons.Default.Save else Icons.Default.Edit,
                contentDescription = if (profileViewModel.isEditing) "Salvar" else "Editar Perfil",
                tint = Color.White
            )
        }

        // ALTERAÇÃO CRÍTICA AQUI: Usando LazyColumn como o container de rolagem principal
        LazyColumn( // Mude Column para LazyColumn
            modifier = Modifier
                .fillMaxSize()
                // REMOVA .verticalScroll(rememberScrollState()) do LazyColumn
                .padding(top = 130.dp),
            // REMOVIDO: horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Item para o Card com informações do usuário
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Adicionado para centralizar conteúdo
                            .padding(top = 80.dp,
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally // Alinhe o conteúdo da Column interna
                    ) {
                        if (profileViewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Carregando perfil...", modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                        } else if (profileViewModel.errorMessage != null) {
                            Text("Erro: ${profileViewModel.errorMessage}", color = Color.Red)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { profileViewModel.fetchUserData() }) {
                                Text("Tentar Novamente")
                            }
                        } else {
                            // Nome do usuário (Editável se estiver no modo de edição)
                            if (profileViewModel.isEditing) {
                                OutlinedTextField(
                                    value = profileViewModel.editedName,
                                    onValueChange = { profileViewModel.updateEditedName(it) },
                                    label = { Text("Nome") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(profileViewModel.currentUser?.nome ?: "Nome de Usuário",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(), // Garante que o Text ocupe a largura total
                                    textAlign = TextAlign.Center // Centraliza o texto
                                )
                            }

                            // Email (sempre em modo de visualização, não editável aqui)
                            Text(profileViewModel.currentUser?.email ?: "Email ou Cargo",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(), // Garante que o Text ocupe a largura total
                                textAlign = TextAlign.Center // Centraliza o texto
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Biografia (Editável se estiver no modo de edição)
                            Text("Sobre",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(), // Garante que o Text ocupe a largura total
                                textAlign = TextAlign.Center // Centraliza o texto
                            )
                            if (profileViewModel.isEditing) {
                                OutlinedTextField(
                                    value = profileViewModel.editedBiography,
                                    onValueChange = { profileViewModel.updateEditedBiography(it) },
                                    label = { Text("Biografia") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            } else {
                                Text(
                                    profileViewModel.currentUser?.biografia ?: "Nenhuma biografia disponível.",
                                    fontSize = 14.sp,
                                    modifier = Modifier.fillMaxWidth(), // Garante que o Text ocupe a largura total
                                    textAlign = TextAlign.Center // Centraliza o texto
                                )
                            }

                            // Opções de foto de perfil (Editável se estiver no modo de edição)
                            if (profileViewModel.isEditing) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Foto de Perfil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.fillMaxWidth(), // Garante que o Text ocupe a largura total
                                    textAlign = TextAlign.Center // Centraliza o texto
                                )
                                Button(
                                    onClick = { pickImageLauncher.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6))
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = "Selecionar Foto")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Selecionar da Galeria")
                                }
                                if (profileViewModel.selectedPhotoUri != null) {
                                    Text(
                                        text = "Imagem selecionada da galeria.",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .fillMaxWidth(), // Garante que o Text ocupe a largura total
                                        textAlign = TextAlign.Center // Centraliza o texto
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = profileViewModel.editedPhotoUrlInput,
                                    onValueChange = { profileViewModel.updateEditedPhotoUrlInput(it) },
                                    label = { Text(text = "Ou cole a URL da imagem") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (profileViewModel.editedPhotoUrlInput.isNotBlank()) {
                                    Text(
                                        text = "Usando URL digitada.",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .fillMaxWidth(), // Garante que o Text ocupe a largura total
                                        textAlign = TextAlign.Center // Centraliza o texto
                                    )
                                }


                                // Campo de senha para confirmar as alterações
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = profileViewModel.confirmationPassword,
                                    onValueChange = { profileViewModel.updateConfirmationPassword(it) },
                                    label = { Text("Confirme sua Senha") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions( // Agora KeyboardOptions está sendo referenciado corretamente
                                        keyboardType = KeyboardType.Password
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Indicadores de carregamento para o salvamento e upload
                                if (profileViewModel.isUpdating || profileViewModel.isUploadingPhoto) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CircularProgressIndicator(modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                                    Text(
                                        text = if (profileViewModel.isUploadingPhoto) "Enviando foto..." else "Salvando perfil...",
                                        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Espaçador após o card de perfil
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Título para as Notícias (apenas visível no modo de visualização)
            if (!profileViewModel.isEditing) {
                item { // Mova o Text para dentro de um item() para o LazyColumn
                    Text(
                        text = "Minhas Notícias",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth() // Adicionado para permitir o padding horizontal
                            .padding(start = 16.dp, bottom = 8.dp),
                        textAlign = TextAlign.Start // Alinhe o título à esquerda se preferir
                    )
                }

                // Substitua LazyVerticalGrid por LazyColumn e renderize os itens em GridCells
                // Isso requer um pouco de adaptação para simular uma grade dentro de uma coluna
                if (profileViewModel.isLoading) {
                    item { // Coloque o CircularProgressIndicator dentro de um item()
                        CircularProgressIndicator(modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                    }
                } else if (profileViewModel.currentUser?.noticias.isNullOrEmpty()) {
                    item { // Coloque o Text dentro de um item()
                        Text(
                            text = "Você não possui notícias publicadas.",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Crie itens em pares para simular a grade de 2 colunas
                    val noticias = profileViewModel.currentUser?.noticias ?: listOf()
                    val pairs = noticias.chunked(2) // Cria pares de notícias

                    items(pairs) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp), // Ajuste o padding
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { noticia ->
                                Box(modifier = Modifier.weight(1f)) { // Cada notícia ocupa 1/2 da largura
                                    NoticiaCard(
                                        noticia = noticia,
                                        onClick = { id -> navController?.navigate("noticiaDetalhes/${id}") },
                                        onDeleteClick = { id -> profileViewModel.deletionViewModel.confirmDeletion(id) }
                                    )
                                }
                            }
                            // Adiciona um Box vazio para preencher a segunda coluna se houver um número ímpar de notícias
                            if (pair.size == 1) {
                                Box(modifier = Modifier.weight(1f)) {}
                            }
                        }
                    }
                    item { // Espaçador no final das notícias
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Foto de perfil flutuante (fora do LazyColumn)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                border = BorderStroke(
                    width = 3.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))
                    )
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                // Lógica para exibir a imagem correta
                val photoUrlToDisplay = when {
                    profileViewModel.selectedPhotoUri != null -> profileViewModel.selectedPhotoUri
                    profileViewModel.editedPhotoUrlInput.isNotBlank() -> profileViewModel.editedPhotoUrlInput
                    else -> profileViewModel.currentUser?.fotoPerfil
                }

                if (photoUrlToDisplay == null || photoUrlToDisplay.toString().isBlank()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = Color.Gray,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color.LightGray)
                    )
                } else {
                    AsyncImage(
                        model = photoUrlToDisplay,
                        contentDescription = "Foto de perfil do usuário",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
        }
    }

    // AlertDialog de confirmação de deleção
    if (profileViewModel.deletionViewModel.showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { profileViewModel.deletionViewModel.dismissDeleteConfirmation() },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja deletar esta notícia?") },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.deletionViewModel.executeDelete {
                            profileViewModel.fetchUserData()
                        }
                    }
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                Button(onClick = { profileViewModel.deletionViewModel.dismissDeleteConfirmation() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaPerfilPreview() {
    TelaPerfil(null)
}