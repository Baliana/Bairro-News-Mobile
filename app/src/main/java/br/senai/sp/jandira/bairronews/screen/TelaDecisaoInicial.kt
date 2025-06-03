package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.model.NoticiaResponse // Importe NoticiaResponse para o callback
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun TelaDecisaoInicial(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("TelaDecisaoInicial", "Verificando status da API...")

        val call = RetrofitFactory().getNoticiaService().listAllNoticias() // Usamos listAllNoticias para o health check

        call.enqueue(object : Callback<NoticiaResponse> {
            override fun onResponse(call: Call<NoticiaResponse>, response: Response<NoticiaResponse>) {
                if (response.isSuccessful) {
                    // API está respondendo, agora verifica o login
                    Log.d("TelaDecisaoInicial", "API está online. Verificando dados do usuário.")
                    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE) // Usar "user" como chave, como usado no login
                    val userId = sharedPref.getString("user_id", null) // É mais seguro verificar um ID
                    val userName = sharedPref.getString("user_name", null) // Ou o nome/email

                    if (userId.isNullOrEmpty() || userName.isNullOrEmpty()) { // Verificação mais robusta
                        Log.d("TelaDecisaoInicial", "Usuário não logado. Navegando para 'login'.")
                        navController.navigate("login") {
                            popUpTo("decisao") { inclusive = true }
                        }
                    } else {
                        Log.d("TelaDecisaoInicial", "Usuário logado. Navegando para 'home'.")
                        navController.navigate("home") {
                            popUpTo("decisao") { inclusive = true }
                        }
                    }
                } else {
                    Log.e("TelaDecisaoInicial", "API respondeu com erro HTTP: ${response.code()} - ${response.message()}")
                    Log.d("TelaDecisaoInicial", "Servidor pode estar no ar, mas endpoint inacessível. Navegando para 'server_offline'.")
                    navController.navigate("server_offline") {
                        popUpTo("decisao") { inclusive = true }
                    }
                }
            }

            override fun onFailure(call: Call<NoticiaResponse>, t: Throwable) {
                // Erro de conexão (servidor offline, IP errado, sem internet, etc.)
                Log.e("TelaDecisaoInicial", "Falha na conexão com a API: ${t.message}", t)
                Log.d("TelaDecisaoInicial", "Servidor está offline. Navegando para 'server_offline'.")
                navController.navigate("server_offline") {
                    popUpTo("decisao") { inclusive = true }
                }
            }
        })
    }
}