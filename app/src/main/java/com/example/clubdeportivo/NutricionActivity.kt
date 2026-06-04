package com.example.clubdeportivo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NutricionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutricion)

        val dbHelper = SQLiteHelper(this)

        // ================================================================
        // BLOQUE 1: REFERENCIAS A LA INTERFAZ
        // Enlazamos las variables con los IDs de nuestro diseño XML
        // ================================================================
        val etDni = findViewById<EditText>(R.id.etDniNutricion)
        val etFecha = findViewById<EditText>(R.id.etFechaNutricion)
        val etHora = findViewById<EditText>(R.id.etHoraNutricion)
        val btnAgendar = findViewById<Button>(R.id.btnAgendarNutricion)

        // ================================================================
        // BLOQUE 2: CONFIGURACIÓN DEL CALENDARIO AUTOMÁTICO
        // Función que bloquea la escritura manual y despliega el
        // calendario nativo de Android al hacer clic en el campo.
        // ================================================================
        fun configurarSelectorFecha(editText: EditText) {
            editText.isFocusable = false
            editText.isClickable = true

            editText.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val diaFormateado = String.format("%02d", dayOfMonth)
                    val mesFormateado = String.format("%02d", month + 1)
                    editText.setText("$diaFormateado/$mesFormateado/$year")
                }, anio, mes, dia)

                datePicker.show()
            }
        }

        // ================================================================
        // BLOQUE 3: CONFIGURACIÓN DEL RELOJ AUTOMÁTICO
        // Función que despliega el selector de horas (formato 24hs).
        // ================================================================
        fun configurarSelectorHora(editText: EditText) {
            editText.isFocusable = false
            editText.isClickable = true

            editText.setOnClickListener {
                val calendario = Calendar.getInstance()
                val horaActual = calendario.get(Calendar.HOUR_OF_DAY)
                val minutoActual = calendario.get(Calendar.MINUTE)

                val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                    val horaFormateada = String.format("%02d", hourOfDay)
                    val minutoFormateado = String.format("%02d", minute)
                    editText.setText("$horaFormateada:$minutoFormateado")
                }, horaActual, minutoActual, true)

                timePicker.show()
            }
        }

        // Aplicamos las funciones a los campos
        configurarSelectorFecha(etFecha)
        configurarSelectorHora(etHora)

        // ================================================================
        // BLOQUE 4: LÓGICA DEL BOTÓN AGENDAR Y VALIDACIONES DE TIEMPO
        // ================================================================
        btnAgendar.setOnClickListener {
            val dni = etDni.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val hora = etHora.text.toString().trim()

            // 4.A - Validamos que no haya campos vacíos
            if (dni.isNotEmpty() && fecha.isNotEmpty() && hora.isNotEmpty()) {

                // 4.B - VALIDACIÓN DE TIEMPO: LA MÁQUINA DEL TIEMPO
                try {
                    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val fechaHoraIngresadaStr = "$fecha $hora"
                    val fechaHoraIngresada = formato.parse(fechaHoraIngresadaStr)
                    val fechaHoraActual = Date()

                    if (fechaHoraIngresada != null && fechaHoraIngresada.before(fechaHoraActual)) {
                        // AQUÍ ESTÁ TU NUEVO MENSAJE ESTILO VOLVER AL FUTURO
                        AlertDialog.Builder(this@NutricionActivity)
                            .setTitle("Viaje en el tiempo detectado ⚡")
                            .setMessage("Como no tenés el DeLorean del Doc Brown, vas a tener que ingresar una fecha y hora actual o futura para agendar el turno.")
                            .setPositiveButton("Entendido", null)
                            .show()
                        return@setOnClickListener
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 4.C - Consultamos en la BD si el DNI es de un SOCIO activo (es_socio = 1)
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ? AND ${SQLiteHelper.COL_ES_SOCIO} = 1", arrayOf(dni))

                // 4.D - Si el cursor encontró al socio, guardamos
                if (cursor.moveToFirst()) {
                    val resultado = dbHelper.insertarTurnoNutricion(dni, fecha, hora)

                    if (resultado != -1L) {
                        AlertDialog.Builder(this@NutricionActivity)
                            .setTitle("Turno Confirmado")
                            .setMessage("El turno de nutrición para el socio con DNI $dni fue agendado correctamente para el $fecha a las $hora hs.")
                            .setPositiveButton("Aceptar") { _, _ ->
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        AlertDialog.Builder(this@NutricionActivity)
                            .setTitle("Error de Sistema")
                            .setMessage("Hubo un problema al agendar el turno en la base de datos.")
                            .setPositiveButton("Entendido", null)
                            .show()
                    }
                } else {
                    // 4.E - ERROR: Si no lo encuentra o es un "No Socio"
                    AlertDialog.Builder(this@NutricionActivity)
                        .setTitle("Error de Validación")
                        .setMessage("El DNI ingresado no corresponde a un Socio activo.\n\nRecuerde que el servicio de nutrición es un beneficio exclusivo para Socios del club.")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
                cursor.close()
            } else {
                AlertDialog.Builder(this@NutricionActivity)
                    .setTitle("Atención")
                    .setMessage("Por favor, complete todos los campos (DNI, Fecha y Hora) para poder agendar el turno.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}