package br.senai.sp.jandira.bairronews.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import br.senai.sp.jandira.bairronews.model.Comentario // Importe o modelo Comentario
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun ComentarioCard(comentario: Comentario) { // Recebe um objeto Comentario
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp), // Ajuste o padding para ficar mais próximo
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F0F0) // Cor mais clara para o card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Elevação menor
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Não exibindo nome do usuário ou foto conforme solicitado

            // Conteúdo do comentário
            Text(
                text = comentario.conteudo,
                fontSize = 14.sp,
                color = Color(0xFF333333) // Cor mais escura para melhor legibilidade
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Data e hora do comentário
            val dataFormatada = try {
                // Tenta parsear como ISO_OFFSET_DATE_TIME primeiro
                OffsetDateTime.parse(comentario.dataPostagem, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")))
            } catch (e: DateTimeParseException) {
                try {
                    // Se falhar, tenta parsear como ISO_LOCAL_DATE_TIME (sem offset)
                    LocalDateTime.parse(comentario.dataPostagem, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")))
                } catch (e2: DateTimeParseException) {
                    Log.e("ComentarioCard", "Erro ao parsear data do comentário: ${comentario.dataPostagem} - ${e2.message}")
                    "Data e hora indisponíveis"
                }
            }

            Text(
                text = dataFormatada,
                fontSize = 10.sp, // Tamanho menor
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
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
            conteudo = "Este é um comentário de exemplo. Muito bom!",
            tblUsuarioId = 1,
            tblNoticiaId = 1,
            dataPostagem = "2024-06-07T10:30:00.000Z",
            user = null
        )
    )
}