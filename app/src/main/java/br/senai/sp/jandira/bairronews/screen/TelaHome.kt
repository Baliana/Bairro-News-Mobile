package br.senai.sp.jandira.bairronews.screen
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
// Importe rememberNavController para o preview
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.bairronews.R
import br.senai.sp.jandira.bairronews.components.NoticiaCard
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.NoticiaResponse
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import br.senai.sp.jandira.bairronews.ui.theme.BairroNewsTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHome(navController: NavHostController?) {
    // Variável para armazenar a lista de notícias
    var noticiasList by remember { mutableStateOf(listOf<NoticiaItem>()) }
    val context = LocalContext.current

    // Função para buscar as notícias da API
    fun fetchNoticias() {
        val call = RetrofitFactory().getNoticiaService().listAllNoticias()

        call.enqueue(object : Callback<NoticiaResponse> {
            override fun onResponse(call: Call<NoticiaResponse>, response: Response<NoticiaResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.status) {
                        noticiasList = responseBody.noticias ?: listOf()
                        Log.d("TelaHome", "Notícias carregadas: ${noticiasList.size}")
                    } else {
                        Log.e("TelaHome", "Erro na resposta da API: ${responseBody?.mensagem}")

                    }
                } else {
                    Log.e("TelaHome", "Resposta não bem-sucedida: ${response.code()}")

                }
            }

            override fun onFailure(call: Call<NoticiaResponse>, t: Throwable) {
                Log.e("TelaHome", "Erro na requisição: ${t.message}")

            }
        })
    }


    LaunchedEffect(Unit) {
        fetchNoticias()
    }

    // Função para deslogar
    fun fazerLogout() {
        val sharedPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
        navController?.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top Bar (Header)
        Card(
            modifier = Modifier
                .padding(top = 50.dp)
                .height(52.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        fontSize = 18.sp,
                        text = "Bairro News",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { navController?.navigate("telanew") }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = Color.Black,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { navController?.navigate("perfil") }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuário",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { fazerLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(noticiasList) { noticia ->
                NoticiaCard(noticia = noticia) { id ->
                    navController?.navigate("noticiaDetalhes/${id}")
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun TelaHomePreview() {
    BairroNewsTheme {
        TelaHome(null)
    }
}