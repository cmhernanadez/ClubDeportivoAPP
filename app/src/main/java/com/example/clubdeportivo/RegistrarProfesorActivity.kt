package com.example.clubdeportivo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RegistrarProfesorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_profesor)

        val dbHelper = SQLiteHelper(this)

        // ================================================================
        // BLOQUE 1: REFERENCIAS A COMPONENTES
        // ================================================================
        val etNombre = findViewById<EditText>(R.id.etNombreProf)
        val etApellido = findViewById<EditText>(R.id.etApellidoProf)
        val etDni = findViewById<EditText>(R.id.etDniProf)
        val spinnerEsp = findViewById<Spinner>(R.id.spinnerEspecialidadProf)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarProf)

        // ================================================================
        // BLOQUE 2: CARGA DE ACTIVIDADES Y VALIDACIÓN FLOTANTE
        // ================================================================
        val cursor = dbHelper.obtenerActividades()
        val listaActividades = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            do {
                listaActividades.add(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_ACT_NOMBRE)))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Verificamos si la lista está vacía para lanzar la alerta
        if (listaActividades.isEmpty()) {
            Toast.makeText(this, "Debe crear al menos 1 actividad", Toast.LENGTH_LONG).show()
            // Bloqueamos el botón para evitar que se guarde un registro inválido
            btnGuardar.isEnabled = false
        } else {
            // Si hay actividades, llenamos el Spinner normalmente
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaActividades)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEsp.adapter = adapter
        }

        // ================================================================
        // BLOQUE 3: LÓGICA DE REGISTRO CON ALERTAS CENTRALES
        // ================================================================
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()

            // Capturamos el dato del Spinner (si está vacío, mandamos un string en blanco)
            val esp = spinnerEsp.selectedItem?.toString() ?: ""

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty() && esp.isNotEmpty()) {
                val resultado = dbHelper.insertarProfesor(nombre, apellido, dni, esp)

                if (resultado != -1L) {
                    AlertDialog.Builder(this)
                        .setTitle("Registro Exitoso")
                        .setMessage("El profesor $nombre $apellido fue registrado correctamente.")
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
                    .setMessage("Por favor, complete todos los campos obligatorios.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}