package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ListadosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        // Botones existentes
        findViewById<Button>(R.id.btnListadoSocios).setOnClickListener {
            startActivity(Intent(this, ListadoSociosActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoNoSocios).setOnClickListener {
            startActivity(Intent(this, ListadoNoSociosActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoActividades).setOnClickListener {
            startActivity(Intent(this, ListadoActividadesActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoPagos).setOnClickListener {
            startActivity(Intent(this, ListadoPagosActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoInscripciones).setOnClickListener {
            startActivity(Intent(this, ListadoInscripcionesActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoProfesores).setOnClickListener {
            startActivity(Intent(this, ListadoProfesoresActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoSuplencias).setOnClickListener {
            startActivity(Intent(this, ListadoSuplenciasActivity::class.java))
        }

        // --- NUEVO: Botón Listado de Morosos ---
        findViewById<Button>(R.id.btnListadoMorosos).setOnClickListener {
            startActivity(Intent(this, ListadoMorososActivity::class.java))
        }
    }
}