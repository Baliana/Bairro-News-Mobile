package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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
import coil.compose.AsyncImage // Importe para carregar imagem da URL
import br.senai.sp.jandira.bairronews.components.DeletionEvent
import br.senai.sp.jandira.bairronews.components.NoticiaCard
import br.senai.sp.jandira.bairronews.components.ProfileViewModel

// Função para sair de perfil
fun fazerLogout(context: Context, navController: NavController) {
    val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.clear()
    editor.apply()

    navController.navigate("login") { // Mudei para login, pois home pode não ser a tela inicial
        popUpTo("home") { inclusive = true } // Se quiser limpar a pilha até home antes de ir para login
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(navController: NavController?) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(context)
    )

    // Coletar eventos de deleção do ViewModel para exibir Toasts (do DeletionModel)
    LaunchedEffect(Unit) {
        profileViewModel.deletionViewModel.deletionEvents.collect { event ->
            when (event) {
                is DeletionEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    // Recarregar os dados do usuário após uma exclusão bem-sucedida para atualizar a lista
                    profileViewModel.fetchUserData()
                }
                is DeletionEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
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
        IconButton(onClick = { navController?.navigate("home") }){
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(26.dp),
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Sair",
                tint = Color.White // Ícone branco para contraste com o gradiente
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Use rememberScrollState diretamente aqui
                .padding(top = 130.dp) // Espaço para a foto de perfil flutuar
        ) {
            // Card com informações do usuário
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 80.dp, // Espaço para a foto de perfil sobrepor
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                ) {
                    if (profileViewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregando perfil...", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (profileViewModel.errorMessage != null) {
                        Text("Erro: ${profileViewModel.errorMessage}", color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { profileViewModel.fetchUserData() }) {
                            Text("Tentar Novamente")
                        }
                    } else {
                        // Nome do usuário
                        Text(profileViewModel.currentUser?.nome ?: "Nome de Usuário",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        // Email ou uma descrição simples
                        Text(profileViewModel.currentUser?.email ?: "Email ou Cargo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Biografia
                        Text("Sobre",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            profileViewModel.currentUser?.biografia ?: "Nenhuma biografia disponível.",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título para as Notícias
            Text(
                text = "Minhas Notícias",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            // LazyVerticalGrid para as notícias
            if (profileViewModel.isLoading) {
                // Indicador de carregamento para as notícias também
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (profileViewModel.currentUser?.noticias.isNullOrEmpty()) {
                Text(
                    text = "Você não possui notícias publicadas.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Duas colunas
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.heightIn(max = 1000.dp) // Limita a altura para que a LazyVerticalGrid funcione dentro de um Column rolavel. Ajuste conforme necessário.
                ) {
                    items(profileViewModel.currentUser?.noticias ?: listOf()) { noticia ->
                        NoticiaCard(
                            noticia = noticia,
                            onClick = { id ->
                                navController?.navigate("noticiaDetalhes/${id}")
                            },
                            onDeleteClick = { id -> profileViewModel.deletionViewModel.confirmDeletion(id) } // Deleção via ViewModel
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento após a grade
            }
        }

        // Foto de perfil flutuante
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
                if (profileViewModel.currentUser?.fotoPerfil.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = Color.Gray,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color.LightGray) // Fundo cinza para ícone padrão
                    )
                } else {
                    AsyncImage(
                        model = profileViewModel.currentUser?.fotoPerfil,
                        contentDescription = "Foto de perfil do usuário",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop // Para cortar a imagem e preencher
                    )
                }
            }
        }
    }

    // AlertDialog de confirmação de deleção (reutilizado do DeletionModel)
    if (profileViewModel.deletionViewModel.showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { profileViewModel.deletionViewModel.dismissDeleteConfirmation() },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja deletar esta notícia?") },
            confirmButton = {
                Button(
                    onClick = {
                        profileViewModel.deletionViewModel.executeDelete {
                            // Este callback será chamado pelo DeletionModel para informar que a notícia foi removida
                            // Precisamos recarregar os dados do usuário para que a lista de notícias seja atualizada
                            // Alternativamente, poderíamos filtrar a lista localmente no ViewModel do perfil
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