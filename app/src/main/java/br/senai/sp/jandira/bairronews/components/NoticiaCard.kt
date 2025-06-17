package br.senai.sp.jandira.bairronews.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.bairronews.model.NoticiaItem
import br.senai.sp.jandira.bairronews.model.Categoria
import br.senai.sp.jandira.bairronews.model.Endereco
import coil.compose.rememberAsyncImagePainter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import androidx.compose.runtime.remember // Adicionar esta importação

@Composable
fun NoticiaCard(
    noticia: NoticiaItem,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit, // <--- Este é o parâmetro obrigatório sem valor padrão
    onDeleteClick: (Int) -> Unit = {}
) {
    // Obter o Context do Compose
    val context = LocalContext.current
    // Acessar SharedPreferences para obter o user_id
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val currentUserIdString = sharedPreferences.getString("user_id", null)
    val currentUserId = currentUserIdString?.toIntOrNull() // Converte para Int se possível

    val imageUrl =
        if (noticia.urlsMidia != null && noticia.urlsMidia.isNotEmpty()) {
            noticia.urlsMidia[0].urlMidia
        } else {
            "https://via.placeholder.com/600x400/CCCCCC/FFFFFF?text=Sem+Imagem"
        }

    val dataFormatada = remember(noticia.dataPostagem) { // Usar remember para otimizar o cálculo
        // *** LOG DA DATA BRUTA AQUI ***
        Log.d("NoticiaCard", "DataPostagem bruta recebida: '${noticia.dataPostagem}' para a notícia ID: ${noticia.id}")

        try {
            // Tenta o formato ISO 8601 completo, incluindo milissegundos e fuso horário
            // Ex: "2024-05-28T00:00:00.000Z"
            val apiDateFormatFull = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val parsedDateTime = OffsetDateTime.parse(noticia.dataPostagem, apiDateFormatFull)
            parsedDateTime.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'às' HH:mm", Locale("pt", "BR")))
        } catch (e: DateTimeParseException) {
            // Se o formato ISO_OFFSET_DATE_TIME falhar, tenta apenas o formato de data local
            try {
                // Ex: "2024-05-28"
                val apiDateFormatLocal = DateTimeFormatter.ISO_LOCAL_DATE
                val parsedDate = LocalDate.parse(noticia.dataPostagem, apiDateFormatLocal)
                parsedDate.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))) // Sem horas
            } catch (e2: DateTimeParseException) {
                // Se ambos falharem, loga o erro e retorna um valor padrão
                Log.e("NoticiaCard", "Erro ao parsear data: ${noticia.dataPostagem} - ${e2.message}", e2)
                "Data Indisponível"
            }
        } catch (e: Exception) {
            // Captura qualquer outra exceção inesperada
            Log.e("NoticiaCard", "Erro inesperado ao formatar data: ${e.message}", e)
            "Data Indisponível"
        }
    }


    val categoriaNome =
        if (noticia.categorias != null && noticia.categorias.isNotEmpty()) {
            noticia.categorias[0].nome
        } else {
            "Geral"
        }

    val enderecoReduzido = if (noticia.endereco != null) {
        when {
            !noticia.endereco.displayName.isNullOrEmpty() -> noticia.endereco.displayName
            !noticia.endereco.localidade.isNullOrEmpty() && !noticia.endereco.bairro.isNullOrEmpty() ->
                "${noticia.endereco.bairro}, ${noticia.endereco.localidade}"

            !noticia.endereco.localidade.isNullOrEmpty() -> noticia.endereco.localidade
            !noticia.endereco.bairro.isNullOrEmpty() -> noticia.endereco.bairro
            else -> null
        }
    } else {
        null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { noticia.id?.let { onClick(it) } },
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = noticia.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // Condição para exibir o botão de deletar:
            // O usuário logado deve ser o proprietário da notícia
            if (currentUserId != null && noticia.tblUsuarioId != null && currentUserId == noticia.tblUsuarioId) {
                IconButton(
                    onClick = {
                        noticia.id?.let {
                            onDeleteClick(it)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Red.copy(alpha = 0.7f), MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Deletar Notícia",
                        tint = Color.White
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = categoriaNome.uppercase(),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Red, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = noticia.titulo,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dataFormatada,
                    color = Color.LightGray,
                    fontSize = 12.sp
                )

                if (!enderecoReduzido.isNullOrEmpty()) {
                    Text(
                        text = enderecoReduzido,
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoticiaCardPreview() {
    val sampleNoticiaDoUsuarioLogado = NoticiaItem(
        id = 1,
        titulo = "Novo centro comunitário será inaugurado em Breve com muitas novidades para a comunidade",
        conteudo = "Conteúdo da notícia (não visível neste card)",
        dataPostagem = "2024-05-28T00:00:00.000Z", // Exemplo de data ISO 8601 completa
        categorias = listOf(Categoria(id = 1, nome = "Esporte", descricao = "Esporte da comunidade", sigla = "ESP")),
        urlsMidia = listOf(br.senai.sp.jandira.bairronews.model.UrlMidiaItem(urlMidia = "https://via.placeholder.com/600x400/FF0000/FFFFFF?text=Placeholder")),
        endereco = Endereco(cep = "00000-000", lat = -23.0, lon = -46.0, displayName = "Rua das Flores, 123 - Centro, São Paulo"),
        tblUsuarioId = 123 // ID do usuário logado no preview
    )

    val sampleNoticiaDeOutroUsuario = NoticiaItem(
        id = 2,
        titulo = "Reunião de moradores para discutir segurança",
        conteudo = "Conteúdo da notícia (não visível neste card)",
        dataPostagem = "2024-05-29T10:30:00.000Z", // Exemplo de data ISO 8601 completa
        categorias = listOf(Categoria(id = 2, nome = "Segurança", descricao = "Segurança pública", sigla = "SEG")),
        urlsMidia = emptyList(),
        endereco = null,
        tblUsuarioId = 456 // ID de outro usuário
    )

    Column {
        NoticiaCard(
            noticia = sampleNoticiaDoUsuarioLogado,
            onClick = { noticiaId -> Log.d("NoticiaCardPreview", "Clicou na notícia com ID: $noticiaId") },
            onDeleteClick = { noticiaId -> Log.d("NoticiaCardPreview", "Deletar notícia (simulado) com ID: $noticiaId") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        NoticiaCard(
            noticia = sampleNoticiaDeOutroUsuario,
            onClick = { noticiaId -> Log.d("NoticiaCardPreview", "Clicou na notícia com ID: $noticiaId") },
            onDeleteClick = { noticiaId -> Log.d("NoticiaCardPreview", "Deletar notícia (simulado) com ID: $noticiaId") }
        )
    }
}