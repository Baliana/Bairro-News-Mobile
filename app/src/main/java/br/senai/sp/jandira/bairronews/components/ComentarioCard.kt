package br.senai.sp.jandira.bairronews.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun ComentarioCard(navHostController: NavHostController?) {
    var titulo by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(38.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCBC8C8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {


            Text(
                text = "Name User",
                fontSize = 14.sp,
                color = Color(0xFF3C3C3C)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = "",
                fontSize = 12.sp
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; isError = false },
                label = { Text(text = "Digite o seu comentario  ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError && titulo.isBlank()
            )

            Text(
                text = "data hora ",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ComentarioCardPreview() {
    ComentarioCard(null )
}
