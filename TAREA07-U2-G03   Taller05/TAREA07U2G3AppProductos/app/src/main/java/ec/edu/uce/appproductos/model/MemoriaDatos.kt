package ec.edu.uce.appproductos.model

import androidx.compose.runtime.mutableStateListOf

object MemoriaDatos {

    val listaUsuarios = mutableListOf<Usuario>()
    val listaProductos = mutableStateListOf<Producto>()

    init {
        listaUsuarios.add(Usuario("Alexis","Carvajal"))
        listaUsuarios.add(Usuario("Joel","Guamangallo"))
        listaUsuarios.add(Usuario("Damian","Minda"))
        listaUsuarios.add(Usuario("Billy","Moreno"))
        listaUsuarios.add(Usuario("David","Ortega"))
        listaUsuarios.add(Usuario("Jostyn","Palacios"))

        listaProductos.add(Producto("001", "Laptop Gamer", "12/12/2024", 1500.00, true))
        listaProductos.add(Producto("002", "Mouse Inalámbrico", "01/10/2024", 25.50, true))
        listaProductos.add(Producto("003", "Teclado Mecánico", "15/11/2024", 80.00, false))
    }
}