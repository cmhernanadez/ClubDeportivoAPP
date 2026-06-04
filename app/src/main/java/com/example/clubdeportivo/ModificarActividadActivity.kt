package com.example.clubdeportivo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ModificarActividadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_actividad)

        val dbHelper = SQLiteHelper(this)

        // ================================================================
        // BLOQUE 1: REFERENCIAS A LOS COMPONENTES
        // ================================================================
        // Enlazamos el nuevo Spinner en lugar del viejo EditText
        val spinnerBuscar = findViewById<Spinner>(R.id.spinnerBuscarActividad)
        val etNuevoNombre = findViewById<EditText>(R.id.etNuevoNombreAct)
        val etNuevoTipo = findViewById<EditText>(R.id.etNuevoTipoAct)
        val etNuevaDesc = findViewById<EditText>(R.id.etNuevaDescAct)
        val etNuevoCupo = findViewById<EditText>(R.id.etNuevoCupoAct)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarCambiosAct)

        // ================================================================
        // BLOQUE 2: CARGAR LAS ACTIVIDADES EN EL MENÚ DESPLEGABLE
        // ================================================================
        fun cargarSpinner(spinner: Spinner) {
            val cursor = dbHelper.obtenerActividades()
            val lista = mutableListOf<String>()
            if (cursor.moveToFirst()) {
                do {
                    lista.add(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_ACT_NOMBRE)))
                } while (cursor.moveToNext())
            }
            cursor.close()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lista)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Llamamos a la función para llenar la lista al abrir la pantalla
        cargarSpinner(spinnerBuscar)

        // ================================================================
        // BLOQUE 3: LÓGICA AL DETECTAR EL CLIC EN "GUARDAR CAMBIOS"
        // ================================================================
        btnGuardar.setOnClickListener {
            // Capturamos el nombre de la actividad desde la lista desplegable
            val nombreAnterior = spinnerBuscar.selectedItem?.toString() ?: ""

            val nuevoNombre = etNuevoNombre.text.toString().trim()
            val nuevoTipo = etNuevoTipo.text.toString().trim()
            val nuevaDescripcion = etNuevaDesc.text.toString().trim()
            val nuevoCupoStr = etNuevoCupo.text.toString().trim()

            // 3.A - Verificamos que haya actividades creadas en el sistema
            if (nombreAnterior.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("No hay actividades registradas en el sistema para modificar.")
                    .setPositiveButton("Entendido", null)
                    .show()
                return@setOnClickListener
            }

            // 3.B - Validación de campos obligatorios
            if (nuevoNombre.isEmpty() || nuevoTipo.isEmpty() || nuevoCupoStr.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("Debe completar por lo menos: Nuevo nombre, Tipo y Cupo.")
                    .setPositiveButton("Entendido", null)
                    .show()
                return@setOnClickListener
            }

            // 3.C - Validación de cupo numérico positivo
            val nuevoCupo = nuevoCupoStr.toIntOrNull()
            if (nuevoCupo == null || nuevoCupo < 0) {
                AlertDialog.Builder(this)
                    .setTitle("Cupo Inválido")
                    .setMessage("Por favor, ingrese un número de cupo válido (0 o mayor).")
                    .setPositiveButton("Entendido", null)
                    .show()
                return@setOnClickListener
            }

            // 3.D - Guardamos en la Base de Datos
            val filasActualizadas = dbHelper.actualizarActividad(
                nombreAnterior,
                nuevoNombre,
                nuevoTipo,
                nuevaDescripcion,
                nuevoCupo
            )

            // ================================================================
            // BLOQUE 4: CONTROL DE RESULTADO Y ALERTAS (Sin advertencias amarillas)
            // ================================================================
            if (filasActualizadas > 0) {
                AlertDialog.Builder(this)
                    .setTitle("Modificación Exitosa")
                    .setMessage("La actividad '$nombreAnterior' fue actualizada correctamente.")
                    .setPositiveButton("Aceptar") { _, _ ->
                        finish() // Cierra la pantalla y vuelve al menú
                    }
                    .setCancelable(false)
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error de Sistema")
                    .setMessage("No se pudo actualizar la actividad. Intente nuevamente.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}