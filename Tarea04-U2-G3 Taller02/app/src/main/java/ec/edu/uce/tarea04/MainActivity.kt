package ec.edu.uce.tarea04

import android.content.Intent // <--- IMPORTANTE PARA COMPARTIR
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // <--- IMPORTANTE PARA EL CONTEXTO
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import ec.edu.uce.tarea04.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NeonPulseInvitation()
            }
        }
    }
}

@Composable
fun NeonPulseInvitation() {
    // 1. OBTENER EL CONTEXTO (Necesario para lanzar el Intent de compartir)
    val context = LocalContext.current

    val NeonCyan = Color(0xFF00FFFF)
    val NeonMagenta = Color(0xFFFF00FF)

    // Fuente Audiowide
    val GlobalFont = FontFamily(
        Font(R.font.audiowide, FontWeight.Normal)
    )

    // MENSAJE DE LA INVITACIÓN
    val shareMessage = """
        ⚡ ¡ESTÁS INVITADO A NEON PULSE 2026! ⚡
        
        📅 Fecha: 18 - 19 Julio, 2026
        📍 Lugar: Parque Bicentenario
        🎵 Line Up: Alan Walker, The Infamous, Fast Boy y más...
        
        ¡No te pierdas el festival de electrónica del año! 🎧
        Más info: contacto@neonpulse.com
    """.trimIndent()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // FONDO
        Image(
            painter = painterResource(id = R.drawable.gemini_generated_image_wugyj7wugyj7wugy),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        // CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. IMAGEN PRINCIPAL
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .weight(0.40f)
                    .border(2.dp, NeonCyan, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.principal),
                        contentDescription = "Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. TÍTULOS
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEON PULSE",
                    color = NeonCyan,
                    fontSize = 32.sp,
                    fontFamily = GlobalFont,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Festival de Música Electrónica",
                    color = NeonMagenta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = GlobalFont,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Divider(
                color = Color.DarkGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // 3. INFO + ARTISTAS + BOTÓN (Con Scroll)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.60f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoCard(R.drawable.ic_calendar, "18 - 19 July, 2026", "Fecha", NeonCyan, GlobalFont)
                InfoCard(R.drawable.ic_location, "Parque Bicentenario", "Lugar", NeonCyan, GlobalFont)
                InfoCard(R.drawable.ic_email, "contacto@neonpulse.com", "Correo", NeonCyan, GlobalFont)
                InfoCard(R.drawable.ic_phone, "+593 99 123 4567", "Teléfono", NeonCyan, GlobalFont)

                // TARJETA DESPLEGABLE
                ArtistExpandableCard(
                    iconRes = R.drawable.ic_lineup,
                    borderColor = NeonCyan,
                    customFont = GlobalFont,
                    artists = listOf("Alan Walker", "The Infamous", "Fast Boy", "Anthony Keyrouz", "Ofenbach", "offrami", "Boris Way", "Bandana", "VIZE", "Zedd")                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN COMPARTIR FUNCIONAL ---
                Button(
                    onClick = {
                        // Lógica para compartir
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareMessage) // Ponemos el mensaje
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Invitar amigos vía...")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COMPARTIR INVITACIÓN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = GlobalFont
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- Componente: Tarjeta Desplegable de Artistas ---
@Composable
fun ArtistExpandableCard(iconRes: Int, borderColor: Color, customFont: FontFamily, artists: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Line Up",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Line Up", color = Color.Gray, fontSize = 9.sp, fontFamily = customFont)
                    Text(text = "Artistas Invitados", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = customFont)
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).rotate(rotationState)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp, start = 40.dp)) {
                    Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(bottom = 6.dp))
                    artists.forEach { artist ->
                        Text(
                            text = "• $artist",
                            color = Color(0xFFFF00FF),
                            fontSize = 13.sp,
                            fontFamily = customFont,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Componente: InfoCard Normal ---
@Composable
fun InfoCard(iconRes: Int, text: String, label: String, borderColor: Color, customFont: FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = label, color = Color.Gray, fontSize = 9.sp, fontFamily = customFont)
                Text(text = text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = customFont)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInvitation() {
    NeonPulseInvitation()
}