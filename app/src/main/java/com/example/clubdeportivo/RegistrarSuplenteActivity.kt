package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog // Importación necesaria
import androidx.appcompat.app.AppCompatActivity

class RegistrarSuplenteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_suplente)

        val dbHelper = SQLiteHelper(this)

        // ================================================================
        // BLOQUE 1: REFERENCIAS A COMPONENTES
        // ================================================================
        val etNombre = findViewById<EditText>(R.id.etNombreSup)
        val etApellido = findViewById<EditText>(R.id.etApellidoSup)
        val etDni = findViewById<EditText>(R.id.etDniSup)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSup)

        // ================================================================
        // BLOQUE 2: LÓGICA DE REGISTRO CON ALERTAS CENTRALES
        // ================================================================
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty()) {
                // Registramos como profesor con especialidad "Suplente"
                val resultado = dbHelper.insertarProfesor(nombre, apellido, dni, "Suplente")

                if (resultado != -1L) {
                    AlertDialog.Builder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("El suplente $nombre $apellido fue registrado correctamente.")
                        .setPositiveButton("Aceptar") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("No se pudo registrar: El DNI ya se encuentra en el sistema.")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("Por favor, complete todos los campos.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}