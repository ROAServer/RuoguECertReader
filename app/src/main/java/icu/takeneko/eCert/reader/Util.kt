package icu.takeneko.eCert.reader

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import java.nio.ByteBuffer

fun logI(string: String) = Log.i("ECert", string)

fun logW(string: String) = Log.w("ECert", string)

fun formatResString(@StringRes format: Int, context: Context, vararg objects: Any?): String {
    return context.getString(format).format(*objects)
}

@OptIn(ExperimentalStdlibApi::class)
fun hexView(byteArray: ByteArray): String =
    buildString {
        append("HexView: \n")
        append("          0 1 2 3 4 5 6 7  8 9 A B C D E F\n")
        var ln = 0
        var s = byteArray.toHexString(HexFormat.UpperCase)
        s += "0".repeat(32 - (s.length % 32))
        var line = String()
        for (c in s) {
            if (line.length == 33) {
                append(ln.toHexString(HexFormat.UpperCase).padStart(6, '0')).append("  ")
                    .append(line).append("\n")
                line = String()
                ln += 16
            }
            if (line.length == 16) {
                line += " "
            }
            line += c
        }
        append(ln.toHexString(HexFormat.UpperCase).padStart(6, '0')).append("  ")
            .append(line)
    }


fun ByteBuffer.getString(): String {
    val length = getLongLE()
    val array = ByteArray(length.toInt())
    val pos = position()
    var index = 0
    for (i in pos until pos + length) {
        array[index++] = this.get()
    }
    return array.decodeToString()
}

fun ByteBuffer.getIntLE(): Int {
    val i = getInt()
    return Integer.reverseBytes(i)
}

fun ByteBuffer.getLongLE(): Long {
    val l = getLong()
    return java.lang.Long.reverseBytes(l)
}

fun ByteBuffer.getShortLE(): Short {
    val s = getShort()
    return java.lang.Short.reverseBytes(s)
}