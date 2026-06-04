package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ModificarNoSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_no_socio)

        val dbHelper = SQLiteHelper(this)

        val etDni = findViewById<EditText>(R.id.etDniModificar)
        val etTelefono = findViewById<EditText>(R.id.etNuevoTelefono)
        val cbFicha = findViewById<CheckBox>(R.id.cbFichaModificar) // El nuevo CheckBox
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarModificacion)

        btnConfirmar.setOnClickListener {
            val dni = etDni.text.toString().trim()
            val nuevoTel = etTelefono.text.toString().trim()
            val fichaMedica = if (cbFicha.isChecked) 1 else 0

            if (dni.isNotEmpty()) {
                var exito = false

                // Si escribió un teléfono nuevo, lo actualizamos
                if (nuevoTel.isNotEmpty()) {
                    val filasTel = dbHelper.actualizarTelefonoPersona(dni, nuevoTel)
                    if (filasTel > 0) exito = true
                }

                // SIEMPRE actualizamos la ficha médica (al marcar o quitar selección)
                val filasFicha = dbHelper.actualizarFichaMedica(dni, fichaMedica)
                if (filasFicha > 0) exito = true

                if (exito) {
                    AlertDialog.Builder(this)
                        .setTitle("Modificación Exitosa")
                        .setMessage("Los datos del cliente (No Socio) con DNI $dni fueron actualizados correctamente.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Cliente no encontrado")
                        .setMessage("No se encontró ningún registro con el DNI '$dni'. Verifique el número e intente nuevamente.")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Atención")
                    .setMessage("Debe ingresar obligatoriamente el DNI del cliente que desea modificar.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }
    }
}