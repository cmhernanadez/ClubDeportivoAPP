package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// --- Pantalla Gestión de Socios ---
class SociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Conexión con el XML ---
        setContentView(R.layout.activity_socios)

        // --- Registrar Socio ---
        findViewById<Button>(R.id.btnRegistrarSocio).setOnClickListener {
            startActivity(Intent(this, RegistrarSocioActivity::class.java))
        }

        // --- Modificar Socio ---
        findViewById<Button>(R.id.btnModificarSocio).setOnClickListener {
            startActivity(Intent(this, ModificarSocioActivity::class.java))
        }

        // --- Listado de Socios ---
        findViewById<Button>(R.id.btnListadoSocios).setOnClickListener {
            startActivity(Intent(this, ListadoSociosActivity::class.java))
        }
    }
}
