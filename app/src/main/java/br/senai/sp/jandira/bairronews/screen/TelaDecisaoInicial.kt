package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun TelaDecisaoInicial(navController: NavHostController) {
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    val nome = sharedPref.getString("nome", null)
    val email = sharedPref.getString("email", null)

    LaunchedEffect(Unit) {
        if (nome.isNullOrEmpty() || email.isNullOrEmpty()) {
            navController.navigate("login") {
                popUpTo("decisao") { inclusive = true }
            }
        } else {
            navController.navigate("home") {
                popUpTo("decisao") { inclusive = true }
            }
        }
    }
}
