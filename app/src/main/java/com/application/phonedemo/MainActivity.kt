package com.application.phonedemo

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PhonePe.init(this, PhonePeEnvironment.RELEASE,"M10B2480H5EN", "c00d1041533b4101882d7f88d7456ab1")
        findViewById<Button>(R.id.btn).setOnClickListener{
            startPhonePayPaymentGateWay()
        }
    }


    private fun startPhonePayPaymentGateWay() {

        val apiEndPoint = "/pg/v1/pay"
        val txnId = "C" + System.currentTimeMillis().toString()
        val data = JSONObject()
        data.put("merchantTransactionId",txnId)
        data.put("merchantId", "M10B2480H5EN")
        data.put("merchantUserId", "MUID101")
        data.put("amount" , 999*100)
        data.put("mobileNumber", "9548829374")
        data.put("callbackUrl","https://comeato.com/phonepe_callback_app")
        val mPaymentInstrument = JSONObject()
        mPaymentInstrument.put("type","PAY_PAGE")

        data.put("paymentInstrument" , mPaymentInstrument)

        val deviceContext = JSONObject()
        deviceContext.put("deviceOS","ANDROID")
        data.put("deviceContext" , deviceContext)
        val base64Body: String = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()),Base64.NO_WRAP)

        val checkSum  = sha256(base64Body+"/pg/v1/pay"+"9f4d0096-e353-4688-9053-654747a0da68")+"###1"

        Log.d("ERRROR------->","checksum: $checkSum")
        Log.d("ERRROR------->", "base64: "+base64Body)
        Log.d("ERRROR------->","checksum: $data")


        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(base64Body)
            .setChecksum(checkSum)
            .setUrl(apiEndPoint)
            .build()

        PhonePe.getImplicitIntent(this,b2BPGRequest,"")?.let {
            phonePayIntentLauncher.launch(it)
        }


    }

    private fun sha256(input: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(charset("UTF-8")))
            val hexString = java.lang.StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    private val phonePayIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d("ERRROR------->", ": "+it.data)
        if(it.resultCode== RESULT_OK)
        {
        }

    }
}