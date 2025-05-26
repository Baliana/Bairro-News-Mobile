package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun TelaHome(navController: NavHostController?) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)

    val nome = sharedPref.getString("nome", "Nome não encontrado") ?: ""
    val email = sharedPref.getString("email", "Email não encontrado") ?: ""
    val biografia = sharedPref.getString("biografia", null)
    val fotoPerfil = sharedPref.getString("foto_perfil", null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        if (!fotoPerfil.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(fotoPerfil),
                contentDescription = "Foto de Perfil",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
                    .clip(CircleShape)
            )
        }

        Text(
            text = nome,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!biografia.isNullOrEmpty()) {
            Text(
                text = biografia,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TelaHomePreview() {
    TelaHome(null)
}
