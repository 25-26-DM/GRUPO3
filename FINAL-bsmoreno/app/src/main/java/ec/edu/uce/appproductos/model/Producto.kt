package ec.edu.uce.appproductos.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@Entity(tableName = "productos")
@DynamoDBTable(tableName = "productos")
data class Producto(

    @PrimaryKey
    @DynamoDBHashKey(attributeName = "codigo")
    var codigo: String = "",

    @DynamoDBAttribute(attributeName = "descripcion")
    var descripcion: String = "",

    @DynamoDBAttribute(attributeName = "fechaFabricacion")
    var fechaFabricacion: String = "",

    @DynamoDBAttribute(attributeName = "costo")
    var costo: Double = 0.0,

    @DynamoDBAttribute(attributeName = "isDisponible")
    var isDisponible: Boolean = false,

    @DynamoDBAttribute(attributeName = "fotoPath")
    var fotoPath: String? = null,

    // --- SINCRONIZACIÓN ---
    // is_synced: ¿Ya se subió a la nube el último cambio (crear/editar)?
    @ColumnInfo(name = "is_synced")
    @DynamoDBIgnore
    var isSynced: Boolean = false,

    // --- NUEVO: ELIMINACIÓN LÓGICA ---
    // is_deleted: ¿El usuario pidió eliminarlo?
    // Si es TRUE, el sincronizador borrará de la nube y luego de local.
    @ColumnInfo(name = "is_deleted")
    @DynamoDBIgnore
    var isDeleted: Boolean = false

) {
    // Constructor vacío OBLIGATORIO para AWS SDK
    // Nota: Ahora pasamos dos 'false' al final (uno para isSynced, otro para isDeleted)
    constructor() : this("", "", "", 0.0, false, null, false, false)
}