package br.senai.sp.jandira.bairronews.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import java.time.OffsetDateTime // Importar OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun NoticiaCard(noticia: NoticiaItem, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    val imageUrl =
        if (noticia.urlsMidia != null && noticia.urlsMidia.isNotEmpty()) {
            noticia.urlsMidia[0].urlMidia
        } else {
            "https://via.placeholder.com/600x400/CCCCCC/FFFFFF?text=Sem+Imagem"
        }

    val dataFormatada = try {
        // Tenta parsear como OffsetDateTime (para o 'Z' final) e depois converter para LocalDate
        val parsedDateTime = OffsetDateTime.parse(noticia.dataPostagem, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        parsedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))
    } catch (e: DateTimeParseException) {
        // Se falhar, tenta como LocalDate (caso a API não envie a parte da hora/fuso em alguns casos)
        try {
            val parsedDate = LocalDate.parse(noticia.dataPostagem, DateTimeFormatter.ISO_LOCAL_DATE)
            parsedDate.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))
        } catch (e2: DateTimeParseException) {
            Log.e("NoticiaCard", "Erro ao parsear data: ${noticia.dataPostagem} - ${e2.message}")
            "Data Indisponível"
        }
    } catch (e: Exception) {
        Log.e("NoticiaCard", "Erro inesperado ao formatar data: ${e.message}")
        "Data Indisponível"
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
    val sampleNoticiaComTudo = NoticiaItem(
        id = 1,
        titulo = "Novo centro comunitário será inaugurado em Breve com muitas novidades para a comunidade",
        conteudo = "Conteúdo da notícia (não visível neste card)",
        dataPostagem = "2024-05-28T00:00:00.000Z", // Exemplo com o formato completo
        categorias = listOf(Categoria(id = 1, nome = "Esporte", descricao = "Esporte da comunidade", sigla = "ESP")),
        urlsMidia = listOf(br.senai.sp.jandira.bairronews.model.UrlMidiaItem(urlMidia = "https://via.placeholder.com/600x400/FF0000/FFFFFF?text=Placeholder")),
        endereco = Endereco(cep = "00000-000", lat = -23.0, lon = -46.0, displayName = "Rua das Flores, 123 - Centro, São Paulo")
    )

    val sampleNoticiaSemMidiaSemEndereco = NoticiaItem(
        id = 2,
        titulo = "Reunião de moradores para discutir segurança",
        conteudo = "Conteúdo da notícia (não visível neste card)",
        dataPostagem = "2024-05-29T10:30:00.000Z",
        categorias = listOf(Categoria(id = 2, nome = "Segurança", descricao = "Segurança pública", sigla = "SEG")),
        urlsMidia = emptyList(),
        endereco = null
    )

    Column {
        NoticiaCard(noticia = sampleNoticiaComTudo) { /* no-op */ }
        Spacer(modifier = Modifier.height(16.dp))
        NoticiaCard(noticia = sampleNoticiaSemMidiaSemEndereco) { /* no-op */ }
    }
}