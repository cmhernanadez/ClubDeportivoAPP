package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoPagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_pagos)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoPagos)

        // Traemos todos los pagos registrados
        val cursor: Cursor = dbHelper.obtenerPagos()

        // Abrimos la base de datos en modo lectura para consultar si es socio o cliente
        val db = dbHelper.readableDatabase

        if (cursor.moveToFirst()) {
            do {
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PAGO_DNI))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_MONTO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_FECHA))

                // --- LÓGICA DE NEGOCIO: ¿Es Socio o Cliente? ---
                var tipoPersona = "Cliente" // Por defecto asumimos que es Cliente
                val cursorCliente = db.rawQuery("SELECT ${SQLiteHelper.COL_ES_SOCIO} FROM ${SQLiteHelper.TABLA_CLIENTES} WHERE ${SQLiteHelper.COL_DNI} = ?", arrayOf(dni))

                if (cursorCliente.moveToFirst()) {
                    val esSocio = cursorCliente.getInt(0)
                    if (esSocio == 1) {
                        tipoPersona = "Socio" // Si tiene un 1, le cambiamos la etiqueta a Socio
                    }
                }
                cursorCliente.close()
                // ------------------------------------------------

                // Inflamos tu diseño genérico de tarjeta
                val vistaPago = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaPago.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaPago.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos a los campos de la tarjeta con la palabra correcta (Socio o Cliente)
                tvNombre.text = "$tipoPersona DNI: $dni"
                tvDetalles.text = "Monto: $$monto\nFecha: $fecha"

                contenedor.addView(vistaPago)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay pagos registrados aún", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}