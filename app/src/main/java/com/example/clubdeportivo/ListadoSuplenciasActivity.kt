package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoSuplenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_suplencias)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoSuplencias)

        val cursor: Cursor = dbHelper.obtenerSuplencias()

        if (cursor.moveToFirst()) {
            do {
                val titular = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_SUP_DNI_TIT))
                val suplente = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_SUP_DNI_SUP))
                val actividad = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COL_SUP_ACT))

                // ¡Inflamos tu diseño genérico de tarjeta!
                val vistaSuplencia = layoutInflater.inflate(R.layout.item_registro_general, contenedor, false)

                val tvNombre = vistaSuplencia.findViewById<TextView>(R.id.tvNombre)
                val tvDetalles = vistaSuplencia.findViewById<TextView>(R.id.tvDetalles)

                // Asignamos los datos a los campos de la tarjeta
                tvNombre.text = "Actividad: $actividad"
                tvDetalles.text = "Titular DNI: $titular\nSuplente DNI: $suplente"

                contenedor.addView(vistaSuplencia)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No hay suplencias registradas", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}