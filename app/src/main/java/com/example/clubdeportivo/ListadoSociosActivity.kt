package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoSociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios)

        // --- BLOQUE 1: INICIALIZACIÓN ---
        // Preparamos el acceso a la base de datos y la herramienta para inflar tarjetas
        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoSocios)
        val inflater = LayoutInflater.from(this)

        // --- BLOQUE 2: OBTENCIÓN DE DATOS ---
        // Consultamos la tabla de personas filtrando por el valor 1 (que representa a los Socios)
        val cursor: Cursor = dbHelper.obtenerPersonas(1)

        // --- BLOQUE 3: PROCESAMIENTO Y VISUALIZACIÓN ---
        // Verificamos si hay registros para mostrar
        if (cursor.moveToFirst()) {
            do {
                // Extraemos los datos de cada columna del cursor
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_NOMBRE))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_APELLIDO))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_DNI))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_TELEFONO))

                // Inflamos nuestra tarjeta (item_registro_general) que definimos antes
                val vistaTarjeta = inflater.inflate(R.layout.item_registro_general, contenedor, false)

                // Buscamos los TextViews dentro de la tarjeta
                val tvNombre = vistaTarjeta.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaTarjeta.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos extraídos a la tarjeta
                tvNombre.text = "$apellido, $nombre"
                tvDetalles.text = "DNI: $dni | Tel: $telefono"

                // Agregamos la tarjeta completa al contenedor principal
                contenedor.addView(vistaTarjeta)

            } while (cursor.moveToNext())
        } else {
            // Avisamos al usuario si la lista está vacía
            Toast.makeText(this, "No hay socios registrados todavía", Toast.LENGTH_SHORT).show()
        }

        // --- BLOQUE 4: CIERRE ---
        // Siempre cerramos el cursor para liberar memoria
        cursor.close()
    }
}