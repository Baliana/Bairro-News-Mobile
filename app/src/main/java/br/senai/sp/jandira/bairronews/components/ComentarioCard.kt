// br.senai.sp.jandira.bairronews.components/ComentarioCard.kt
package br.senai.sp.jandira.bairronews.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.bairronews.model.Comentario
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun ComentarioCard(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)) // Fundo claro para o card de comentário
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val dataFormatada = try {
                // Tenta parsear o formato ISO_LOCAL_DATE_TIME e então formatar
                val parsedDate = LocalDateTime.parse(comentario.dataPostagem, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("pt", "BR")))
            } catch (e: DateTimeParseException) {
                // Se ISO_LOCAL_DATE_TIME falhar, tenta com OffsetDateTime (para o 'Z')
                try {
                    val parsedOffsetDate = java.time.OffsetDateTime.parse(comentario.dataPostagem, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    parsedOffsetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("pt", "BR")))
                } catch (e2: DateTimeParseException) {
                    "Data Indisponível"
                }
            }
            Text(
                text = "Usuário ${comentario.tblUsuarioId ?: "Anônimo"} em $dataFormatada",
                color = Color.DarkGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comentario.conteudo,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComentarioCardPreview() {
    ComentarioCard(
        comentario = Comentario(
            id = 1,
            conteudo = "Este é um comentário de exemplo para o preview.",
            tblUsuarioId = 123,
            dataPostagem = "2024-06-03T16:00:00.000Z" // Exemplo com Z
        )
    )
}