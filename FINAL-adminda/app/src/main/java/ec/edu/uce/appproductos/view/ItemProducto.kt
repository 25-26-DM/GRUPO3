package ec.edu.uce.appproductos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter // <--- Importante
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

            // 1. FOTO PEQUEÑA (Si existe fotoPath)
            if (producto.fotoPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(producto.fotoPath), // <--- Usamos fotoPath
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // 2. DATOS
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Código: ${producto.codigo}")
                Text(text = "Fecha: ${producto.fechaFabricacion}")
                Text(text = "Precio: $${producto.costo}")

                val colorDispo = if (producto.isDisponible) Color.Green else Color.Red
                Text(text = if (producto.isDisponible) "Disponible" else "Agotado", color = colorDispo)
            }

            // 3. BOTONES
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