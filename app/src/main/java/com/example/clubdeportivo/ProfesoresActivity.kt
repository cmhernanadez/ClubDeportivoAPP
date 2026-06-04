package com.example.clubdeportivo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class ProfesoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores)

        val dbHelper = SQLiteHelper(this)

        // ================================================================
        // BLOQUE 1: FUNCIONES AUXILIARES (CALENDARIO Y LISTAS)
        // ================================================================
        fun configurarSelectorFecha(editText: EditText) {
            editText.isFocusable = false // Bloquea el teclado manual
            editText.isClickable = true

            editText.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    // Corrección de los warnings amarillos usando Locale y String.format correctamente
                    val fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    editText.setText(fechaFormateada)
                }, anio, mes, dia)

                datePicker.show()
            }
        }

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

        // ================================================================
        // BLOQUE 2: NAVEGACIÓN A OTRAS PANTALLAS
        // ================================================================
        findViewById<Button>(R.id.btnRegistrarProfesor).setOnClickListener {
            startActivity(Intent(this, RegistrarProfesorActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarSuplente).setOnClickListener {
            startActivity(Intent(this, RegistrarSuplenteActivity::class.java))
        }

        // ================================================================
        // BLOQUE 3: LÓGICA DE REGISTRAR ASISTENCIA (AHORA CON SPINNER)
        // ================================================================
        findViewById<Button>(R.id.btnRegistrarAsistencia).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_asistencia_profesor, null)
            val etDniProfesor = vista.findViewById<EditText>(R.id.etDniProfesor)

            // Instanciamos el Spinner que agregaste en el XML
            val spinnerActividadAsistencia = vista.findViewById<Spinner>(R.id.spinnerActividadAsistencia)
            val etFechaAsistencia = vista.findViewById<EditText>(R.id.etFechaAsistencia)

            // Cargamos la lista desplegable de la base de datos
            cargarSpinner(spinnerActividadAsistencia)
            configurarSelectorFecha(etFechaAsistencia)

            AlertDialog.Builder(this)
                .setTitle("Registrar Asistencia del Profesor")
                .setView(vista)
                .setPositiveButton("Registrar") { _, _ ->
                    val dni = etDniProfesor.text.toString().trim()
                    val fecha = etFechaAsistencia.text.toString().trim()

                    // Capturamos el texto de la actividad seleccionada en la lista
                    val actividad = spinnerActividadAsistencia.selectedItem?.toString() ?: ""

                    if (dni.isNotEmpty() && actividad.isNotEmpty() && fecha.isNotEmpty()) {
                        val resultado = dbHelper.insertarAsistencia(dni, actividad, fecha)

                        if (resultado != -1L) {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Asistencia Registrada")
                                .setMessage("La asistencia fue guardada con éxito.")
                                .setPositiveButton("Aceptar", null)
                                .show()
                        } else {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Error de Sistema")
                                .setMessage("Error al guardar la asistencia en la base de datos.")
                                .setPositiveButton("Entendido", null)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(this@ProfesoresActivity)
                            .setTitle("Atención")
                            .setMessage("Por favor complete todos los datos requeridos.")
                            .setPositiveButton("Entendido", null)
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // ================================================================
        // BLOQUE 4: LÓGICA DE ASIGNAR SUPLENTE (AHORA CON VALIDACIÓN DE DNI)
        // ================================================================
        findViewById<Button>(R.id.btnAsignarSuplente).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_asignar_suplente, null)

            val etDniProfesorTitular = vista.findViewById<EditText>(R.id.etDniProfesorTitular)
            val etDniProfesorSuplente = vista.findViewById<EditText>(R.id.etDniProfesorSuplente)
            val spinnerActividad = vista.findViewById<Spinner>(R.id.spinnerActividadSuplente)

            val etFechaInicio = vista.findViewById<EditText>(R.id.etFechaInicioSuplencia)
            val etDiasDuracion = vista.findViewById<EditText>(R.id.etDiasDuracionSuplencia)

            // Llenamos el spinner y le ponemos el calendario a la fecha
            cargarSpinner(spinnerActividad)
            configurarSelectorFecha(etFechaInicio)

            AlertDialog.Builder(this)
                .setTitle("Asignar Suplente")
                .setView(vista)
                .setPositiveButton("Asignar") { _, _ ->
                    val titular = etDniProfesorTitular.text.toString().trim()
                    val suplente = etDniProfesorSuplente.text.toString().trim()
                    val actividad = spinnerActividad.selectedItem?.toString() ?: ""
                    val fechaInicio = etFechaInicio.text.toString().trim()
                    val diasStr = etDiasDuracion.text.toString().trim()

                    // Verificamos que los 5 campos estén llenos
                    if (titular.isNotEmpty() && suplente.isNotEmpty() && actividad.isNotEmpty() && fechaInicio.isNotEmpty() && diasStr.isNotEmpty()) {

                        val dias = diasStr.toIntOrNull() ?: 0

                        if(dias <= 0) {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Atención")
                                .setMessage("La cantidad de días debe ser mayor a cero.")
                                .setPositiveButton("Entendido", null)
                                .show()
                            return@setPositiveButton
                        }

                        val db = dbHelper.readableDatabase

                        // --- NUEVO: VALIDACIÓN DE DNI TITULAR ---
                        val cursorTitular = db.rawQuery("SELECT ${SQLiteHelper.COL_PROF_DNI} FROM ${SQLiteHelper.TABLA_PROFESORES} WHERE ${SQLiteHelper.COL_PROF_DNI} = ?", arrayOf(titular))
                        val existeTitular = cursorTitular.moveToFirst()
                        cursorTitular.close()

                        if (!existeTitular) {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Atención")
                                .setMessage("DNI de titular incorrecto. No se encontró al profesor titular en el sistema.")
                                .setPositiveButton("Entendido", null)
                                .show()
                            return@setPositiveButton
                        }

                        // --- NUEVO: VALIDACIÓN DE DNI SUPLENTE ---
                        val cursorSuplente = db.rawQuery("SELECT ${SQLiteHelper.COL_PROF_DNI} FROM ${SQLiteHelper.TABLA_PROFESORES} WHERE ${SQLiteHelper.COL_PROF_DNI} = ?", arrayOf(suplente))
                        val existeSuplente = cursorSuplente.moveToFirst()
                        cursorSuplente.close()

                        if (!existeSuplente) {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Atención")
                                .setMessage("DNI de suplente incorrecto. Registre un DNI válido de un profesor existente.")
                                .setPositiveButton("Entendido", null)
                                .show()
                            return@setPositiveButton
                        }

                        // Si pasa las dos validaciones, guarda la suplencia
                        val resultado = dbHelper.insertarSuplencia(titular, suplente, actividad, fechaInicio, dias)

                        if (resultado != -1L) {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Suplente Asignado")
                                .setMessage("El profesor con DNI $suplente cubrirá a $titular en '$actividad' a partir del $fechaInicio por $dias día(s).")
                                .setPositiveButton("Aceptar", null)
                                .show()
                        } else {
                            AlertDialog.Builder(this@ProfesoresActivity)
                                .setTitle("Error de Sistema")
                                .setMessage("Hubo un problema al asignar el suplente en la base de datos.")
                                .setPositiveButton("Entendido", null)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(this@ProfesoresActivity)
                            .setTitle("Atención")
                            .setMessage("Por favor, complete todos los campos obligatorios.")
                            .setPositiveButton("Entendido", null)
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}