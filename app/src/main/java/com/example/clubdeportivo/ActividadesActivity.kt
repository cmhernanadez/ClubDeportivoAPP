package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        // Botón para ir a Registrar Actividad
        findViewById<Button>(R.id.btnRegistrarActividad).setOnClickListener {
            startActivity(Intent(this, RegistrarActividadActivity::class.java))
        }

        // Botón para ir a Modificar Actividad
        findViewById<Button>(R.id.btnModificarActividad).setOnClickListener {
            startActivity(Intent(this, ModificarActividadActivity::class.java))
        }

        // Botón para ir al Listado de Actividades
        findViewById<Button>(R.id.btnListadoActividades).setOnClickListener {
            startActivity(Intent(this, ListadoActividadesActivity::class.java))
        }
    }
}