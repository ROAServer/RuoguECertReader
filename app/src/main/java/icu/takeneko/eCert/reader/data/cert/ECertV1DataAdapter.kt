package icu.takeneko.eCert.reader.data.cert

import icu.takeneko.eCert.reader.activity.ECertV1DisplayActivity
import icu.takeneko.eCert.reader.data.CardHeader
import icu.takeneko.eCert.reader.data.DataAdapter
import icu.takeneko.eCert.reader.getLongLE
import icu.takeneko.eCert.reader.getShortLE
import icu.takeneko.eCert.reader.getString
import java.nio.ByteBuffer

class ECertV1DataAdapter :
    DataAdapter<ECertV1, ECertV1DisplayActivity>(ECertV1DisplayActivity::class.java) {
    override fun extractDataWithoutSignature(bytes: ByteArray): ByteArray {
        val byteBuffer = ByteBuffer.wrap(bytes)
        byteBuffer.position(16 + 2 + 8 + 1);
        repeat(4){
            val len = byteBuffer.getLongLE()
            byteBuffer.position((byteBuffer.position() + len).toInt())
        }
        val len = byteBuffer.position()
        val byteArray = ByteArray(len)
        byteBuffer.position(0)
        byteBuffer.get(byteArray, 0, len)
        return byteArray
    }

    override fun parseByteArray(bytes: ByteArray): ECertV1 {
        val byteBuffer = ByteBuffer.wrap(bytes)
        val version = byteBuffer.get()
        byteBuffer.position(16)
        if (version.toInt() != 1) throw IllegalArgumentException()
        val cardHeader = CardHeader(version)
        val serialNumber = byteBuffer.getShortLE().toInt()
        val timeStamp = byteBuffer.getLongLE()
        val issuer = byteBuffer.get().toInt()

        val recipient = byteBuffer.getString()
        val giftReason = byteBuffer.getString()
        val description = byteBuffer.getString()
        val postscript = byteBuffer.getString()
        val signature = ByteArray(64)
        val pos = byteBuffer.position()
        var index = 0
        for (i in pos until pos + 64) {
            signature[index++] = byteBuffer.get()
        }
        return ECertV1(
            cardHeader,
            serialNumber,
            timeStamp,
            issuer,
            recipient,
            giftReason,
            description,
            postscript,
            signature
        )
    }
}

