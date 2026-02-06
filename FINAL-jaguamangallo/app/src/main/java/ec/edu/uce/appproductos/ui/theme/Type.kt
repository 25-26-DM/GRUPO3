package ec.edu.uce.appproductos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ec.edu.uce.appproductos.R // <--- Asegúrate que importe TU R

// 1. DEFINIMOS LA FAMILIA DE FUENTE
val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_regular, FontWeight.Normal),
    Font(R.font.playfair_bold, FontWeight.Bold)
)

// 2. APLICAMOS LA FUENTE A TODOS LOS ESTILOS DE TEXTO
val Typography = Typography(
    // Estilo para Títulos Grandes (Ej: "TECHDROP" en Login)
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
    // Estilo para Títulos Medianos (Ej: Encabezados)
    titleLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Estilo para Texto Normal (Ej: Inputs, párrafos)
    bodyLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    // Estilo para Botones
    labelLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )

    /* Nota: Material 3 tiene más estilos (bodyMedium, titleMedium, etc.),
       pero por defecto heredarán la fuente si la configuras bien en el tema,
       o puedes definir todos aquí si ves que alguno no cambia. */
)