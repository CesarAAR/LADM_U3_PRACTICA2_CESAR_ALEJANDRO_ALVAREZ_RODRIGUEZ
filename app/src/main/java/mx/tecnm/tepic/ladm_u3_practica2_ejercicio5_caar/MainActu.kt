package mx.tecnm.tepic.ladm_u3_practica2_ejercicio5_caar

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_actu.*

class MainActu : AppCompatActivity() {
    var baseDatos = BaseAgenda(this,"basedatos1",null,1)
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_actu)

        var extra=intent.extras
        id=extra!!.getString("IDEVENTO")!!

        textView.setText(textView.text.toString()+"${id}")

        try {
            var base = baseDatos.readableDatabase
            var respuestas = base.query("EVENTOS", arrayOf("LUGAR","HORAA","FECHA","DESCRIPCION"),"ID=?", arrayOf(id),null,null,null)
            if(respuestas.moveToFirst()){
                lugaractu.setText(respuestas.getString(0))
                horaactu.setText(respuestas.getString(1))
                fechaactu.setText(respuestas.getString(2))
                descractu.setText(respuestas.getString(3))
            }else{
                mensaje("ERROR! no se encontro ID")
            }
            base.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
        button3.setOnClickListener {
            actualizar(id)
        }
        button4.setOnClickListener {
            finish()
        }
    }
    private fun actualizar(id: String) {
        try {
            var trans = baseDatos.writableDatabase
            var valores = ContentValues()

            valores.put("LUGAR",lugaractu.text.toString())
            valores.put("HORAA",horaactu.text.toString())
            valores.put("FECHA",fechaactu.text.toString())
            valores.put("DESCRIPCION",descractu.text.toString())
            var res= trans.update("EVENTOS",valores,"ID=?", arrayOf(id))
            if(res>0){
                mensaje("SE ACTUALIZO CORRECTAMENTE ID${id}")
                finish()
            }else{
                mensaje("NO SE PUDO ACTUALIZAR ID")
            }
            trans.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
    }
    private fun mensaje(s:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i-> d.dismiss()}.show()
    }
}