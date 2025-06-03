package br.senai.sp.jandira.bairronews.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.bairronews.model.NoticiaCreatePayload
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdd(navHostController: NavHostController?) {
    val titulo = remember { mutableStateOf("") }
    val categoria = remember { mutableStateOf("") }
    val resumo = remember { mutableStateOf("") }
    val conteudo = remember { mutableStateOf("") }
    val endereco = remember { mutableStateOf("") }
    val imagemUrl = remember { mutableStateOf("") }
    var dataPostagem by remember { mutableStateOf("") }
    val autor = remember { mutableStateOf("") }
    var isFormError by remember { mutableStateOf(false) }

    var isDateError by remember { mutableStateOf(false) }

    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            dataPostagem = String.format("%02d/%02d/%d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
            isDateError = false
        }, mYear, mMonth, mDay
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0390E1),
                        Color(0xFF081E94)
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "Enviar uma nova notícia ",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = "Título *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = titulo.value,
                        onValueChange = {
                            titulo.value = it
                            isFormError = false
                        },
                        label = { Text(text = "Digite o título da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isFormError && titulo.value.isBlank()
                    )

                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = "Categoria *",
                        fontSize = 12.sp
                    )

                    var expanded by remember { mutableStateOf(false)}
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isFormError && categoria.value.isBlank()) Color.Red else Color.Gray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = if (categoria.value.isNotEmpty()) categoria.value else "Selecione uma categoria",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Alta Relevancia", "Media Relevancia", "Baixa Relevancia").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        categoria.value = option
                                        expanded = false
                                        isFormError = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        modifier = Modifier.padding(top = 14.dp),
                        text = "Resumo *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = resumo.value,
                        onValueChange = {
                            resumo.value = it
                            isFormError = false
                        },
                        label = { Text(text = "Digite um breve resumo da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isFormError && resumo.value.isBlank()
                    )

                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = "Conteúdo *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = conteudo.value,
                        onValueChange = {
                            conteudo.value = it
                            isFormError = false
                        },
                        label = { Text(text = "Digite o conteúdo completo da notícia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp),
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp),
                        isError = isFormError && conteudo.value.isBlank()
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Use parágrafos separados por linha em branco para melhor formatação.",
                        fontSize = 10.sp
                    )

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "URL da imagem *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = imagemUrl.value,
                        onValueChange = {
                            imagemUrl.value = it
                            isFormError = false
                        },
                        label = { Text(text = "https://example.com/image.jpg") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isFormError && imagemUrl.value.isBlank()
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Cole a URL de uma imagem que represente a notícia.",
                        fontSize = 7.sp
                    )

                    Text(text = "Data de postagem *", fontSize = 12.sp)
                    OutlinedTextField(
                        value = dataPostagem,
                        onValueChange = {
                            dataPostagem = it
                            isDateError = false
                        },
                        label = { Text("DD/MM/AAAA") },
                        placeholder = { Text("DD/MM/AAAA") },
                        readOnly = true,
                        leadingIcon = {
                            IconButton(onClick = { mDatePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Selecionar data")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = isDateError
                    )

                    if (isDateError) {
                        Text(
                            text = "A data de postagem é obrigatória.",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 14.dp),
                        text = "Seu nome *",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = autor.value,
                        onValueChange = {
                            autor.value = it
                            isFormError = false
                        },
                        label = { Text(text = "Digite seu nome") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isFormError && autor.value.isBlank()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {navHostController?.navigate("home")},
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Cancelar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                isFormError = false
                                isDateError = false

                                val camposPreenchidos = titulo.value.isNotBlank() &&
                                        categoria.value.isNotBlank() &&
                                        resumo.value.isNotBlank() &&
                                        conteudo.value.isNotBlank() &&
                                        imagemUrl.value.isNotBlank() &&
                                        autor.value.isNotBlank() &&
                                        dataPostagem.isNotBlank()

                                val categoriaLimpa = categoria.value.trim().lowercase()
                                val categoriasSelecionadas: List<Int> = when (categoriaLimpa) {
                                    "alta relevancia" -> listOf(1)
                                    "media relevancia" -> listOf(2)
                                    "baixa relevancia" -> listOf(3)
                                    else -> listOf()
                                }

                                if (camposPreenchidos && categoriasSelecionadas.isNotEmpty()) {
                                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    val parsedDate = LocalDate.parse(dataPostagem, formatter)
                                    // Voltando para ISO_LOCAL_DATE_TIME para incluir a hora (00:00:00)
                                    val apiFormattedDate = parsedDate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)


                                    val noticia = NoticiaCreatePayload(
                                        titulo = titulo.value,
                                        conteudo = conteudo.value,
                                        categorias = categoriasSelecionadas,
                                        tblUsuarioId = 1,
                                        tblEnderecoId = 1,
                                        urlsMidia = listOf(imagemUrl.value),
                                        dataPostagem = apiFormattedDate
                                    )

                                    val call = RetrofitFactory().getNoticiaService().saveNoticia(noticia)
                                    call.enqueue(object : retrofit2.Callback<NoticiaItem> {
                                        override fun onResponse(call: Call<NoticiaItem>, response: Response<NoticiaItem>) {
                                            if (response.isSuccessful) {
                                                navHostController?.navigate("home")
                                            } else {
                                                println("Erro ao postar: ${response.code()}")
                                                println("Detalhes do erro: ${response.errorBody()?.string()}") // Adicionado para depuração
                                            }
                                        }

                                        override fun onFailure(call: Call<NoticiaItem>, t: Throwable) {
                                            println("Erro de rede: ${t.message}")
                                            t.printStackTrace() // Adicionado para depuração
                                        }
                                    })
                                } else {
                                    isFormError = true
                                    if (dataPostagem.isBlank()) {
                                        isDateError = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Publicar")
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TelaAddPreview() {
    TelaAdd(null)
}