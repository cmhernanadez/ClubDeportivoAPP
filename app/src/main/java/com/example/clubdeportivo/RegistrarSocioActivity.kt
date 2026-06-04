package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RegistrarSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_socio)

        val dbHelper = SQLiteHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombreSocio)
        val etApellido = findViewById<EditText>(R.id.etApellidoSocio)
        val etDni = findViewById<EditText>(R.id.etDniSocio)
        val etTelefono = findViewById<EditText>(R.id.etTelefonoSocio)
        val cbFicha = findViewById<CheckBox>(R.id.cbFichaMedica)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarSocio)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            val fichaMedica = if (cbFicha.isChecked) 1 else 0

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty()) {
                val resultado = dbHelper.insertarPersona(nombre, apellido, dni, telefono, 1, fichaMedica)

                if (resultado != -1L) {
                    AlertDialog.Builder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("El socio $nombre $apellido fue registrado correctamente en el sistema.")
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