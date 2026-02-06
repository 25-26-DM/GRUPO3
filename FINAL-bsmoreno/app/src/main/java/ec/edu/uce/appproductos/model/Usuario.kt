package ec.edu.uce.appproductos.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@Entity(tableName = "usuarios")
@DynamoDBTable(tableName = "usuarios") // <--- Nombre de la tabla en AWS
data class Usuario(

    // ID numérico para Room (Local)
    @PrimaryKey(autoGenerate = true)
    @DynamoDBIgnore // <--- AWS no necesita este ID autogenerado local
    var id: Int = 0,

    // El nombre de usuario será la CLAVE en AWS (HashKey)
    @ColumnInfo(name = "usuario")
    @DynamoDBHashKey(attributeName = "usuario")
    var usuario: String = "",

    @ColumnInfo(name = "clave")
    @DynamoDBAttribute(attributeName = "clave")
    var clave: String = ""
) {
    // Constructor vacío OBLIGATORIO para DynamoDB
    constructor() : this(0, "", "")
}