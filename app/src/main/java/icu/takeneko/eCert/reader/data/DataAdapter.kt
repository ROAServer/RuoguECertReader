package icu.takeneko.eCert.reader.data

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import icu.takeneko.eCert.reader.DisplayActivity
import java.lang.IllegalArgumentException

abstract class DataAdapter<E : Parcelable, A : DisplayActivity<E>>(private val activity: Class<A>) {
    private fun startDisplayActivity(context: Context, data: E, dataInBytesWithoutSignature:ByteArray) {
        val intent = Intent(context, activity)
        intent.putExtra("data", data)
        intent.putExtra("dataInBytesWithoutSignature", dataInBytesWithoutSignature)
        context.startActivity(intent)
    }

    abstract fun extractDataWithoutSignature(bytes: ByteArray):ByteArray
    abstract fun parseByteArray(bytes: ByteArray): E
    fun startDisplayActivity(context: Context, bytes: ByteArray): Boolean {
        try {
            val data = parseByteArray(bytes)
            startDisplayActivity(context, data, extractDataWithoutSignature(bytes))
        } catch (e: IllegalArgumentException) {
            return false
        }
        return true
    }
}