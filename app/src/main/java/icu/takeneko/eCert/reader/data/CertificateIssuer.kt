package icu.takeneko.eCert.reader.data

import androidx.annotation.StringRes
import icu.takeneko.eCert.reader.R

enum class CertificateIssuer(val value:Int, @StringRes val stringId: Int) {
    RUOGU_STUDIO(1, R.string.text_ruogu_studio),
    ROA_SERVER(2, R.string.text_roa_server);

    companion object{
        fun match(value: Int):Int{
            return when(value){
                1 -> RUOGU_STUDIO.stringId
                2 -> ROA_SERVER.stringId
                else -> R.string.text_unknown
            }
        }
    }
}