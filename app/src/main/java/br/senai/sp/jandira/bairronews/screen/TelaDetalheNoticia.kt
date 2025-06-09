package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.components.ComentarioCard
import br.senai.sp.jandira.bairronews.model.Comentario
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.NoticiaResponse
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import coil.compose.rememberAsyncImagePainter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.time.ZoneOffset // Importar ZoneOffset para garantir o 'Z' no final

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalheNoticia(navController: NavHostController?, noticiaId: Int?) {
    val context = LocalContext.current
    var noticia by remember { mutableStateOf<NoticiaItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val newComentarioContent = remember { mutableStateOf("") }
    var isSendingComment by remember { mutableStateOf(false) }
    var showCommentInput by remember { mutableStateOf(false) }

    fun fetchNoticiaDetalhes(id: Int) {
        isLoading = true
        errorMessage = null
        val call = RetrofitFactory().getNoticiaService().listNoticia(id)

        call.enqueue(object : Callback<NoticiaResponse> {
            override fun onResponse(call: Call<NoticiaResponse>, response: Response<NoticiaResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val noticiaResponse = response.body()
                    if (noticiaResponse != null && noticiaResponse.status && noticiaResponse.noticias.isNotEmpty()) {
                        noticia = noticiaResponse.noticias.first()
                        Log.d("TelaDetalheNoticia", "Notícia carregada: ${noticia?.titulo}")
                    } else {
                        errorMessage = "Notícia não encontrada ou erro na resposta da API."
                        Log.e("TelaDetalheNoticia", "Notícia não encontrada: ${response.code()} - ${response.message()}")
                    }
                } else {
                    errorMessage = "Erro ao carregar notícia: ${response.code()} - ${response.message()}"
                    Log.e("TelaDetalheNoticia", "Erro HTTP ao carregar notícia: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NoticiaResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Erro de conexão: ${t.message}"
                Log.e("TelaDetalheNoticia", "Falha na requisição: ${t.message}", t)
            }
        })
    }

    fun getUserId(): Int? {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPrefs.getString("user_id", null)?.toIntOrNull()
    }

    fun sendComentario(noticiaId: Int) {
        if (newComentarioContent.value.isBlank()) {
            Toast.makeText(context, "O comentário não pode ser vazio.", Toast.LENGTH_SHORT).show()
            return
        }

        isSendingComment = true
        val userId = getUserId()
        if (userId == null) {
            Toast.makeText(context, "Erro: ID do usuário não encontrado. Faça login novamente.", Toast.LENGTH_LONG).show()
            isSendingComment = false
            navController?.navigate("login")
            return
        }

        // --- CORREÇÃO DA DATA: Formatar para o padrão ISO_OFFSET_DATE_TIME com 'Z' ---
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC) // Usar UTC para garantir o 'Z'
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formattedDate = currentDateTime.format(formatter)
        Log.d("TelaDetalheNoticia", "Data formatada para envio: $formattedDate")

        val comentarioPayload = Comentario(
            conteudo = newComentarioContent.value.trim(),
            tblUsuarioId = userId,
            tblNoticiaId = noticiaId,
            dataPostagem = formattedDate // Usar a data formatada
        )

        val call = RetrofitFactory().getNoticiaService().addComentario(comentarioPayload)

        call.enqueue(object : Callback<Comentario> {
            override fun onResponse(call: Call<Comentario>, response: Response<Comentario>) {
                isSendingComment = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Comentário enviado!", Toast.LENGTH_SHORT).show()
                    newComentarioContent.value = ""
                    showCommentInput = false
                    noticiaId.let { fetchNoticiaDetalhes(it) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context, "Erro ao enviar comentário: ${response.code()} - ${errorBody}", Toast.LENGTH_LONG).show()
                    Log.e("TelaDetalheNoticia", "Erro ao enviar comentário: ${response.code()} - ${errorBody}")
                }
            }

            override fun onFailure(call: Call<Comentario>, t: Throwable) {
                isSendingComment = false
                Toast.makeText(context, "Erro de conexão ao enviar comentário: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TelaDetalheNoticia", "Falha na requisição de comentário: ${t.message}", t)
            }
        })
    }

    LaunchedEffect(noticiaId) {
        if (noticiaId != null) {
            fetchNoticiaDetalhes(noticiaId)
        } else {
            errorMessage = "ID da notícia não fornecido."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Notícia") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE0E0E0), // AppBar em tom de cinza claro
                    titleContentColor = Color.Black, // Título da AppBar em preto
                    navigationIconContentColor = Color.Black // Ícone de navegação em preto
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    color = Color(0xFFF0F0F0) // Fundo da tela em cinza quase branco
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black) // Cor do loading em preto
                Text("Carregando notícia...", color = Color.Black, modifier = Modifier.offset(y = 40.dp)) // Cor do texto de loading em preto
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { noticiaId?.let { fetchNoticiaDetalhes(it) } }) {
                    Text("Tentar Novamente")
                }
            } else if (noticia == null) {
                Text("Notícia não encontrada.", color = Color.Black) // Cor do texto em preto
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    // Imagem da notícia
                    item {
                        val imageUrl = noticia?.urlsMidia?.firstOrNull()?.urlMidia
                        if (!imageUrl.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = noticia?.titulo,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Título da Notícia
                    item {
                        Text(
                            text = noticia?.titulo ?: "Título Indisponível",
                            color = Color.Black, // Texto preto
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )
                    }

                    // Informações adicionais (Autor, Data, Categoria, Endereço)
                    item {
                        Column {
                            val dataFormatada = try {
                                val parsedDate = OffsetDateTime.parse(noticia?.dataPostagem, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                                parsedDate.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de'yyyy 'às' HH:mm", Locale("pt", "BR")))
                            } catch (e: DateTimeParseException) {
                                Log.e("TelaDetalheNoticia", "Erro ao parsear data: ${noticia?.dataPostagem} - ${e.message}")
                                "Data Indisponível"
                            }
                            Text(
                                text = "Por ${noticia?.user?.nome ?: "Desconhecido"} em $dataFormatada",
                                color = Color(0xFF616161), // Um cinza escuro para informações secundárias
                                fontSize = 12.sp
                            )
                            val categoriasString = noticia?.categorias?.joinToString(", ") { it.nome }
                            if (!categoriasString.isNullOrBlank()) {
                                Text(
                                    text = "Categorias: $categoriasString",
                                    color = Color(0xFF616161), // Um cinza escuro
                                    fontSize = 12.sp
                                )
                            }
                            val enderecoDisplay = noticia?.endereco?.displayName
                            if (!enderecoDisplay.isNullOrBlank()) {
                                Text(
                                    text = "Local: $enderecoDisplay",
                                    color = Color(0xFF616161), // Um cinza escuro
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Conteúdo da Notícia
                    item {
                        Text(
                            text = noticia?.conteudo ?: "Conteúdo da notícia indisponível.",
                            color = Color.Black, // Texto preto
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify
                        )
                    }

                    // Seção de Comentários
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Comentários",
                                color = Color.Black, // Texto preto
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // Botão para mostrar/esconder o campo de comentário
                            IconButton(
                                onClick = { showCommentInput = !showCommentInput },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Color.Black // Ícone em preto
                                )
                            ) {
                                Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Adicionar Comentário")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Campo para adicionar novo comentário (condicional)
                    if (showCommentInput) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)) // Fundo do card de comentário branco
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Adicionar um comentário", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = newComentarioContent.value,
                                        onValueChange = { newComentarioContent.value = it },
                                        label = { Text("Seu comentário") },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Red,
                                            unfocusedBorderColor = Color.Gray,
                                            focusedLabelColor = Color.Red,
                                            unfocusedLabelColor = Color.Gray,
                                            unfocusedTextColor = Color.Black, // Texto digitado em preto
                                            focusedTextColor = Color.Black // Texto digitado em preto
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { noticiaId?.let { sendComentario(it) } },
                                        modifier = Modifier.align(Alignment.End),
                                        enabled = !isSendingComment,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                                    ) {
                                        if (isSendingComment) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Enviando...")
                                        } else {
                                            Text("Enviar Comentário")
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Icon(Icons.Filled.Send, contentDescription = "Enviar")
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Lista de Comentários
                    if (noticia?.comentarios.isNullOrEmpty()) {
                        item {
                            Text(
                                text = "Nenhum comentário ainda. Seja o primeiro a comentar!",
                                color = Color(0xFF616161), // Um cinza escuro
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        val sortedComments = noticia!!.comentarios!!.sortedByDescending {
                            try {
                                OffsetDateTime.parse(it.dataPostagem, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime()
                            } catch (e: DateTimeParseException) {
                                try {
                                    LocalDateTime.parse(it.dataPostagem, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                } catch (e2: DateTimeParseException) {
                                    Log.e("TelaDetalheNoticia", "Erro ao parsear data do comentário: ${it.dataPostagem} - ${e2.message}")
                                    LocalDateTime.MIN
                                }
                            }
                        }
                        items(sortedComments) { comentario ->
                            ComentarioCard(comentario = comentario)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaDetalheNoticiaPreview() {
    TelaDetalheNoticia(null, 1)
}