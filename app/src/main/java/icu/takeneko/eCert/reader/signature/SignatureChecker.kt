package icu.takeneko.eCert.reader.signature

import icu.takeneko.eCert.reader.hexView
import icu.takeneko.eCert.reader.logI
import java.security.Signature

object SignatureChecker {

    fun checkSignature(originalData:ByteArray, signature: ByteArray):Boolean{
        logI("originalData:")
        logI(hexView(originalData))
        logI("signature:")
        logI(hexView(signature))
        return true
    }
}