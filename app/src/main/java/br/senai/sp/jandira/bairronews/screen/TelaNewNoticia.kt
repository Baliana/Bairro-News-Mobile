package br.senai.sp.jandira.bairronews.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun TelaAdd(navHostController: NavHostController?) {
    val titulo = remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(top = 64.dp),
                text = "Enviar nova notícia",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            // Título *
            Text(
                modifier = Modifier.padding(top = 64.dp, bottom = 4.dp),
                text = "Título *",
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = titulo.value,
                onValueChange = {
                    titulo.value = it
                    isError.value = false
                },
                label = { Text(text = "Digite o título da notícia ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError.value
            )
            // Categoria * em categoria vms fazer um lista de opcao para o usuario escolher
            Text(
                modifier = Modifier.padding(top = 44.dp, bottom = 4.dp),
                text = "Categoria *",
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = titulo.value,
                onValueChange = {
                    titulo.value = it
                    isError.value = false
                },
                label = { Text(text = "Categoria *") },
                placeholder = { Text(text = "") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError.value
            )
            
            //Resumo *
            Text(
                modifier = Modifier.padding(top = 44.dp, bottom = 4.dp),
                text = "Resumo *",
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = titulo.value,
                onValueChange = {
                    titulo.value = it
                    isError.value = false
                },
                label = { Text(text = "Digite um breve resumo da notícia") },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError.value
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TelaAddPreview() {
    TelaAdd(null)
}
