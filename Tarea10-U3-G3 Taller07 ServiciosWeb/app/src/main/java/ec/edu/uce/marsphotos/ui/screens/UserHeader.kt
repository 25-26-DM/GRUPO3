package ec.edu.uce.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserInfoBar(
    userName: String,
    loginTime: String,
    modifier: Modifier = Modifier
) {
    // Usamos una tarjeta (Card) para darle un toquecito elevado y elegante
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), // Margen externo
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // Color de fondo suave
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Espacio interno
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Separa los elementos a los extremos
        ) {
            // LADO IZQUIERDO: Nombre del Usuario
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary // Usa el color Rojo/Naranja del tema
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Usuario",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // LADO DERECHO: Hora de Ingreso
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Ingreso",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = loginTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.DateRange, // Icono de reloj/calendario
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}