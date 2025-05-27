package br.senai.sp.jandira.bairronews.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun TelaHome(navController: NavHostController?) {

    Column(modifier = Modifier.fillMaxSize()) {

        // Topo
        Card(
            modifier = Modifier
                .padding(top = 65.dp)
                .height(62.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        fontSize = 15.sp,
                        text = "Bairro News",
                        color = Color.Red
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Person, contentDescription = "Usuário")
                }
            }
        }

        Spacer(modifier = Modifier.height(45.dp))


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ){}

        Spacer(modifier = Modifier.height(45.dp))


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ){}
    }
}
@Preview(showSystemUi = true)
@Composable
private fun TelaHomePreview() {
    TelaHome(null)
}
