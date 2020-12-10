package mx.tecnm.tepic.ladm_u3_practica2_ejercicio5_caar

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_consultas.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class consultas : AppCompatActivity() {
    var baseDatos = BaseAgenda(this,"basedatos1",null,1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultas)

        buttonConsultar.setOnClickListener {
            if(radioButtonDescr.isChecked){
                cargarporDesc()
            }
            if(radioButtonLugar.isChecked){
                cargarporLugar()
            }
            if(radioButtonFech.isChecked){
                cargarporFECHA()
            }
            if(!radioButtonFech.isChecked && !radioButtonLugar.isChecked && !radioButtonDescr.isChecked){
                mensaje("TIENES QUE ELEGIR UNO")
            }
        }
        buttonSalir.setOnClickListener {
            finish()
        }
    }

    /*override fun onResume(){
        super.onResume()
        cargarporDesc()
        cargarporLugar()
        cargarporFECHA()
    }*/

    private fun cargarporDesc() {
        try{
            var trans = baseDatos.readableDatabase
            var eventoss = ArrayList<String>()

            var respuesta = trans.rawQuery("SELECT * FROM EVENTOS ORDER BY DESCRIPCION ASC", null)
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion = "ID: ${respuesta.getInt(0)}\nLUGAR: " +
                            "${respuesta.getString(1)}\nHORA: ${respuesta.getString(2)}\n" +
                            "FECHA: ${respuesta.getString(3)}\n" +
                            "DESCRIPCION: ${respuesta.getString(4)}"
                    eventoss.add(concatenacion)
                }while (respuesta.moveToNext())
            }else{
                eventoss.add("NO HAY EVENTOS INSERTADAS")
            }
            listconsulta.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventoss)
            trans.close()
        }catch (e: SQLiteException){
            mensaje("Error: "+e.message!!)
        }
    }
    private fun cargarporLugar() {
        try{
            var trans = baseDatos.readableDatabase
            var eventoss = ArrayList<String>()

            var respuesta = trans.rawQuery("SELECT * FROM EVENTOS ORDER BY LUGAR ASC", null)
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion = "ID: ${respuesta.getInt(0)}\nLUGAR: " +
                            "${respuesta.getString(1)}\nHORA: ${respuesta.getString(2)}\n" +
                            "FECHA: ${respuesta.getString(3)}\n" +
                            "DESCRIPCION: ${respuesta.getString(4)}"
                    eventoss.add(concatenacion)
                }while (respuesta.moveToNext())
            }else{
                eventoss.add("NO HAY EVENTOS INSERTADAS")
            }
            listconsulta.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventoss)
            trans.close()
        }catch (e: SQLiteException){
            mensaje("Error: "+e.message!!)
        }
    }
    private fun cargarporFECHA() {
        try{
            //date('now','start of day')>FECHA OR
            var trans = baseDatos.readableDatabase
            var eventoss = ArrayList<String>()
            var respuesta = trans.rawQuery("SELECT * FROM EVENTOS WHERE FECHA>julianday(date('now')) ORDER BY FECHA ASC", null)
            if(respuesta.moveToFirst()){
                    do {
                        var concatenacion = "ID: ${respuesta.getInt(0)}\nLUGAR: " +
                                "${respuesta.getString(1)}\nHORA: ${respuesta.getString(2)}\n" +
                                "FECHA: ${respuesta.getString(3)}\n" +
                                "DESCRIPCION: ${respuesta.getString(4)}"
                        eventoss.add(concatenacion)
                    } while (respuesta.moveToNext())
            }else{
                eventoss.add("NO HAY EVENTOS INSERTADAS")
            }
            listconsulta.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventoss)
            trans.close()
        }catch (e: SQLiteException){
            mensaje("Error: "+e.message!!)
        }
    }

    private fun mensaje(s:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i-> d.dismiss()}.show()
    }
}