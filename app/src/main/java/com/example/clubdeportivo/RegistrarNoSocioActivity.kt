package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RegistrarNoSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_no_socio)

        val dbHelper = SQLiteHelper(this)

        // --- BLOQUE 1: REFERENCIAS (Enganchadas a los IDs nuevos) ---
        val etNombre = findViewById<EditText>(R.id.etNombreNoSocio)
        val etApellido = findViewById<EditText>(R.id.etApellidoNoSocio)
        val etDni = findViewById<EditText>(R.id.etDniNoSocio)
        val etTelefono = findViewById<EditText>(R.id.etTelefonoNoSocio)
        val cbFicha = findViewById<CheckBox>(R.id.cbFichaMedicaNoSocio)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarNoSocio)

        // --- BLOQUE 2: LÓGICA DE GUARDADO ---
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            // Capturamos el estado de la ficha
            val fichaMedica = if (cbFicha.isChecked) 1 else 0

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty()) {
                // Mandamos el 0 indicando que es No Socio
                val resultado = dbHelper.insertarPersona(nombre, apellido, dni, telefono, 0, fichaMedica)

                if (resultado != -1L) {
                    AlertDialog.Builder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("El cliente (No Socio) $nombre $apellido fue registrado correctamente en el sistema.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error de Registro")
                        .setMessage("El DNI ingresado ya se encuentra registrado. Verifique los datos.")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("Por favor, complete los campos obligatorios: Nombre, Apellido y DNI.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}