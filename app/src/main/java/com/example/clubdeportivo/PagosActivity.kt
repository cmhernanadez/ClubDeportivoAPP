package com.example.clubdeportivo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class PagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        val dbHelper = SQLiteHelper(this)

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

        findViewById<Button>(R.id.btnRegistrarPago).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_registrar_pago, null)
            val etDni = vista.findViewById<EditText>(R.id.etDni)
            val etMonto = vista.findViewById<EditText>(R.id.etMonto)
            val etFecha = vista.findViewById<EditText>(R.id.etFecha)

            configurarSelectorFecha(etFecha)

            AlertDialog.Builder(this)
                .setTitle("Registrar Pago (Solo Socios)")
                .setView(vista)
                .setPositiveButton("Guardar") { _, _ ->
                    val dni = etDni.text.toString().trim()
                    val montoStr = etMonto.text.toString().trim()
                    val fecha = etFecha.text.toString().trim()

                    if (dni.isNotEmpty() && montoStr.isNotEmpty() && fecha.isNotEmpty()) {

                        val db = dbHelper.readableDatabase
                        val cursor = db.rawQuery("SELECT * FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ? AND ${SQLiteHelper.COL_ES_SOCIO} = 1", arrayOf(dni))

                        if (cursor.moveToFirst()) {
                            val monto = montoStr.toDoubleOrNull() ?: 0.0
                            val resultado = dbHelper.insertarPago(dni, monto, fecha)

                            if (resultado != -1L) {
                                // Alerta de Éxito al cobrar
                                AlertDialog.Builder(this@PagosActivity)
                                    .setTitle("Pago Exitoso")
                                    .setMessage("El pago de $$montoStr para el DNI $dni fue registrado correctamente en el sistema.")
                                    .setPositiveButton("Aceptar", null)
                                    .show()
                            } else {
                                // Alerta de Error de Base de Datos
                                AlertDialog.Builder(this@PagosActivity)
                                    .setTitle("Error de Sistema")
                                    .setMessage("Hubo un problema al guardar el pago en la base de datos.")
                                    .setPositiveButton("Entendido", null)
                                    .show()
                            }
                        } else {
                            // Alerta si el DNI no es de un Socio
                            AlertDialog.Builder(this@PagosActivity)
                                .setTitle("Error de Validación")
                                .setMessage("El DNI ingresado no corresponde a un Socio activo.\n\nSolo los Socios pueden registrar pagos de cuota mensual.")
                                .setPositiveButton("Entendido", null)
                                .show()
                        }
                        cursor.close()
                    } else {
                        // Alerta de campos vacíos
                        AlertDialog.Builder(this@PagosActivity)
                            .setTitle("Atención")
                            .setMessage("Por favor, complete todos los campos (DNI, Monto y Fecha) para registrar el pago.")
                            .setPositiveButton("Entendido", null)
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.btnVerFacturas).setOnClickListener {
            startActivity(Intent(this, VerFacturasActivity::class.java))
        }
    }
}