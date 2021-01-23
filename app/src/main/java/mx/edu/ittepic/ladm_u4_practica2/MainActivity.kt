package mx.edu.ittepic.ladm_u4_practica2

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var opciones = arrayOf("FACEBOOK",
        "INSTAGRAM",
        "TWITTER",
        "LINKEDIN")
    var valorOpciones = arrayOf("https://www.facebook.com/ismael.lopez.5264/",
        "https://www.instagram.com/ismaelcaslop/",
        "https://twitter.com/IsCasLop",
        "https://www.linkedin.com/in/ismaelcaslop/")
    val baseDatos = BaseDatos(this, "contacto", null, 1)
    val timer = object : CountDownTimer(5000, 100){
        override fun onTick(millisUntilFinished: Long) { }

        override fun onFinish() {
            try {
                val leer = baseDatos.readableDatabase
                val result = leer.query("enviar", arrayOf("*"), "enviado = ?", arrayOf("0"), null, null, null)
                if(result.moveToFirst()) {
                    do {
                        SmsManager.getDefault().sendTextMessage(result.getString(0), null, result.getString(1), null, null)
                    } while (result.moveToNext())
                    val update = baseDatos.writableDatabase
                    val values = ContentValues()
                    values.put("enviado", "1")
                    var res = update.update("enviar", values, "enviado = ?", arrayOf("0"))
                }
                baseDatos.close()
            } catch (e: SQLiteException) {  }
            start()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        try {
            val baseDatos = BaseDatos(this, "contacto", null, 1)
            val leer = baseDatos.readableDatabase
            val result = leer.query("contacto", arrayOf("*"), null, null, null, null, null)
            if(!result.moveToFirst()) {
                (0..opciones.size-1).forEach {
                    var insertar = baseDatos.writableDatabase
                    var sql = "INSERT INTO contacto VALUES('${opciones[it]}', '${valorOpciones[it]}')"
                    insertar.execSQL(sql)
                }
            }
            comandos.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, opciones)
            baseDatos.close()
            mensaje()
        } catch (e: SQLiteException) {
            Toast.makeText(this, "${e.message}", Toast.LENGTH_LONG).show()
        }

        button2.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECEIVE_SMS), 1)
            }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS), 2)
            }
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS), 1)
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS), 2)
        }

        button.setOnClickListener {
                SmsManager.getDefault().sendTextMessage(editText.text.toString(), null, editText2.text.toString(), null, null)
                Toast.makeText(this, "Se envio el SMS", Toast.LENGTH_LONG).show()
        }

        timer.start()
    }

    fun mensaje(){
        Toast.makeText(this, "Se enviaron los SMS pendientes", Toast.LENGTH_LONG).show()
    }
}