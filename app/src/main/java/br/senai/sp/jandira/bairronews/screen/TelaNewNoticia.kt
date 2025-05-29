package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun TelaAdd(navHostController: NavHostController?) {
    val titulo = remember { mutableStateOf("") }
    val categoria = remember { mutableStateOf("") }
    val resumo = remember { mutableStateOf("") }
    val conteudo = remember { mutableStateOf("") }
    val imagemUrl = remember { mutableStateOf("") }
    val autor = remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) }

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
                    .padding(top = 25.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 35.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    // Título *
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = "Título *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = titulo.value,
                        onValueChange = {
                            titulo.value = it
                            isError.value = false
                        },
                        label = { Text(text = "Digite o título da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )

                    // Categoria *
                    Text(
                        modifier = Modifier.padding(top = 30.dp),
                        text = "Categoria *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = categoria.value,
                        onValueChange = {
                            categoria.value = it
                            isError.value = false
                        },
                        label = { Text(text = "Categoria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )

                    // Resumo *
                    Text(
                        modifier = Modifier.padding(top = 34.dp),
                        text = "Resumo *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = resumo.value,
                        onValueChange = {
                            resumo.value = it
                            isError.value = false
                        },
                        label = { Text(text = "Digite um breve resumo da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )

                    // Conteúdo *
                    Text(
                        modifier = Modifier.padding(top = 25.dp),
                        text = "Conteúdo *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = conteudo.value,
                        onValueChange = {
                            conteudo.value = it
                            isError.value = false
                        },
                        label = { Text(text = "Digite o conteúdo completo da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(116.dp),
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Use parágrafos separados por linha em branco para melhor formatação.",
                        fontSize = 10.sp
                    )

                    // URL da imagem *
                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = "URL da imagem *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = imagemUrl.value,
                        onValueChange = {
                            imagemUrl.value = it
                            isError.value = false
                        },
                        label = { Text(text = "https://example.com/image.jpg") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Cole a URL de uma imagem que represente a notícia.",
                        fontSize = 12.sp
                    )

                    // Seu nome *
                    Text(
                        modifier = Modifier.padding(top = 24.dp),
                        text = "Seu nome *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = autor.value,
                        onValueChange = {
                            autor.value = it
                            isError.value = false
                        },
                        label = { Text(text = "Digite seu nome") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isError.value
                    )

                    // Botões
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 44.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {navHostController?.navigate("home")},
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
                            onClick = {navHostController?.navigate("home")},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
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

@Preview(showSystemUi = true)
@Composable
private fun TelaAddPreview() {
    TelaAdd(null)
}
