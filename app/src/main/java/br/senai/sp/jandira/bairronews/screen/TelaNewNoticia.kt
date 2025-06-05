package br.senai.sp.jandira.bairronews.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.R
import br.senai.sp.jandira.bairronews.model.Endereco
import br.senai.sp.jandira.bairronews.model.NoticiaCreatePayload
import br.senai.sp.jandira.bairronews.model.Categoria
import br.senai.sp.jandira.bairronews.model.CategoriaResponse
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import br.senai.sp.jandira.bairronews.service.buscarCoordenadasComEndereco
import br.senai.sp.jandira.bairronews.service.uploadFileToAzure
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Adicione ExperimentalLayoutApi aqui
import androidx.compose.foundation.layout.ExperimentalLayoutApi // IMPORTANTE: Adicione esta importação

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // <-- CORREÇÃO AQUI
@Composable
fun TelaNewNoticia(navHostController: NavHostController?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var conteudo by remember { mutableStateOf("") }
    var enderecoInput by remember { mutableStateOf("") }
    var enderecoSelecionado by remember { mutableStateOf<Endereco?>(null) }
    var imageUrlsInput by remember { mutableStateOf("") }
    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    var availableCategories by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    val selectedCategoryIds = remember { mutableStateListOf<Int>() }

    var isError by remember { mutableStateOf(false) }
    var isLoadingAddress by remember { mutableStateOf(false) }
    var isUploadingImages by remember { mutableStateOf(false) }
    var showAddressError by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImageUris.clear()
        selectedImageUris.addAll(uris)
    }
    //O LaunchedEffect Envia uma requisição para buscar as categorias.
    //Se a resposta for OK, armazena essas categorias em uma variável (availableCategories), que provavelmente atualiza a UI.
    LaunchedEffect(Unit) {
        val call = RetrofitFactory().getNoticiaService().listAllCategorias()
        call.enqueue(object : Callback<CategoriaResponse> {
            override fun onResponse(call: Call<CategoriaResponse>, response: Response<CategoriaResponse>) {
                if (response.isSuccessful) {
                    availableCategories = response.body()?.categorias ?: emptyList()
                    Log.d("TelaNewNoticia", "Categorias carregadas: ${availableCategories.size}")
                } else {
                    Log.e("TelaNewNoticia", "Erro ao carregar categorias: ${response.code()}")
                    Toast.makeText(context, "Erro ao carregar categorias.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoriaResponse>, t: Throwable) {
                Log.e("TelaNewNoticia", "Falha na requisição de categorias: ${t.message}")
                Toast.makeText(context, "Erro de conexão ao carregar categorias.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0390E1),
                        Color(0xFF081E94)
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "Enviar uma nova notícia ",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = "Título *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it; isError = false },
                        label = { Text(text = "Digite o título da notícia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError && titulo.isBlank()
                    )

                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = "Categorias *",
                        fontSize = 12.sp
                    )

                    //flowrow utilizado para organizar os elementos
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableCategories.forEach { category ->
                            val isSelected = selectedCategoryIds.contains(category.id)
                            //Componente visual clicável que representa a categoria
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedCategoryIds.remove(category.id)
                                    } else {
                                        selectedCategoryIds.add(category.id)
                                    }
                                },
                                label = { Text(category.nome) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.LocationOn, contentDescription = "Selected") }
                                } else null,
                                border = if (!isSelected) BorderStroke(1.dp, Color.Gray) else null
                            )
                        }
                    }
                    if (isError && selectedCategoryIds.isEmpty()) {
                        Text(
                            text = "Selecione ao menos uma categoria.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = "Conteúdo *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = conteudo,
                        onValueChange = { conteudo = it; isError = false },
                        label = { Text(text = "Digite o conteúdo completo da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 200.dp),
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError && conteudo.isBlank()
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Use parágrafos separados por linha em branco para melhor formatação.",
                        fontSize = 10.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Endereço *",
                        fontSize = 12.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = enderecoInput,
                            onValueChange = {
                                enderecoInput = it
                                enderecoSelecionado = null
                                showAddressError = false
                            },
                            label = { Text(text = "Digite o endereço (Rua, Cidade, Estado)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            isError = showAddressError
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isLoadingAddress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    if (enderecoInput.isNotBlank()) {
                                        isLoadingAddress = true
                                        showAddressError = false
                                        coroutineScope.launch {
                                            val result = buscarCoordenadasComEndereco(enderecoInput)
                                            isLoadingAddress = false
                                            if (result != null) {
                                                enderecoSelecionado = result
                                                Toast.makeText(context, "Endereço validado!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                enderecoSelecionado = null
                                                showAddressError = true
                                                Toast.makeText(context, "Endereço não encontrado ou inválido.", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } else {
                                        showAddressError = true
                                        Toast.makeText(context, "O campo de endereço não pode estar vazio.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !isLoadingAddress
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = "Buscar Endereço")
                            }
                        }
                    }

                    if (showAddressError) {
                        Text(
                            text = "Endereço inválido ou não encontrado. Por favor, digite um endereço válido.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (enderecoSelecionado != null) {
                        Text(
                            text = "Endereço selecionado: ${enderecoSelecionado!!.displayName} (CEP: ${enderecoSelecionado!!.cep})",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Mídias (Imagens/Vídeos)",
                        fontSize = 12.sp
                    )
                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6))
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Selecionar Imagens")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selecionar Imagens da Galeria")
                    }
                    if (selectedImageUris.isNotEmpty()) {
                        Text(
                            text = "Imagens selecionadas: ${selectedImageUris.size}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imageUrlsInput,
                        onValueChange = { imageUrlsInput = it },
                        label = { Text(text = "Ou cole URLs de imagens/vídeos (uma por linha)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp, max = 150.dp),
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { navHostController?.navigate("home") },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Cancelar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                isError = false
                                showAddressError = false

                                if (titulo.isBlank() || conteudo.isBlank() || selectedCategoryIds.isEmpty()) {
                                    isError = true
                                    Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios (Título, Conteúdo, Categorias).", Toast.LENGTH_LONG).show()
                                    return@Button
                                }

                                if (enderecoSelecionado == null) {
                                    showAddressError = true
                                    Toast.makeText(context, "Por favor, valide o endereço.", Toast.LENGTH_LONG).show()
                                    return@Button
                                }

                                coroutineScope.launch {
                                    val allMediaUrls = mutableListOf<String>()

                                    if (selectedImageUris.isNotEmpty()) {
                                        isUploadingImages = true
                                        val sasToken = "sv=2022-11-02&ss=bfqt&srt=sco&sp=rwlacx&se=2025-12-31T23:59:59Z&st=2025-06-04T00:00:00Z&spr=https&sig=SUASIGNATURAAQUI"
                                        val storageAccount = "imgevideodenoticias"
                                        val containerName = "imagens"

                                        for (uri in selectedImageUris) {
                                            val file = getFileFromUri(context, uri)
                                            if (file != null) {
                                                val uploadedUrl = uploadFileToAzure(file, storageAccount, sasToken, containerName)
                                                if (uploadedUrl != null) {
                                                    allMediaUrls.add(uploadedUrl)
                                                    Log.d("TelaNewNoticia", "Uploaded: $uploadedUrl")
                                                } else {
                                                    Toast.makeText(context, "Falha ao carregar imagem: ${file.name}", Toast.LENGTH_LONG).show()
                                                    isUploadingImages = false
                                                    return@launch
                                                }
                                            } else {
                                                Toast.makeText(context, "Não foi possível obter o arquivo da URI: $uri", Toast.LENGTH_LONG).show()
                                                isUploadingImages = false
                                                return@launch
                                            }
                                        }
                                        isUploadingImages = false
                                    }

                                    if (imageUrlsInput.isNotBlank()) {
                                        val urlsFromInput = imageUrlsInput.split('\n')
                                            .map { it.trim() }
                                            .filter { it.isNotBlank() }
                                        allMediaUrls.addAll(urlsFromInput)
                                    }

                                    val dataPostagemFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                                    val noticiaData = NoticiaCreatePayload(
                                        titulo = titulo,
                                        conteudo = conteudo,
                                        tblUsuarioId = 1,
                                        endereco = enderecoSelecionado!!,
                                        urlsMidia = if (allMediaUrls.isNotEmpty()) allMediaUrls else null,
                                        categorias = selectedCategoryIds.toList(),
                                        dataPostagem = dataPostagemFormatada
                                    )

                                    val postCall = RetrofitFactory().getNoticiaService().saveNoticia(noticiaData)
                                    postCall.enqueue(object : Callback<br.senai.sp.jandira.bairronews.model.NoticiaItem> {
                                        override fun onResponse(call: Call<br.senai.sp.jandira.bairronews.model.NoticiaItem>, response: Response<br.senai.sp.jandira.bairronews.model.NoticiaItem>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Notícia publicada com sucesso!", Toast.LENGTH_SHORT).show()
                                                navHostController?.navigate("home") {
                                                    popUpTo("home") { inclusive = true }
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                Log.e("TelaNewNoticia", "Erro ao publicar notícia: ${response.code()} - $errorBody")
                                                Toast.makeText(context, "Erro ao publicar notícia: ${response.code()}", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<br.senai.sp.jandira.bairronews.model.NoticiaItem>, t: Throwable) {
                                            Log.e("TelaNewNoticia", "Falha na requisição de publicação: ${t.message}", t)
                                            Toast.makeText(context, "Erro de conexão ao publicar notícia.", Toast.LENGTH_LONG).show()
                                        }
                                    })
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoadingAddress && !isUploadingImages
                        ) {
                            if (isUploadingImages) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviando mídias...")
                            } else {
                                Text(text = "Publicar")
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getFileFromUri(context: android.content.Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val fileName = context.contentResolver.getFileName(uri) ?: return null
    val tempFile = File(context.cacheDir, fileName)

    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun android.content.ContentResolver.getFileName(uri: Uri): String? =
    when (uri.scheme) {
        "content" -> getCursorFileName(uri)
        "file" -> uri.lastPathSegment
        else -> null
    }

private fun android.content.ContentResolver.getCursorFileName(uri: Uri): String? {
    val cursor = query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                return it.getString(nameIndex)
            }
        }
    }
    return null
}

@Preview(showSystemUi = true)
@Composable
private fun TelaNewNoticiaPreview() {
    TelaNewNoticia(null)
}