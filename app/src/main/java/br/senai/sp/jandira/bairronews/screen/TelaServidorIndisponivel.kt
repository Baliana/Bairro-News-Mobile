package br.senai.sp.jandira.bairronews.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.R
import br.senai.sp.jandira.bairronews.ui.theme.BairroNewsTheme

@Composable
fun TelaServidorIndisponivel(navController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ops!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "O servidor está offline ou inacessível.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Verifique sua conexão com a internet ou tente novamente mais tarde.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                // Tenta voltar para a TelaDecisaoInicial para reavaliar
                navController?.navigate("decisao") {
                    popUpTo("server_offline") { inclusive = true } // Remove a tela atual
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Tentar Novamente", fontSize = 18.sp)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewTelaServidorIndisponivel() {
    BairroNewsTheme {
        TelaServidorIndisponivel(null)
    }
}