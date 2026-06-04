package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerFacturasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_facturas)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorFacturas)

        val cursor: Cursor = dbHelper.obtenerPagos()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PAGO_ID))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PAGO_DNI))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_MONTO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_FECHA))

                // ¡Usamos tu diseño genérico impecable!
                val vistaPago = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaPago.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaPago.findViewById<TextView>(R.id.tvDetalles)

                // En el título (negrita) ponemos el número de factura
                tvNombre.text = "Comprobante de Pago N°: $id"

                // En los detalles (texto gris) ponemos el resto de la info junta
                tvDetalles.text = "DNI del Socio: $dni\nFecha: $fecha\nMonto Abonado: $$monto"

                contenedor.addView(vistaPago)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay facturas para mostrar", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}