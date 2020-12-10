package mx.tecnm.tepic.ladm_u3_practica2_ejercicio5_caar

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_eli_filacti.*

class EliFilacti : AppCompatActivity() {
    var baseDatos = BaseAgenda(this,"basedatos1",null,1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eli_filacti)

        Salirdeaqui.setOnClickListener {
            finish()
        }
        EliminarPorButton.setOnClickListener {
            if (eliLugar.isChecked) {
                var texto = textPorBuscar.text.toString()
                eliminarporLugar(texto)
            }
            if(eliFecha.isChecked){
                var texto = textPorBuscar.text.toString()
                eliminarporFecha(texto)
            }
            if(!eliLugar.isChecked && !eliFecha.isChecked){
                mensaje("No ha seleccionado")
            }
        }
    }
    private fun mensaje(s:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i-> d.dismiss()}.show()
    }

    private fun eliminarporLugar(palabra:String){
        try{
            var trans = baseDatos.writableDatabase
            var respuesta = trans.delete("EVENTOS","LUGAR=?", arrayOf(palabra))
            if(respuesta==0){
                mensaje("no se pudo eliminar")
                textPorBuscar.setText(" ")
            }else{
                mensaje("Se logro eliminar registros")
                finish()
                textPorBuscar.setText(" ")
            }
            trans.close()
        }catch (e:SQLiteException){mensaje(e.message!!)}
    }

    private fun eliminarporFecha(palabra:String){
        try{
            var trans = baseDatos.writableDatabase
            var respuesta = trans.delete("EVENTOS","FECHA=?", arrayOf(palabra))
            if(respuesta==0){
                mensaje("no se pudo eliminar")
                textPorBuscar.setText(" ")
            }else{
                mensaje("Se logro eliminar registros")
                finish()
                textPorBuscar.setText(" ")
            }
            trans.close()
        }catch (e:SQLiteException){mensaje(e.message!!)}
    }
}