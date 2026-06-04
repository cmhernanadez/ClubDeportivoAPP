package com.example.clubdeportivo

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListadoMorososActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_morosos)

        val dbHelper = SQLiteHelper(this)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorListadoMorosos)
        val inflater = LayoutInflater.from(this)

        // Preparamos el formato de la fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Calculamos la fecha de HOY (limpiamos las horas para ser justos en el cálculo de días)
        val hoy = Calendar.getInstance()
        hoy.set(Calendar.HOUR_OF_DAY, 0)
        hoy.set(Calendar.MINUTE, 0)
        hoy.set(Calendar.SECOND, 0)
        hoy.set(Calendar.MILLISECOND, 0)

        // Buscamos los últimos pagos de todos
        val cursor: Cursor = dbHelper.obtenerUltimosPagosClientes()
        var cantidadMorosos = 0

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))

                // --- NUEVO: Capturamos el DNI de la base de datos ---
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))

                val esSocio = cursor.getInt(cursor.getColumnIndexOrThrow("es_socio"))
                val ultimoPagoStr = cursor.getString(cursor.getColumnIndexOrThrow("ultimo_pago"))

                var estaVencido = false
                var textoVencimiento = ""

                // 1. Verificamos si nunca pagó
                if (ultimoPagoStr == null) {
                    estaVencido = true
                    textoVencimiento = "Debe pagar inscripción/cuota."
                } else {
                    // 2. Si pagó, calculamos cuándo vence
                    try {
                        val fechaPago = sdf.parse(ultimoPagoStr)
                        if (fechaPago != null) {
                            val calVencimiento = Calendar.getInstance()
                            calVencimiento.time = fechaPago

                            // LA CONDICIÓN CLAVE
                            if (esSocio == 1) {
                                // Es SOCIO: La cuota es mensual
                                calVencimiento.add(Calendar.MONTH, 1)
                            } else {
                                // NO ES SOCIO: La cuota de actividad es diaria
                                calVencimiento.add(Calendar.DAY_OF_YEAR, 1)
                            }

                            // Comparamos: Si la fecha de vencimiento NO es posterior a hoy...
                            if (!calVencimiento.time.after(hoy.time)) {
                                estaVencido = true
                                textoVencimiento = "Venció el: ${sdf.format(calVencimiento.time)}"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 3. Si la cuota está vencida, inflamos la tarjeta y lo mostramos
                if (estaVencido) {
                    cantidadMorosos++
                    val vistaTarjeta = inflater.inflate(R.layout.item_registro_general, contenedor, false)

                    val tvNombre = vistaTarjeta.findViewById<TextView>(R.id.tvNombre)
                    val tvDetalles = vistaTarjeta.findViewById<TextView>(R.id.tvDetalles)

                    val etiquetaTipo = if (esSocio == 1) "Socio" else "No Socio"

                    // --- ACÁ METEMOS EL DNI EN LA TARJETITA ---
                    tvNombre.text = "$apellido, $nombre"
                    tvDetalles.text = "DNI: $dni | $etiquetaTipo | $textoVencimiento"
                    // ------------------------------------------

                    contenedor.addView(vistaTarjeta)
                }

            } while (cursor.moveToNext())
        }
        cursor.close()

        // Si nadie debe plata, le avisamos al usuario
        if (cantidadMorosos == 0) {
            Toast.makeText(this, "Todos los clientes están al día.", Toast.LENGTH_LONG).show()
        }
    }
}