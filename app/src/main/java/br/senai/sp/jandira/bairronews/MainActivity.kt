package br.senai.sp.jandira.bairronews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import br.senai.sp.jandira.bairronews.ui.theme.BairroNewsTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import br.senai.sp.jandira.bairronews.screen.TelaCadastro
import br.senai.sp.jandira.bairronews.screen.TelaCadastroScreen
import br.senai.sp.jandira.bairronews.screen.TelaHome
import br.senai.sp.jandira.bairronews.screen.TelaLogin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BairroNewsTheme {
                var navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ){
                    composable(
                        route = "home"
                    ){
                        TelaHome(navController)
                    }
                    composable(
                        route = "login"
                    ){
                        TelaLogin(navController)
                    }
                    composable(
                        route = "cadastro"
                    ) {
                        TelaCadastro(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BairroNewsTheme {
        Greeting("Android")
    }
}