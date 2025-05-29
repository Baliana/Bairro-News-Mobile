package br.senai.sp.jandira.bairronews.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.R
import br.senai.sp.jandira.bairronews.model.Login
import br.senai.sp.jandira.bairronews.model.User
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.tooling.preview.Preview
import br.senai.sp.jandira.bairronews.model.AuthenticationUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLogin(navController: NavHostController?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    fun fazerLogin() {
        val login = Login(email = email.trim(), senha = password.trim())
        val call = RetrofitFactory.getUserService().loginUser(login)

        call.enqueue(object : Callback<AuthenticationUser> {
            override fun onResponse(
                call: Call<AuthenticationUser>,
                response: Response<AuthenticationUser>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null && responseBody.status && responseBody.usuario != null) {
                        val usuario = responseBody.usuario!!

                        // Salvar apenas parte do usuário
                        val shared = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString("user_id", usuario.id.toString())
                        editor.putString("user_name", usuario.nome.trim())
                        editor.putString("user_email", usuario.email.trim())
                        editor.putString("user_biografia", usuario.biografia ?: "")
                        editor.putString("user_foto", usuario.fotoPerfil ?: "")
                        editor.apply()

                        navController?.navigate("home_user")
                    } else {
                        isError = true
                        errorMessage = responseBody?.messagem ?: "Erro desconhecido"
                    }
                } else {
                    isError = true
                    errorMessage = context.getString(R.string.login_invalido)
                }
            }

            override fun onFailure(call: Call<AuthenticationUser>, t: Throwable) {
                isError = true
                errorMessage = context.getString(R.string.erro_conexao)
                Log.e("LOGIN_ERROR", "Erro: ${t.message}")
            }
        })
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.Login),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // E-mail
            Text(
                text = stringResource(R.string.email),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 64.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isError = false
                },
                label = { Text(stringResource(R.string.email)) },
                placeholder = { Text(stringResource(R.string.email_digitar)) },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Ícone de e-mail", tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            Text(
                text = stringResource(R.string.senha),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isError = false
                },
                label = { Text(stringResource(R.string.senha)) },
                placeholder = { Text(stringResource(R.string.senha_digitar)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Ícone de senha") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isError
            )

            if (isError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    if (email.isBlank() || !email.contains("@")) {
                        isError = true
                        errorMessage = context.getString(R.string.email_invalido)
                    } else if (password.length < 4) {
                        isError = true
                        errorMessage = context.getString(R.string.senha_invalida)
                    } else {
                        fazerLogin()
                        navController?.navigate("home")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DA1F2),
                    contentColor = Color.White
                )
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.nao_conta))
                Text(
                    text = stringResource(R.string.cadastrar),
                    color = Color(0xFF1DA1F2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController?.navigate("cadastro")
                    }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.concordar_termos),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = stringResource(R.string.termos),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
                Text(" e ")
                Text(
                    text = stringResource(R.string.politica),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun TelaLoginPreview() {
    TelaLogin(null)
}
