package ec.edu.uce.appproductos.model

data class Producto(
    val id: String, // Identificador único (ej: "P001")
    var descripcion: String,
    var fechaFabricacion: String,
    var costo: Double,
    var disponible: Boolean
)
