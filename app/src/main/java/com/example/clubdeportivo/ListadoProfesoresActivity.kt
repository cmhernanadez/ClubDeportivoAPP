package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoProfesoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_profesores)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoProfesores)

        // Traemos todos los profesores de la BD
        val cursor: Cursor = dbHelper.obtenerProfesores()

        if (cursor.moveToFirst()) {
            do {
                // Usamos las columnas exactas que declaramos en DatabaseHelper
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PROF_NOMBRE))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PROF_APELLIDO))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PROF_DNI))
                val esp = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_PROF_ESP))

                // ¡Inflamos tu diseño genérico de tarjeta!
                val vistaProfesor = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaProfesor.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaProfesor.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos a los campos de la tarjeta
                tvNombre.text = "Prof. $apellido, $nombre"
                tvDetalles.text = "DNI: $dni\nEspecialidad: $esp"

                contenedor.addView(vistaProfesor)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay profesores registrados", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}