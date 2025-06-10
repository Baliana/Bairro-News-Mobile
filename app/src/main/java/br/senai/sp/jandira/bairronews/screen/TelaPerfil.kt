import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

// Função para sair de perfeil
fun fazerLogout(context: Context, navController: NavController) {
    val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.clear()
    editor.apply()

    navController.navigate("home") {
        popUpTo("home") { inclusive = true }
    }
}

@Composable
fun TelaPerfil(navController: NavController?) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(26.dp),
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Sair",
            tint = Color.Black
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 130.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 80.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                ) {
                    Text("marrasdas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text("Repórter Local",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Sobre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Jornalista especializada em notícias locais com mais de 5 anos de experiência.\n" +
                                "Apaixonada por contar histórias que fazem a diferença em nossa comunidade.",
                        fontSize = 14.sp
                    )
                }
            }
        }

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
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    tint = Color.Gray,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TelaPerfilPreview() {
    TelaPerfil(null)
}
