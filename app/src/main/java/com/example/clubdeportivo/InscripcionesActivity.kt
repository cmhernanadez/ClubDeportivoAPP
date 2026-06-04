package com.example.clubdeportivo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class InscripcionesActivity : AppCompatActivity() {

    /**
     * BLOQUE: Validación de fechas
     * Compara la fecha ingresada por el usuario con la fecha actual del sistema.
     * Retorna true si la fecha ingresada es anterior al día de hoy (tiempo pasado).
     */
    private fun esFechaPasada(fechaStr: String): Boolean {
        try {
            val partes = fechaStr.split("/")
            if (partes.size == 3) {
                val dia = partes[0].toInt()
                val mes = partes[1].toInt() - 1 // Los meses arrancan en 0
                val anio = partes[2].toInt()

                val fechaIngresada = Calendar.getInstance()
                fechaIngresada.set(anio, mes, dia, 0, 0, 0)
                fechaIngresada.set(Calendar.MILLISECOND, 0)

                val hoy = Calendar.getInstance()
                hoy.set(Calendar.HOUR_OF_DAY, 0)
                hoy.set(Calendar.MINUTE, 0)
                hoy.set(Calendar.SECOND, 0)
                hoy.set(Calendar.MILLISECOND, 0)

                return fechaIngresada.before(hoy)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscripciones)

        val dbHelper = SQLiteHelper(this)

        /**
         * BLOQUE: Carga de Spinners
         * Consulta la tabla de actividades y llena los desplegables dinámicamente.
         */
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

        /**
         * BLOQUE: Configuración de Selector de Fechas
         * Configura un DatePickerDialog para que el usuario elija la fecha de inscripción.
         */
        fun configurarSelectorFecha(editText: EditText) {
            editText.isFocusable = false
            editText.setOnClickListener {
                val calendario = Calendar.getInstance()
                val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    editText.setText("${String.format("%02d", dayOfMonth)}/${String.format("%02d", month + 1)}/$year")
                }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }
        }

        /**
         * BLOQUE: Lógica de Inscripción de Socios
         * Valida DNI, tipo de cliente, ficha médica, cupos y fecha antes de registrar.
         */
        findViewById<Button>(R.id.btnSocioAActividad).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_socio_a_actividad, null)
            val spinner = vista.findViewById<Spinner>(R.id.spinnerActividades)
            val etDni = vista.findViewById<EditText>(R.id.etDniSocio)
            val etFec = vista.findViewById<EditText>(R.id.etFechaInscripcion)
            cargarSpinner(spinner)
            configurarSelectorFecha(etFec)

            AlertDialog.Builder(this).setTitle("Inscribir Socio").setView(vista)
                .setPositiveButton("Inscribir") { _, _ ->
                    val dni = etDni.text.toString().trim()
                    val fec = etFec.text.toString().trim()
                    val act = spinner.selectedItem?.toString() ?: ""

                    if (dni.isEmpty() || fec.isEmpty()) {
                        AlertDialog.Builder(this).setTitle("Atención").setMessage("El DNI y la Fecha son obligatorios.").setPositiveButton("Entendido", null).show()
                        return@setPositiveButton
                    }

                    if (esFechaPasada(fec)) {
                        AlertDialog.Builder(this)
                            .setTitle("Viaje en el tiempo detectado ⚡")
                            .setMessage("Como no tenés el DeLorean del Doc Brown, vas a tener que ingresar una fecha y hora actual o futura para agendar el turno.")
                            .setPositiveButton("Entendido", null).show()
                        return@setPositiveButton
                    }

                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery("SELECT ${SQLiteHelper.COL_ES_SOCIO} FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ?", arrayOf(dni))
                    if (cursor.moveToFirst() && cursor.getInt(0) == 1) {
                        cursor.close()
                        if (!dbHelper.tieneFichaMedica(dni)) {
                            AlertDialog.Builder(this).setTitle("Apto Médico Requerido").setMessage("Falta Ficha Médica.").setPositiveButton("Entendido", null).show()
                        } else {
                            // --- VALIDACIÓN DE CUPOS PARA SOCIOS ---
                            val cupoTotal = db.rawQuery("SELECT ${SQLiteHelper.COL_ACT_CUPO} FROM ${SQLiteHelper.TABLA_ACTIVIDADES} WHERE ${SQLiteHelper.COL_ACT_NOMBRE} = ?", arrayOf(act)).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
                            val inscritos = dbHelper.contarInscripcionesPorActividad(act)

                            if (inscritos < cupoTotal) {
                                dbHelper.insertarInscripcion(dni, act, fec)
                                AlertDialog.Builder(this).setTitle("Éxito").setMessage("Socio inscrito en $act.").setPositiveButton("Aceptar", null).show()
                            } else {
                                AlertDialog.Builder(this).setTitle("Cupo Agotado").setMessage("Lo sentimos, no hay más vacantes disponibles para la actividad: $act.").setPositiveButton("Entendido", null).show()
                            }
                        }
                    } else {
                        cursor.close()
                        AlertDialog.Builder(this).setTitle("Error").setMessage("Socio no encontrado o no corresponde.").setPositiveButton("Entendido", null).show()
                    }
                }.setNegativeButton("Cancelar", null).show()
        }

        /**
         * BLOQUE: Lógica de Inscripción de No Socios
         * Valida datos, gestiona el pago de la actividad y realiza la inscripción.
         */
        findViewById<Button>(R.id.btnNoSocioAActividad).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_no_socio_a_actividad, null)
            val etDni = vista.findViewById<EditText>(R.id.etDniNoSocio)
            val etFec = vista.findViewById<EditText>(R.id.etFechaNoSocio)
            val etMonto = vista.findViewById<EditText>(R.id.etMontoNoSocio)
            val spinner = vista.findViewById<Spinner>(R.id.spinnerActividadesNoSocio)
            cargarSpinner(spinner)
            configurarSelectorFecha(etFec)

            AlertDialog.Builder(this).setTitle("Inscripción No Socio").setView(vista)
                .setPositiveButton("Confirmar") { _, _ ->
                    val fec = etFec.text.toString().trim()
                    val dni = etDni.text.toString().trim()
                    val montoStr = etMonto.text.toString().trim()
                    val monto = montoStr.toDoubleOrNull() ?: 0.0
                    val act = spinner.selectedItem?.toString() ?: ""

                    if (dni.isEmpty() || fec.isEmpty() || montoStr.isEmpty()) {
                        AlertDialog.Builder(this).setTitle("Atención").setMessage("Todos los campos son obligatorios.").setPositiveButton("Entendido", null).show()
                        return@setPositiveButton
                    }

                    if (esFechaPasada(fec)) {
                        AlertDialog.Builder(this)
                            .setTitle("Viaje en el tiempo detectado ⚡")
                            .setMessage("Como no tenés el DeLorean del Doc Brown, vas a tener que ingresar una fecha y hora actual o futura para agendar el turno.")
                            .setPositiveButton("Entendido", null).show()
                        return@setPositiveButton
                    }

                    val db = dbHelper.readableDatabase

                    // --- NUEVO: VALIDACIÓN PARA BLOQUEAR SOCIOS EN ESTE FORMULARIO ---
                    val cursorSocio = db.rawQuery("SELECT ${SQLiteHelper.COL_ES_SOCIO} FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ?", arrayOf(dni))
                    var esSocioRegistrado = false
                    if (cursorSocio.moveToFirst()) {
                        if (cursorSocio.getInt(0) == 1) {
                            esSocioRegistrado = true
                        }
                    }
                    cursorSocio.close()

                    if (esSocioRegistrado) {
                        AlertDialog.Builder(this)
                            .setTitle("Atención")
                            .setMessage("El DNI ingresado pertenece a un Socio activo. Por favor, realice la inscripción utilizando el botón 'Socio a Actividad'.")
                            .setPositiveButton("Entendido", null)
                            .show()
                        return@setPositiveButton
                    }
                    // -----------------------------------------------------------------

                    if (monto > 0) {
                        // --- VALIDACIÓN DE CUPOS PARA NO SOCIOS ---
                        val cupoTotal = db.rawQuery("SELECT ${SQLiteHelper.COL_ACT_CUPO} FROM ${SQLiteHelper.TABLA_ACTIVIDADES} WHERE ${SQLiteHelper.COL_ACT_NOMBRE} = ?", arrayOf(act)).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
                        val inscritos = dbHelper.contarInscripcionesPorActividad(act)

                        if (inscritos < cupoTotal) {
                            dbHelper.insertarInscripcion(dni, act, fec)
                            dbHelper.insertarPago(dni, monto, fec)
                            AlertDialog.Builder(this).setTitle("Éxito").setMessage("No socio registrado en $act.").setPositiveButton("Aceptar", null).show()
                        } else {
                            AlertDialog.Builder(this).setTitle("Cupo Agotado").setMessage("Lo sentimos, no hay más vacantes disponibles para la actividad: $act.").setPositiveButton("Entendido", null).show()
                        }
                    } else {
                        AlertDialog.Builder(this).setTitle("Atención").setMessage("El monto debe ser mayor a 0.").setPositiveButton("Entendido", null).show()
                    }
                }.setNegativeButton("Cancelar", null).show()
        }

        /**
         * BLOQUE: Validación de Cupos y Ficha Médica
         * Consulta rápidamente si un cliente tiene apto médico y si hay lugar en la actividad.
         */
        findViewById<Button>(R.id.btnValidacionCupos).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_validacion_cupos, null)
            val etNombreEmail = vista.findViewById<EditText>(R.id.etNombreEmail)
            val spinnerActividad = vista.findViewById<Spinner>(R.id.spinnerActividadCupo)

            cargarSpinner(spinnerActividad)

            AlertDialog.Builder(this).setTitle("Validación Rápida")
                .setView(vista)
                .setPositiveButton("Validar") { _, _ ->
                    val dni = etNombreEmail.text.toString().trim()
                    val actividad = spinnerActividad.selectedItem?.toString() ?: ""

                    if (dni.isEmpty() || actividad.isEmpty()) {
                        AlertDialog.Builder(this@InscripcionesActivity)
                            .setTitle("Atención")
                            .setMessage("Debe ingresar el DNI y seleccionar una Actividad para consultar.")
                            .setPositiveButton("Entendido", null)
                            .show()
                        return@setPositiveButton
                    }

                    val db = dbHelper.readableDatabase

                    // 1. Consultar estado del cliente
                    var estadoMedico = "No encontrado en el sistema."
                    val cursorCliente = db.rawQuery("SELECT ${SQLiteHelper.COL_FICHA_MEDICA} FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ?", arrayOf(dni))
                    if (cursorCliente.moveToFirst()) {
                        val tieneFicha = cursorCliente.getInt(0)
                        estadoMedico = if (tieneFicha == 1) "✅ Apto Médico Válido" else "❌ Requiere Apto Médico"
                    }
                    cursorCliente.close()

                    // 2. Consultar estado de la actividad
                    var estadoCupo = "Actividad no encontrada."
                    val cursorAct = db.rawQuery("SELECT ${SQLiteHelper.COL_ACT_CUPO} FROM ${SQLiteHelper.TABLA_ACTIVIDADES} WHERE ${SQLiteHelper.COL_ACT_NOMBRE} = ?", arrayOf(actividad))
                    if (cursorAct.moveToFirst()) {
                        val cupoTotal = cursorAct.getInt(0)
                        val inscritos = dbHelper.contarInscripcionesPorActividad(actividad)
                        val disponibles = cupoTotal - inscritos
                        estadoCupo = if (disponibles > 0) "✅ Hay lugar ($disponibles libres de $cupoTotal)" else "❌ Cupo Agotado ($inscritos inscritos)"
                    }
                    cursorAct.close()

                    // 3. Mostrar el reporte final
                    AlertDialog.Builder(this@InscripcionesActivity)
                        .setTitle("Reporte de Validación")
                        .setMessage("DNI Consultado: $dni\nFicha Médica: $estadoMedico\n\nActividad Consultada: $actividad\nDisponibilidad: $estadoCupo")
                        .setPositiveButton("Entendido", null)
                        .show()

                }.setNegativeButton("Cerrar", null).show()
        }
    }
}