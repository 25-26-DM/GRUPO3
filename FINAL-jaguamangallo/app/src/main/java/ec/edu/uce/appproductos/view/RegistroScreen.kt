package ec.edu.uce.appproductos.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.uce.appproductos.controller.ProductoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: ProductoViewModel
) {
    // ESTADOS DEL FORMULARIO
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ESTADOS DE ERROR (VALIDACIÓN)
    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // COLORES DE LA MARCA
    val colorNaranjaTech = Color(0xFFFF9800)
    val colorRojoTech = Color(0xFFFF0000)
    val gradiente = Brush.horizontalGradient(listOf(colorNaranjaTech, colorRojoTech))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 1. TÍTULO
            Text(
                text = "Crear Cuenta",
                style = TextStyle(
                    brush = gradiente,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Únete a TechDrop hoy mismo",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // 2. CAMPO USUARIO (CON VALIDACIÓN)
            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    // Validación en tiempo real (requiere que agregues esUsuarioValido en ViewModel)
                    // Si aún no agregaste la función en el ViewModel, borra esta línea:
                    try { errorUsuario = viewModel.esUsuarioValido(it) } catch (e: Exception) { /* Ignorar si no existe */ }
                },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),

                // Feedback visual de error
                isError = errorUsuario != null,
                supportingText = {
                    if (errorUsuario != null) {
                        Text(text = errorUsuario!!, color = MaterialTheme.colorScheme.error)
                    }
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (errorUsuario != null) MaterialTheme.colorScheme.error else colorNaranjaTech
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorNaranjaTech,
                    focusedLabelColor = colorNaranjaTech,
                    cursorColor = colorNaranjaTech,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red
                )
            )

            Spacer(modifier = Modifier.height(8.dp)) // Menos espacio porque el supportingText ocupa lugar

            // 3. CAMPO CONTRASEÑA (CON VALIDACIÓN)
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    // Validación en tiempo real
                    errorPassword = viewModel.esPasswordSegura(it)
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                // Feedback visual de error
                isError = errorPassword != null,
                supportingText = {
                    if (errorPassword != null) {
                        Text(text = errorPassword!!, color = MaterialTheme.colorScheme.error)
                    }
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (errorPassword != null) MaterialTheme.colorScheme.error else colorNaranjaTech
                    )
                },

                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Alternar vista")
                    }
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorNaranjaTech,
                    focusedLabelColor = colorNaranjaTech,
                    cursorColor = colorNaranjaTech,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. BOTÓN DE REGISTRO INTELIGENTE
            Button(
                onClick = {
                    // Validamos todo una última vez antes de enviar
                    // (Try-catch por si no has pegado la función esUsuarioValido en el VM todavía)
                    val finalErrorUser = try { viewModel.esUsuarioValido(usuario) } catch (e: Exception) { null }
                    val finalErrorPass = viewModel.esPasswordSegura(password)

                    if (finalErrorUser != null) {
                        errorUsuario = finalErrorUser
                        Toast.makeText(context, finalErrorUser, Toast.LENGTH_SHORT).show()
                    } else if (finalErrorPass != null) {
                        errorPassword = finalErrorPass
                        Toast.makeText(context, finalErrorPass, Toast.LENGTH_SHORT).show()
                    } else if (usuario.isBlank()) {
                        Toast.makeText(context, "El usuario es obligatorio", Toast.LENGTH_SHORT).show()
                    } else {
                        // TODO CORRECTO -> INTENTAR REGISTRO
                        scope.launch {
                            val exito = viewModel.registrarUsuario(usuario.trim(), password.trim())
                            if (exito) {
                                Toast.makeText(context, "¡Bienvenido a la familia TechDrop!", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "El usuario ya existe, prueba otro.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradiente, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "REGISTRARME",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}