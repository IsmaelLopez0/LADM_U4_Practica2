package mx.edu.ittepic.ladm_u4_practica2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context?, name: String?,
                factory: SQLiteDatabase.CursorFactory?,
                version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE contacto (nombre VARCHAR(250), valor VARCHAR(250))")
        db.execSQL("CREATE TABLE enviar (telefono VARCHAR(250), valor VARCHAR(250), enviado VARCHAR(1))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}