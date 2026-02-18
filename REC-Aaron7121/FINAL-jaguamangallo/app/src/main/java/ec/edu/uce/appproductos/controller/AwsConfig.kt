package ec.edu.uce.appproductos.controller

import android.content.Context
import android.util.Log
// 1. ELIMINAMOS EL IMPORT QUE DABA ERROR (AWSStaticCredentialsProvider)
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import ec.edu.uce.appproductos.BuildConfig

object AwsConfig {

    // --- TUS CREDENCIALES DEL LAB ---
    private const val ACCESS_KEY = "ASIA5J4IX3LTOPSATSVZ" // Pega tu Access Key
    private const val SECRET_KEY = "tFspT7v4q9/XxXlR0Jq4pWG2MXjnw55e8sBNWsrF" // Pega tu Secret Key
    private const val SESSION_TOKEN = "IQoJb3JpZ2luX2VjEIH//////////wEaCXVzLXdlc3QtMiJGMEQCIDXi0xJf02DbnFnCwdX4fipwV+mC9EHOv/i1OHln5EG7AiAuclhh2MsabUSwMkcjyL5i/4Gt8ljN+fsMD/8D4yOSoiqgAghKEAEaDDkxNDU3NzkzOTE3NCIMGNDpM8yNpR+1lScBKv0BTKWOeIriy50IRxBLZYgYiFhScrxXMotIRxFNq9JIdNFKyRE3RG4xXF/+pX8eWauzfDFbAG38BMkGCYPxyHG+dNpPO6P/fSWeTjuABghgNiAaULzfNEW8/JwzgrPLpxewYS2gxebKXtqzDgMl2r1Ogs4UBEptv8gjhdKT2Tg1ZapoCmIVAnFDtifMv3F5wfKJ8zBeKaFAHjOwpG3e2PBANVTZwI6uz199yQ0x6jD/dFkuOcrJKPWX1RSbalhKNkAWoB+EunI2TTqDEolGtjOpkC5SofWkS15dw6Zaphf7iR2cXloej9SUGhVBkmcSYBGfYriH2ciDjwMQbJ2ukzDevpjMBjqeAciBZE/HjIyRWap7LdjACEN+i5Ja40qivEPlYtKzp8t9hcBnIC8ZntH+C3P8DXzJiLAhDQq3ontzS2jz4Zpt3JZzH2kBvhwNQP2aoZZaeWP0kiGW5svDQ/xwGJxkyA2X9LTWg9yyXcbYR69rkB5J11sv992zR7SyHgduTO5Gv7KmBcUHAtpiOUZn/wVxWrHAuOLwsDWPFCuazHDC5t+5\n"

    // ---

    fun getDynamoDBMapper(context: Context): DynamoDBMapper {
        // 1. Creamos las credenciales
        val credentials = BasicSessionCredentials(ec.edu.uce.appproductos.BuildConfig.AWS_ACCESS_KEY_ID,
            ec.edu.uce.appproductos.BuildConfig.AWS_SECRET_ACCESS_KEY , ec.edu.uce.appproductos.BuildConfig.AWS_SESION_TOKEN)
        Log.d("AWS", "Credenciales: ${BuildConfig.AWS_ACCESS_KEY_ID} ${BuildConfig.AWS_SECRET_ACCESS_KEY} ${BuildConfig.AWS_SESION_TOKEN}")

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