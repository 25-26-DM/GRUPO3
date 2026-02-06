package ec.edu.uce.appproductos.view

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.R
import ec.edu.uce.appproductos.controller.Rutas
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // ESTADOS PARA LA ANIMACIÓN
    // Escala inicial del logo (empieza pequeño: 0f)
    val scale = remember { Animatable(0f) }

    // Colores de tu marca
    val colorNaranjaTech = Color(0xFFFF9800)
    val colorRojoTech = Color(0xFFFF0000)

    // EFECTO LANZADO: Se ejecuta al abrir la pantalla
    LaunchedEffect(key1 = true) {
        // 1. Iniciar animación de escala
        scale.animateTo(
            targetValue = 1f, // Escala final (tamaño normal)
            animationSpec = tween(
                durationMillis = 800, // Dura casi 1 segundo
                easing = {
                    // Efecto de "rebote" al final (Overshoot)
                    OvershootInterpolator(1.5f).getInterpolation(it)
                }
            )
        )

        // 2. Esperar un momento con el logo en pantalla (tiempo total de splash)
        delay(2000L) // Espera 2 segundos adicionales

        // 3. Navegar al Login y eliminar la Splash del historial
        navController.navigate(Rutas.LOGIN) {
            popUpTo(Rutas.SPLASH) { inclusive = true }
        }
    }

    // DISEÑO DE LA PANTALLA
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // O un fondo oscuro si prefieres estilo Netflix puro
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // LOGO ANIMADO
            Image(
                painter = painterResource(id = R.drawable.ic_logo_techdrop),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale.value) // <-- Aquí aplicamos la escala animada
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TEXTO CON GRADIENTE (Aparece junto con el logo)
            Text(
                text = "TECHDROP",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(colorNaranjaTech, colorRojoTech)),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                ),
                modifier = Modifier.scale(scale.value) // También animamos el texto
            )
        }
    }
}