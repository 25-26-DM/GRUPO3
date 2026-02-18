package ec.edu.uce.appproductos.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import java.util.UUID

@Entity(tableName = "logs")
@DynamoDBTable(tableName = "app_logs_rec")
data class Log(

    @PrimaryKey
    @DynamoDBHashKey(attributeName = "logId")
    var logId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "accion")
    @DynamoDBAttribute(attributeName = "accion")
    var accion: String = "", // "INGRESO", "CREACION", "ELIMINACION", "ACTUALIZACION"

    @ColumnInfo(name = "usuario")
    @DynamoDBAttribute(attributeName = "usuario")
    var usuario: String = "",

    @ColumnInfo(name = "fecha")
    @DynamoDBAttribute(attributeName = "fecha")
    var fecha: String = "", // Formato: "yyyy-MM-dd HH:mm:ss"

    @ColumnInfo(name = "detalles")
    @DynamoDBAttribute(attributeName = "detalles")
    var detalles: String = "", // JSON o descripción adicional

    @ColumnInfo(name = "recurso")
    @DynamoDBAttribute(attributeName = "recurso")
    var recurso: String = "", // "PRODUCTO", "USUARIO", etc.

    @ColumnInfo(name = "idRecurso")
    @DynamoDBAttribute(attributeName = "idRecurso")
    var idRecurso: String = "", // ID del producto/usuario afectado

    @ColumnInfo(name = "sincronizado")
    @DynamoDBAttribute(attributeName = "sincronizado")
    var sincronizado: Boolean = false // Para controlar sincronización con AWS
)
