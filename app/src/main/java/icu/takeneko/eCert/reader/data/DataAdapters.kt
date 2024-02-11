package icu.takeneko.eCert.reader.data

import android.content.Context
import icu.takeneko.eCert.reader.data.cert.ECertV1DataAdapter
import icu.takeneko.eCert.reader.logI

enum class DataAdapters(private val dataAdapter: DataAdapter<*, *>) {
    CERT_V1(ECertV1DataAdapter());

    companion object {
        fun tryStartActivity(context: Context, bytes: ByteArray) {
            for (value in entries) {
                logI("Attempt to match $value")
                val result = value.dataAdapter.startDisplayActivity(context, bytes)
                if (result) {
                    return
                }
            }
            throw IllegalArgumentException("Illegal card data")
        }
    }

}