package com.example.android.whileinuselocation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SensorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_list)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val sensorListTextView = findViewById<TextView>(R.id.sensor_list_text_view)
        val backButton = findViewById<Button>(R.id.back_button)

        val sensorInfo = StringBuilder()
        for (sensor in deviceSensors) {
            sensorInfo.append("Nombre: ${sensor.name}\n")
            sensorInfo.append("Proveedor: ${sensor.vendor}\n")
            sensorInfo.append("Versión: ${sensor.version}\n")
            sensorInfo.append("Tipo: ${sensor.type}\n")
            sensorInfo.append("----------------------------\n")
        }

        sensorListTextView.text = sensorInfo.toString()

        backButton.setOnClickListener {
            finish()
        }
    }
}
