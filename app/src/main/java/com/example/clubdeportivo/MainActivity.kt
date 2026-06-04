package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsuario = findViewById<EditText>(R.id.txtUsuario)
        val etPass = findViewById<EditText>(R.id.txtContrasena)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPass.text.toString()

            // Validación simple para el emulador
            if (usuario == "admin" && password == "1234") {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Cerramos el login para que no pueda volver atrás con el botón del celu
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}