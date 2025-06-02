package br.senai.sp.jandira.bairronews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
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
                        TelaAdd(navController)
                    }
                    composable("server_offline") {
                        TelaServidorIndisponivel(navController)
                    }
                }
            }
        }
    }
}
