package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoInscripcionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_inscripciones)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoInscripciones)

        // Buscamos directamente en la tabla inscripciones
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${SQLiteHelper.TABLA_INSCRIPCIONES}", null)

        if (cursor.moveToFirst()) {
            do {
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_INS_DNI))
                val actividad = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_INS_ACT))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_INS_FECHA))

                // ¡Inflamos tu diseño genérico de tarjeta!
                val vistaInscripcion = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaInscripcion.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaInscripcion.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos a los campos de la tarjeta
                tvNombre.text = "Inscripción: $actividad"
                tvDetalles.text = "DNI Inscripto: $dni\nFecha: $fecha"

                contenedor.addView(vistaInscripcion)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay inscripciones registradas", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}