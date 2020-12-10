package mx.tecnm.tepic.ladm_u3_practica2_ejercicio5_caar

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseAgenda(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context,name,factory,version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE EVENTOS(ID INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, LUGAR VARCHAR(60), HORAA TIME, FECHA DATE, DESCRIPCION VARCHAR(100))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}