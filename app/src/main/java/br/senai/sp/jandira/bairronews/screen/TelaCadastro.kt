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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import br.senai.sp.jandira.bairronews.model.AuthenticationUser


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

    // Estados de erro por campo
    var nomeError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var confirmSenhaError by remember { mutableStateOf<String?>(null) }
    var dataError by remember { mutableStateOf<String?>(null) }
    var cadastroError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dataNascimento = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dataError = null
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

            // Nome
            Text(text = stringResource(R.string.complement_name), fontSize = 12.sp)
            OutlinedTextField(
                value = nome,
                onValueChange = {
                    nome = it
                    nomeError = null
                },
                label = { Text(stringResource(R.string.Digite_name)) },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                isError = nomeError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            nomeError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(text = stringResource(R.string.email), fontSize = 12.sp)
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text(stringResource(R.string.email)) },
                placeholder = { Text(stringResource(R.string.email_digitar)) },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            emailError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            Text(text = stringResource(R.string.senha), fontSize = 12.sp)
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    senhaError = null
                },
                label = { Text(stringResource(R.string.senha)) },
                placeholder = { Text(stringResource(R.string.senha_digitar)) },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val visibilityIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(visibilityIcon, contentDescription = null)
                    }
                },
                isError = senhaError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            senhaError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar Senha
            Text(text = "Confirmar senha", fontSize = 12.sp)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmSenhaError = null
                },
                label = { Text("Confirmar senha") },
                placeholder = { Text("Digite novamente a senha") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmSenhaError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            confirmSenhaError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data de nascimento
            Text(text = "Data de nascimento", fontSize = 12.sp)
            OutlinedTextField(
                value = dataNascimento,
                onValueChange = {},
                label = { Text("Data de nascimento") },
                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = false,
                readOnly = true,
                isError = dataError != null
            )
            dataError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de cadastro
            Button(
                onClick = {
                    // Validações
                    var valid = true

                    if (nome.length < 3) {
                        nomeError = context.getString(R.string.support_name)
                        valid = false
                    }

                    if (!email.contains("@") || email.length < 5) {
                        emailError = "Email inválido"
                        valid = false
                    }

                    if (password.length < 6) {
                        senhaError = "A senha deve ter no mínimo 6 caracteres"
                        valid = false
                    }

                    if (confirmPassword != password) {
                        confirmSenhaError = "As senhas não coincidem"
                        valid = false
                    }

                    if (dataNascimento.isBlank()) {
                        dataError = "Escolha uma data"
                        valid = false
                    }

                    if (valid) {
                        val user = User(
                            nome = nome.trim(),
                            email = email.trim(),
                            senha = password.trim(),
                            dataDeNascimento = formatarData(dataNascimento),
                            biografia = null,
                            fotoPerfil = "https://www.pngall.com/wp-content/uploads/5/User-Profile-PNG-Image.png"
                        )

                        val call = RetrofitFactory.getUserService().saveUser(user)
                        call.enqueue(object : Callback<AuthenticationUser> {
                            override fun onResponse(call: Call<AuthenticationUser>, response: Response<AuthenticationUser>) {
                                if (response.isSuccessful) {
                                    val userResponse = response.body()?.usuario // acessa o objeto `usuario`
                                    userResponse?.let {
                                        val sharedPref = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                                        with(sharedPref.edit()) {
                                            putInt("id", it.id ?: 0)
                                            putString("nome", it.nome)
                                            putString("email", it.email)
                                            apply()
                                        }
                                        navController?.navigate("login")
                                    }
                                } else {
                                    cadastroError = "Erro ao cadastrar: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<AuthenticationUser>, t: Throwable) {
                                cadastroError = "Erro de conexão: ${t.message}"
                            }
                        })

                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DA1F2),
                    contentColor = Color.White
                )
            ) {
                Text("Cadastrar", fontWeight = FontWeight.Bold)
            }

            // Exibe erro de cadastro, se houver
            cadastroError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link para login
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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
