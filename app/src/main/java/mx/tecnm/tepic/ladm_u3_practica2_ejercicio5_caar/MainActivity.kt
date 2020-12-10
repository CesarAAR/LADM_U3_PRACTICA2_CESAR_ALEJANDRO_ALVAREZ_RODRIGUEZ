package mx.tecnm.tepic.ladm_u3_practica2_ejercicio5_caar

import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_actu.*
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var baseDatos = BaseAgenda(this,"basedatos1",null,1)
    var baseRemotas = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    var idSeleccionadoEnLista = -1
    var listIDEliminados = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txttime= findViewById<EditText>(R.id.horaagenda)

       /* baseRemotas.collection("eventos")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    mensaje("ERROR! No se pudo recuperar data desde NUBE")
                    return@addSnapshotListener
                }
            }*/

        txttime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                txttime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        fechaagenda.setOnClickListener{
            showDatePickerDialog()
        }

        button.setOnClickListener{
            insertar()
        }
        button2.setOnClickListener {
            syncroInsertar()
            comprobarDELETE()
        }
        buttonVentanaConsulta.setOnClickListener {
            var intent = Intent(this,consultas::class.java)
            startActivity(intent)
        }
        buttonEliminarFiltro.setOnClickListener {
            var intento = Intent(this,EliFilacti::class.java)
            startActivity(intento)
        }
        cargarContactos()
    }

    private fun comprobar() {
        baseRemotas.collection("EVENTOS")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException->
                if(firebaseFirestoreException!=null){
                    mensaje("ERROR! No se pudo recuperar data desde la nube")
                    return@addSnapshotListener
                }
                var cadena = ""
                for(registro in querySnapshot!!){
                    cadena="ID: ${registro.id}\nLUGAR: ${registro.getString("LUGAR")}\n" +
                            "HORA: ${registro.getString("HORA")}\nFECHA: ${registro.getString("FECHA")}\n" +
                            "DESCRIPCION: ${registro.getString("DESCRIPCION")}"
                    mensaje("${cadena}")
                }
            }
    }

    private fun comprobarDELETE(){
        /*var trans = baseDatos.readableDatabase
        var respuesta = trans.rawQuery("SELECT * FROM EVENTOS",null)

        baseRemotas.collection("EVENTOS")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException->
                if(firebaseFirestoreException!=null){
                    mensaje("ERROR! No se pudo recuperar data desde la nube")
                    return@addSnapshotListener
                }

                for(registro in querySnapshot!!){
                    respuesta.moveToFirst()
                    do{
                        if(registro.id.toInt()==respuesta.getInt(0)){
                            mensaje("Si esta ${registro.id}")
                        }
                    }while (respuesta.moveToNext())
                }
                trans.close()
            }
        */
        for(respuesta in listIDEliminados){
            baseRemotas.collection("EVENTOS")
                .document(respuesta)
                .delete()
                .addOnSuccessListener {
                    listIDEliminados.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"NO SE PUDO ELIMINAR ALGUNOS REGISTROS",Toast.LENGTH_LONG)
                }
        }
    }

    override fun onResume(){
        super.onResume()
        cargarContactos()
    }
    private fun mensaje(s:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i-> d.dismiss()}.show()
    }

    private fun limpiarCampos() {
        lugar.setText("")
        horaagenda.setText("")
        fechaagenda.setText("")
        descripcion.setText("")
    }

    private fun insertar() {
        try {
            var trans = baseDatos.writableDatabase //permite leer y escribir
            var variables= ContentValues()
            variables.put("LUGAR",lugar.text.toString())
            variables.put("HORAA",horaagenda.text.toString())
            variables.put("FECHA",fechaagenda.text.toString())
            variables.put("DESCRIPCION",descripcion.text.toString())

            var respuesta = trans.insert("EVENTOS",null,variables)
            if(respuesta==-1L){
                mensaje("ERROR NO SE PUDO INSERTAR")
            }else{
                mensaje("SE INSERTO CON EXITO")
                limpiarCampos()
            }
            trans.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
        cargarContactos()
    }

    private fun syncroInsertar() {
        try {
            var trans = baseDatos.writableDatabase
            var respuesta = trans.rawQuery("SELECT * FROM EVENTOS",null)
            if(respuesta!=null && respuesta.count!=0){
                respuesta.moveToFirst()
                do{
                    var datosinsertar = hashMapOf<String,Any>()
                    datosinsertar.put("LUGAR",respuesta.getString(1))
                    datosinsertar.put("HORA",respuesta.getString(2))
                    datosinsertar.put("FECHA",respuesta.getString(3))
                    datosinsertar.put("DESCRIPCION",respuesta.getString(4))
                    baseRemotas.collection("EVENTOS")
                        .document(respuesta.getInt(0).toString())
                        .set(datosinsertar as Any)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                            mensaje(it.message!!)
                        }
                }while (respuesta.moveToNext())
            }else{
                Toast.makeText(this,"No hay registros",Toast.LENGTH_LONG)
            }
            trans.close()
        }catch (e:SQLiteException){mensaje(e.message!!)}
    }

    private fun cargarContactos() {
        try{
            var trans = baseDatos.readableDatabase
            var eventoss = ArrayList<String>()

            var respuesta = trans.query("EVENTOS", arrayOf("*"),null,null,null,null,null)

            listaID.clear()
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion = "ID: ${respuesta.getInt(0)}\nLUGAR: " +
                            "${respuesta.getString(1)}\nHORA: ${respuesta.getString(2)}\n" +
                            "FECHA: ${respuesta.getString(3)}\n" +
                            "DESCRIPCION: ${respuesta.getString(4)}"
                    eventoss.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())
            }else{
                eventoss.add("NO HAY EVENTOS INSERTADAS")
            }
            listaEventos.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventoss)
            this.registerForContextMenu(listaEventos)
            listaEventos.setOnItemClickListener { adapterView, view, i, id ->
                idSeleccionadoEnLista=i
                Toast.makeText(this, "Se selecciono elemento ${idSeleccionadoEnLista}",Toast.LENGTH_LONG).show()
            }
            trans.close()
        }catch (e: SQLiteException){
            mensaje("Error: "+e.message!!)
        }
    }
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        var inflaterOB = menuInflater
        inflaterOB.inflate(R.menu.menuppal,menu)
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(idSeleccionadoEnLista==-1){
            mensaje("ERROR! debes dar clic primero en un item para ACTUALIZAR/BORRAR")
            return true
        }

        when(item.itemId){
            R.id.itemactualizar->{
                var itent=Intent(this, MainActu::class.java)
                itent.putExtra("IDEVENTO",listaID.get(idSeleccionadoEnLista))
                startActivity(itent)
            }
            R.id.itemeliminar->{
                var idEliminar = listaID.get(idSeleccionadoEnLista)
                AlertDialog.Builder(this)
                    .setTitle("ATENCION")
                    .setMessage("ESTAS SEGURO DE ELIMINAR ID: ${idEliminar}?")
                    .setPositiveButton("Eliminar"){d,i->
                        listIDEliminados.add(idEliminar.toString())
                        eliminar(idEliminar)
                    }
                    .setNeutralButton("NO"){d,i->}
                    .show()
            }
            R.id.itemsalir->{
                finish()
            }
        }
        idSeleccionadoEnLista==-1
        return true
    }

    private fun eliminar(ideliminar: String) {
        try {
            var trans = baseDatos.writableDatabase
            var resultados= trans.delete("EVENTOS","ID=?",
                arrayOf(ideliminar))
            if(resultados==0){
                mensaje("ERROR! No se pudo eliminar")
            }else{
                mensaje("Se logro eliminar con exito el ID ${ideliminar}")
            }
            trans.close()
            cargarContactos()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month+1, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        fechaagenda.setText("$day/$month/$year")
    }
}