package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Enlazamos cada botón con su Activity correspondiente
        findViewById<Button>(R.id.btnActividades).setOnClickListener {
            startActivity(Intent(this, ActividadesActivity::class.java))
        }

        findViewById<Button>(R.id.btnSocios).setOnClickListener {
            startActivity(Intent(this, SociosActivity::class.java))
        }

        findViewById<Button>(R.id.btnNoSocios).setOnClickListener {
            startActivity(Intent(this, NoSociosActivity::class.java))
        }

        findViewById<Button>(R.id.btnPagos).setOnClickListener {
            startActivity(Intent(this, PagosActivity::class.java))
        }

        findViewById<Button>(R.id.btnInscripciones).setOnClickListener {
            startActivity(Intent(this, InscripcionesActivity::class.java))
        }

        findViewById<Button>(R.id.btnNutricion).setOnClickListener {
            startActivity(Intent(this, NutricionActivity::class.java))
        }

        findViewById<Button>(R.id.btnProfesores).setOnClickListener {
            startActivity(Intent(this, ProfesoresActivity::class.java))
        }

        findViewById<Button>(R.id.btnListados).setOnClickListener {
            startActivity(Intent(this, ListadosActivity::class.java))
        }
    }
}