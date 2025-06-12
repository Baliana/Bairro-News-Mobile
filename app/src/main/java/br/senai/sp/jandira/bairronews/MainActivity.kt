package br.senai.sp.jandira.bairronews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.senai.sp.jandira.bairronews.screen.*
import br.senai.sp.jandira.bairronews.ui.theme.BairroNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BairroNewsTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "decisao"
                ) {
                    composable("decisao") {
                        TelaDecisaoInicial(navController)
                    }
                    composable("login") {
                        TelaLogin(navController)
                    }
                    composable("cadastro") {
                        TelaCadastro(navController)
                    }
                    composable("home") {
                        TelaHome(navController)
                    }
                    composable("telanew") {
                        TelaNewNoticia(navController)
                    }
                    composable("perfil") {
                        TelaPerfil(navController)
                    }
                    composable("home") {
                        TelaHome(navController)
                    }
                    // Nova rota para a tela de detalhes da notícia
                    composable(
                        "noticiaDetalhes/{noticiaId}", // O {noticiaId} indica um argumento
                        arguments = listOf(navArgument("noticiaId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val noticiaId = backStackEntry.arguments?.getInt("noticiaId")
                        TelaDetalheNoticia(navController = navController, noticiaId = noticiaId)
                    }
                    composable("server_offline") {
                        TelaServidorIndisponivel(navController)
                    }
                }
            }
        }
    }
}
