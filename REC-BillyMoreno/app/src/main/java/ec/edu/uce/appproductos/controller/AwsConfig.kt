package ec.edu.uce.appproductos.controller

import android.content.Context
// 1. ELIMINAMOS EL IMPORT QUE DABA ERROR (AWSStaticCredentialsProvider)
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper

object AwsConfig {

    // --- TUS CREDENCIALES DEL LAB ---
    private const val ACCESS_KEY = "" // Pega tu Access Key
    private const val SECRET_KEY = "" // Pega tu Secret Key
    private const val SESSION_TOKEN = ""

    fun getDynamoDBMapper(context: Context): DynamoDBMapper {
        // 1. Creamos las credenciales
        val credentials = BasicSessionCredentials(ACCESS_KEY, SECRET_KEY, SESSION_TOKEN)

        // 2. TRUCO MAESTRO: Creamos nuestro propio proveedor al vuelo
        // Así no dependemos de si existe o no la clase AWSStaticCredentialsProvider
        val provider = object : AWSCredentialsProvider {
            override fun getCredentials(): AWSCredentials {
                return credentials
            }

            override fun refresh() {
                // No es necesario refrescar en credenciales de sesión estáticas
            }
        }

        // 3. Cliente de DynamoDB
        val client = AmazonDynamoDBClient(provider)
        client.setRegion(Region.getRegion(Regions.US_EAST_1))

        return DynamoDBMapper(client)
    }
}