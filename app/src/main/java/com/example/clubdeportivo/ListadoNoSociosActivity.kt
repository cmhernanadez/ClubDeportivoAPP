package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoNoSociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_no_socios)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoNoSocios)
        val inflater = LayoutInflater.from(this)

        val cursor: Cursor = dbHelper.obtenerPersonas(0)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_NOMBRE))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_APELLIDO))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_DNI))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_TELEFONO))

                // --- AQUÍ ESTÁ EL CAMBIO DE DISEÑO ---
                // Inflamos nuestra tarjeta personalizada
                val vistaTarjeta = inflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaTarjeta.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaTarjeta.findViewById<TextView>(R.id.tvDetalles)

                tvNombre.text = "$apellido, $nombre"
                tvDetalles.text = "DNI: $dni | Tel: $telefono"

                contenedor.addView(vistaTarjeta)
                // --------------------------------------

            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay No Socios registrados", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}