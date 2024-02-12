package icu.takeneko.eCert.reader.signature

import android.content.Context
import icu.takeneko.eCert.reader.hexView
import icu.takeneko.eCert.reader.logI
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class SignatureChecker(context: Context, origin: String) {

    private val publicKey: PublicKey

    init {
        publicKey =
            KeyFactory.getInstance("EC")
                .generatePublic(
                    X509EncodedKeySpec(context.assets.run {
                        open("$origin.x509").readBytes().also { logI("private key:"+hexView(it)) }
                    })
                )
    }

    fun checkSignature(originalData: ByteArray, signature: ByteArray): Boolean {
        logI("originalData: ${originalData.size} bytes")
        logI(hexView(originalData))
        logI("signature(${signature.size} bytes):")
        logI(hexView(signature))
        val instance = Signature.getInstance("SHA256withECDSA")
        instance.initVerify(publicKey)
        instance.update(originalData)
        return instance.verify(signature)
    }
}