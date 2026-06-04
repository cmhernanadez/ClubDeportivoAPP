package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoActividadesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_actividades)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoActividades)
        val cursor: Cursor = dbHelper.obtenerActividades()

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_ACT_NOMBRE))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_ACT_TIPO))
                val cupo = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_ACT_CUPO))

                // ¡Inflamos tu diseño genérico de tarjeta!
                val vistaActividad = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaActividad.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaActividad.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos a los campos de la tarjeta
                tvNombre.text = nombre
                tvDetalles.text = "Tipo: $tipo\nCupos Totales: $cupo"

                contenedor.addView(vistaActividad)

            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay actividades registradas", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}