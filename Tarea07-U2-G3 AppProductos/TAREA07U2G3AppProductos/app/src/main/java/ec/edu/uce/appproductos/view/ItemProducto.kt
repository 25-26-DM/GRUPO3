package ec.edu.uce.appproductos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ec.edu.uce.appproductos.model.Producto

@Composable
fun ItemProducto(
    producto: Producto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // COLUMNA 1: Datos del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Código: ${producto.id}")
                Text(text = "Precio: $${producto.costo}")

                // Un detalle visual para la disponibilidad
                val colorDispo = if (producto.disponible) Color.Green else Color.Red
                Text(text = if (producto.disponible) "Disponible" else "Agotado", color = colorDispo)
            }

            // COLUMNA 2: Botones de acción (Editar / Borrar)
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Blue)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}