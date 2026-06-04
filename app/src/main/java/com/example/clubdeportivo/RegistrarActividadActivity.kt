package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RegistrarActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_actividad)

        val dbHelper = SQLiteHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombreAct)
        val etTipo = findViewById<EditText>(R.id.etTipoAct)
        val etDesc = findViewById<EditText>(R.id.etDescAct)
        val etCupo = findViewById<EditText>(R.id.etCupoAct)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarAct)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val tipo = etTipo.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val cupoStr = etCupo.text.toString().trim()

            if (nombre.isNotEmpty() && tipo.isNotEmpty() && cupoStr.isNotEmpty()) {
                val cupo = cupoStr.toIntOrNull() ?: 0
                val resultado = dbHelper.insertarActividad(nombre, tipo, desc, cupo)

                if (resultado != -1L) {
                    // Alerta de Éxito
                    AlertDialog.Builder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("La actividad '$nombre' se registró correctamente.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            finish() // Cerramos la pantalla al aceptar
                        }
                        .setCancelable(false) // Obliga a tocar el botón
                        .show()
                } else {
                    // Alerta de Error en BD
                    AlertDialog.Builder(this)
                        .setTitle("Error de Registro")
                        .setMessage("Hubo un problema al guardar la actividad en la base de datos.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Entendido", null)
                        .show()
                }
            } else {
                // Alerta de Validación (Campos vacíos)
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("Por favor, complete los campos obligatorios: Nombre, Tipo y Cupo.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}