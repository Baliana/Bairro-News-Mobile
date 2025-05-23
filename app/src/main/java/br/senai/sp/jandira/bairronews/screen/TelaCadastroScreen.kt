package br.senai.sp.jandira.bairronews.screen

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.R
import br.senai.sp.jandira.bairronews.model.User
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

fun formatarData(data: String): String {
    val partes = data.split("/")
    return if (partes.size == 3) {
        val dia = partes[0].padStart(2, '0')
        val mes = partes[1].padStart(2, '0')
        val ano = partes[2]
        "$ano-$mes-$dia"
    } else {
        ""
    }
}

@Composable
fun TelaCadastro(navController: NavHostController?) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dataNascimento = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.cadastro_user),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.complement_name),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )

            OutlinedTextField(
                value = nome,
                onValueChange = {
                    nome = it
                    isError = false
                },
                label = { Text(text = stringResource(R.string.Digite_name)) },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (isError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.email),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                placeholder = { Text(stringResource(R.string.email_digitar)) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.senha),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.senha)) },
                placeholder = { Text(stringResource(R.string.senha_digitar)) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Confirmar senha",
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar senha") },
                placeholder = { Text("Digite novamente a senha") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Data de nascimento",
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            OutlinedTextField(
                value = dataNascimento,
                onValueChange = {},
                label = { Text("Data de nascimento") },
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = false,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nome.length < 3) {
                        isError = true
                        errorMessage = context.getString(R.string.support_name)
                    } else {
                        val user = User(
                            nome = nome.trim(),
                            email = email.trim(),
                            senha = password.trim(),
                            dataDeNascimento = formatarData(dataNascimento),
                            biografia = null,
                            fotoPerfil = null
                        )

                        val call = RetrofitFactory.getUserService().saveUser(user)
                        call.enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                if (response.isSuccessful) {
                                    val userResponse = response.body()
                                    userResponse?.let {
                                        val sharedPref = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                                        with(sharedPref.edit()) {
                                            putInt("id", it.id ?: 0)
                                            putString("nome", it.nome)
                                            putString("email", it.email)
                                            apply()
                                        }
                                        navController?.navigate("home")
                                    }
                                } else {
                                    isError = true
                                    errorMessage = "Erro ao cadastrar: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                isError = true
                                errorMessage = "Erro de conexão: ${t.message}"
                            }
                        })
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cadastrar", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.ja_tem_conta))
                Text(
                    text = stringResource(R.string.Entre),
                    color = Color(0xFF1DA1F2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController?.navigate("login")
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TelaCadastroPreview() {
    TelaCadastro(null)
}
