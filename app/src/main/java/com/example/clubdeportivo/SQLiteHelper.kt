package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "ClubDeportivo.db"

        const val TABLA_CLIENTES = "clientes"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_APELLIDO = "apellido"
        const val COL_DNI = "dni"
        const val COL_TELEFONO = "telefono"
        const val COL_ES_SOCIO = "es_socio"
        const val COL_FICHA_MEDICA = "ficha_medica"

        const val TABLA_PAGOS = "pagos"
        const val COL_PAGO_ID = "id_pago"
        const val COL_PAGO_DNI = "dni_socio"
        const val COL_MONTO = "monto"
        const val COL_FECHA = "fecha"

        const val TABLA_ACTIVIDADES = "actividades"
        const val COL_ACT_ID = "id_act"
        const val COL_ACT_NOMBRE = "nombre_act"
        const val COL_ACT_TIPO = "tipo_act"
        const val COL_ACT_DESC = "descripcion"
        const val COL_ACT_CUPO = "cupo"

        const val TABLA_INSCRIPCIONES = "inscripciones"
        const val COL_INS_ID = "id_ins"
        const val COL_INS_DNI = "dni_persona"
        const val COL_INS_ACT = "actividad_nombre"
        const val COL_INS_FECHA = "fecha_ins"

        const val TABLA_PROFESORES = "profesores"
        const val COL_PROF_ID = "id_prof"
        const val COL_PROF_NOMBRE = "nombre"
        const val COL_PROF_APELLIDO = "apellido"
        const val COL_PROF_DNI = "dni"
        const val COL_PROF_ESP = "especialidad"

        const val TABLA_SUPLENCIAS = "suplencias"
        const val COL_SUP_DNI_TIT = "dni_titular"
        const val COL_SUP_DNI_SUP = "dni_suplente"
        const val COL_SUP_ACT = "actividad"
        const val COL_SUP_FECHA_INICIO = "fecha_inicio"
        const val COL_SUP_DIAS = "dias_duracion"

        const val TABLA_NUTRICION = "turnos_nutricion"
        const val COL_NUT_DNI = "dni_socio"
        const val COL_NUT_FECHA = "fecha"
        const val COL_NUT_HORA = "horario"

        const val TABLA_ASISTENCIAS = "asistencias"
        const val COL_ASIS_ID = "id_asistencia"
        const val COL_ASIS_DNI_PROF = "dni_profesor"
        const val COL_ASIS_ACT = "actividad"
        const val COL_ASIS_FECHA = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLA_CLIENTES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT,
                $COL_APELLIDO TEXT,
                $COL_DNI TEXT UNIQUE,
                $COL_TELEFONO TEXT,
                $COL_ES_SOCIO INTEGER,
                $COL_FICHA_MEDICA INTEGER DEFAULT 0
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_PAGOS (
                $COL_PAGO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PAGO_DNI TEXT,
                $COL_MONTO REAL,
                $COL_FECHA TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_ACTIVIDADES (
                $COL_ACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ACT_NOMBRE TEXT,
                $COL_ACT_TIPO TEXT,
                $COL_ACT_DESC TEXT,
                $COL_ACT_CUPO INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_INSCRIPCIONES (
                $COL_INS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_INS_DNI TEXT,
                $COL_INS_ACT TEXT,
                $COL_INS_FECHA TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_PROFESORES (
                $COL_PROF_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PROF_NOMBRE TEXT,
                $COL_PROF_APELLIDO TEXT,
                $COL_PROF_DNI TEXT UNIQUE,
                $COL_PROF_ESP TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_SUPLENCIAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SUP_DNI_TIT TEXT,
                $COL_SUP_DNI_SUP TEXT,
                $COL_SUP_ACT TEXT,
                $COL_SUP_FECHA_INICIO TEXT,
                $COL_SUP_DIAS INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_NUTRICION (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NUT_DNI TEXT,
                $COL_NUT_FECHA TEXT,
                $COL_NUT_HORA TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLA_ASISTENCIAS (
                $COL_ASIS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ASIS_DNI_PROF TEXT,
                $COL_ASIS_ACT TEXT,
                $COL_ASIS_FECHA TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_CLIENTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PAGOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ACTIVIDADES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_INSCRIPCIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PROFESORES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SUPLENCIAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_NUTRICION")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ASISTENCIAS")
        onCreate(db)
    }

    // ================================================================
    // BLOQUE: MÉTODOS DE INSERCIÓN
    // ================================================================
    fun insertarPersona(nombre: String, apellido: String, dni: String, tel: String, esSocio: Int, fichaMedica: Int): Long {
        val valores = ContentValues()
        valores.put(COL_NOMBRE, nombre)
        valores.put(COL_APELLIDO, apellido)
        valores.put(COL_DNI, dni)
        valores.put(COL_TELEFONO, tel)
        valores.put(COL_ES_SOCIO, esSocio)
        valores.put(COL_FICHA_MEDICA, fichaMedica)

        return writableDatabase.insert(TABLA_CLIENTES, null, valores)
    }

    fun insertarPago(dni: String, monto: Double, fecha: String): Long {
        val valores = ContentValues()
        valores.put(COL_PAGO_DNI, dni)
        valores.put(COL_MONTO, monto)
        valores.put(COL_FECHA, fecha)

        return writableDatabase.insert(TABLA_PAGOS, null, valores)
    }

    fun insertarActividad(nombre: String, tipo: String, desc: String, cupo: Int): Long {
        val valores = ContentValues()
        valores.put(COL_ACT_NOMBRE, nombre)
        valores.put(COL_ACT_TIPO, tipo)
        valores.put(COL_ACT_DESC, desc)
        valores.put(COL_ACT_CUPO, cupo)

        return writableDatabase.insert(TABLA_ACTIVIDADES, null, valores)
    }

    fun insertarInscripcion(dni: String, act: String, fecha: String): Long {
        val valores = ContentValues()
        valores.put(COL_INS_DNI, dni)
        valores.put(COL_INS_ACT, act)
        valores.put(COL_INS_FECHA, fecha)

        return writableDatabase.insert(TABLA_INSCRIPCIONES, null, valores)
    }

    fun insertarProfesor(nom: String, ape: String, dni: String, esp: String): Long {
        val valores = ContentValues()
        valores.put(COL_PROF_NOMBRE, nom)
        valores.put(COL_PROF_APELLIDO, ape)
        valores.put(COL_PROF_DNI, dni)
        valores.put(COL_PROF_ESP, esp)

        return writableDatabase.insert(TABLA_PROFESORES, null, valores)
    }

    fun insertarSuplencia(titular: String, suplente: String, act: String, fechaInicio: String, dias: Int): Long {
        val valores = ContentValues()
        valores.put(COL_SUP_DNI_TIT, titular)
        valores.put(COL_SUP_DNI_SUP, suplente)
        valores.put(COL_SUP_ACT, act)
        valores.put(COL_SUP_FECHA_INICIO, fechaInicio)
        valores.put(COL_SUP_DIAS, dias)

        return writableDatabase.insert(TABLA_SUPLENCIAS, null, valores)
    }

    fun insertarTurnoNutricion(dni: String, fecha: String, hora: String): Long {
        val valores = ContentValues()
        valores.put(COL_NUT_DNI, dni)
        valores.put(COL_NUT_FECHA, fecha)
        valores.put(COL_NUT_HORA, hora)

        return writableDatabase.insert(TABLA_NUTRICION, null, valores)
    }

    fun insertarAsistencia(dniProfesor: String, actividad: String, fecha: String): Long {
        val valores = ContentValues()
        valores.put(COL_ASIS_DNI_PROF, dniProfesor)
        valores.put(COL_ASIS_ACT, actividad)
        valores.put(COL_ASIS_FECHA, fecha)

        return writableDatabase.insert(TABLA_ASISTENCIAS, null, valores)
    }

    // ================================================================
    // BLOQUE: MÉTODOS DE CONSULTA (READ)
    // ================================================================
    fun obtenerPersonas(esSocio: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_CLIENTES WHERE $COL_ES_SOCIO = ?",
            arrayOf(esSocio.toString())
        )
    }

    fun obtenerActividades(): Cursor = readableDatabase.rawQuery("SELECT * FROM $TABLA_ACTIVIDADES", null)
    fun obtenerProfesores(): Cursor = readableDatabase.rawQuery("SELECT * FROM $TABLA_PROFESORES", null)
    fun obtenerPagos(): Cursor = readableDatabase.rawQuery("SELECT * FROM $TABLA_PAGOS", null)
    fun obtenerSuplencias(): Cursor = readableDatabase.rawQuery("SELECT * FROM $TABLA_SUPLENCIAS", null)

    /**
     * Consulta relacional para obtener el último pago de cada cliente registrado.
     * Modificación: Se incluye el campo 'dni' en el SELECT para asociar reportes.
     */
    fun obtenerUltimosPagosClientes(): Cursor {
        val consulta = """
            SELECT c.${COL_DNI} AS dni, 
                   c.${COL_NOMBRE} AS nombre, 
                   c.${COL_APELLIDO} AS apellido, 
                   c.${COL_ES_SOCIO} AS es_socio, 
                   p.${COL_FECHA} AS ultimo_pago 
            FROM $TABLA_CLIENTES c 
            LEFT JOIN $TABLA_PAGOS p ON c.${COL_DNI} = p.${COL_PAGO_DNI} 
            WHERE p.${COL_PAGO_ID} = (SELECT MAX(id_pago) FROM $TABLA_PAGOS WHERE ${COL_PAGO_DNI} = c.${COL_DNI}) 
            OR p.${COL_PAGO_ID} IS NULL
        """
        return readableDatabase.rawQuery(consulta, null)
    }

    fun contarInscripcionesPorActividad(nombreActividad: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLA_INSCRIPCIONES WHERE $COL_INS_ACT = ?",
            arrayOf(nombreActividad)
        )
        var cuenta = 0
        if (cursor.moveToFirst()) {
            cuenta = cursor.getInt(0)
        }
        cursor.close()
        return cuenta
    }

    fun tieneFichaMedica(dni: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT $COL_FICHA_MEDICA FROM $TABLA_CLIENTES WHERE $COL_DNI = ?",
            arrayOf(dni)
        )
        var tieneFicha = false
        if (cursor.moveToFirst()) {
            tieneFicha = cursor.getInt(0) == 1
        }
        cursor.close()
        return tieneFicha
    }

    // ================================================================
    // BLOQUE: MÉTODOS DE ACTUALIZACIÓN (UPDATE)
    // ================================================================
    fun actualizarTelefonoPersona(dni: String, nuevoTel: String): Int {
        val valores = ContentValues()
        valores.put(COL_TELEFONO, nuevoTel)

        return writableDatabase.update(TABLA_CLIENTES, valores, "$COL_DNI = ?", arrayOf(dni))
    }

    fun actualizarActividad(nombreAnt: String, nuevoNom: String, nuevoTipo: String, nuevaDesc: String, nuevoCupo: Int): Int {
        val valores = ContentValues()
        valores.put(COL_ACT_NOMBRE, nuevoNom)
        valores.put(COL_ACT_TIPO, nuevoTipo)
        valores.put(COL_ACT_DESC, nuevaDesc)
        valores.put(COL_ACT_CUPO, nuevoCupo)

        return writableDatabase.update(TABLA_ACTIVIDADES, valores, "$COL_ACT_NOMBRE = ?", arrayOf(nombreAnt))
    }

    fun actualizarFichaMedica(dni: String, tieneFicha: Int): Int {
        val valores = ContentValues()
        valores.put(COL_FICHA_MEDICA, tieneFicha)

        return writableDatabase.update(TABLA_CLIENTES, valores, "$COL_DNI = ?", arrayOf(dni))
    }
}