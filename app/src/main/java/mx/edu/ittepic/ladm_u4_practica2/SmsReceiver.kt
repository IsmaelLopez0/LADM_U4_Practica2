package mx.edu.ittepic.ladm_u4_practica2

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver(){
    var palabraClave = "CONTACTO"

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if( extras != null ){
            var sms = extras.get("pdus") as Array<Any>
            for (indice in sms.indices){
                val formato = extras.getString("format")

                var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                when(mensajeValido(contenidoSMS, context)) {
                    0 -> {
                        try {
                            val baseDatos = BaseDatos(context, "contacto", null, 1)
                            val insertar = baseDatos.writableDatabase
                            val values = ContentValues()
                            values.put("telefono", celularOrigen)
                            values.put("valor", comprobarOpcion(contenidoSMS, context))
                            values.put("enviado", "0")
                            val consulta = insertar.insert("enviar", null, values)
                            if(consulta==-1L){ Toast.makeText(context, "No se podrá enviar la respuesta", Toast.LENGTH_LONG).show() }
                            baseDatos.close()
                        } catch(e: SQLiteException){ Toast.makeText(context, e.message, Toast.LENGTH_LONG).show() }
                    }
                    2 -> {
                        try {
                            val baseDatos = BaseDatos(context, "contacto", null, 1)
                            val insertar = baseDatos.writableDatabase
                            val values = ContentValues()
                            values.put("telefono", celularOrigen)
                            values.put("valor", "Asegurese de solo escribir la palabra $palabraClave y la opción deseada separadas por un espacio.")
                            values.put("enviado", "0")
                            val consulta = insertar.insert("enviar", null, values)
                            if(consulta==-1L){ Toast.makeText(context, "No se podrá enviar la respuesta", Toast.LENGTH_LONG).show() }
                            baseDatos.close()
                        } catch(e: SQLiteException){ Toast.makeText(context, e.message, Toast.LENGTH_LONG).show() }
                    }
                    3 -> {
                        try {
                            val baseDatos = BaseDatos(context, "contacto", null, 1)
                            val insertar = baseDatos.writableDatabase
                            val values = ContentValues()
                            values.put("telefono", celularOrigen)
                            values.put("valor", "Opción no válida, favor de comprobar")
                            values.put("enviado", "0")
                            val consulta = insertar.insert("enviar", null, values)
                            if(consulta==-1L){ Toast.makeText(context, "No se podrá enviar la respuesta", Toast.LENGTH_LONG).show() }
                            baseDatos.close()
                        } catch(e: SQLiteException){ Toast.makeText(context, e.message, Toast.LENGTH_LONG).show() }
                    }
                }
            }
        }
    }

    private fun mensajeValido(sms: String, context: Context): Int{
        var smsSplit = sms.split(" ")
        if (smsSplit[0] != palabraClave) return 1
        if (smsSplit.size != 2) return 2
        if (comprobarOpcion(sms, context) == "") return 3
        return 0
    }

    private fun comprobarOpcion(sms: String, context: Context): String{
        var smsSplit = sms.split(" ")
        var opciones = ArrayList<String>()
        var valorOpcion = ArrayList<String>()
        try {
            var baseDatos = BaseDatos(context, "contacto", null, 1)
            var leer = baseDatos.readableDatabase
            var consulta = leer.query("contacto", null, null, null, null, null, null)
            if(consulta.moveToFirst()){
                do {
                    opciones.add(consulta.getString(0))
                    valorOpcion.add(consulta.getString(1))
                }while (consulta.moveToNext())
            }
            baseDatos.close()
        } catch (e: SQLiteException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
        (0 until opciones.size).forEach {
            Log.d("Validar", "${opciones[it]} == ${smsSplit[1]}")
            if (opciones[it] == smsSplit[1]){
                return valorOpcion[it]
            }
        }
        return ""
    }

}