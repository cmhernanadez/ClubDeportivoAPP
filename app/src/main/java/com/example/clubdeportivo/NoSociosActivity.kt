package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NoSociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_socios)

        // Botón para ir a Registrar No Socio
        findViewById<Button>(R.id.btnRegistrarNoSocio).setOnClickListener {
            startActivity(Intent(this, RegistrarNoSocioActivity::class.java))
        }

        // Botón para ir a Modificar No Socio
        findViewById<Button>(R.id.btnModificarNoSocio).setOnClickListener {
            startActivity(Intent(this, ModificarNoSocioActivity::class.java))
        }

        // Botón para ir al Listado de No Socios
        findViewById<Button>(R.id.btnListadoNoSocios).setOnClickListener {
            startActivity(Intent(this, ListadoNoSociosActivity::class.java))
        }
    }
}