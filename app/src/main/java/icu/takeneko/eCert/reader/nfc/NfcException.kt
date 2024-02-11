package icu.takeneko.eCert.reader.nfc

class NfcException(message: String): RuntimeException(message){
    constructor(message: String, e:Exception) : this(message) {
        super.initCause(e)
    }
}