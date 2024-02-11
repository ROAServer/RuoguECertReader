package icu.takeneko.eCert.reader.data.cert

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import icu.takeneko.eCert.reader.data.CardHeader
import java.lang.RuntimeException
import kotlin.properties.Delegates

class ECertV1(parcel: Parcel?) : Parcelable {
    lateinit var cardHeader: CardHeader
    var serialNumber by Delegates.notNull<Int>()
    var timeStamp by Delegates.notNull<Long>()
    var issuer by Delegates.notNull<Int>()
    lateinit var recipient: String
    lateinit var giftReason: String
    lateinit var description: String
    lateinit var postscript: String
    var signature: ByteArray = ByteArray(64)

    init {
        if (parcel != null) {
            cardHeader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                parcel.readParcelable(this.javaClass.classLoader, CardHeader::class.java)!!
            } else {
                parcel.readParcelable<CardHeader>(this.javaClass.classLoader)!!
            }
            serialNumber = parcel.readInt()
            timeStamp = parcel.readLong()
            issuer = parcel.readInt()
            recipient = parcel.readString()!!
            giftReason = parcel.readString()!!
            description = parcel.readString()!!
            postscript = parcel.readString()!!
            parcel.readByteArray(signature)
        }
    }

    constructor(
        cardHeader: CardHeader,
        serialNumber: Int,
        timeStamp: Long,
        issuer: Int,
        recipient: String,
        giftReason: String,
        description: String,
        postscript: String,
        signature: ByteArray
    ) : this(null) {
        this.cardHeader = cardHeader
        this.serialNumber = serialNumber
        this.timeStamp = timeStamp
        this.issuer = issuer
        this.recipient = recipient
        this.giftReason = giftReason
        this.description = description
        this.postscript = postscript
        System.arraycopy(signature, 0, this.signature, 0, 64)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cardHeader, flags)
        parcel.writeInt(serialNumber)
        parcel.writeLong(timeStamp)
        parcel.writeInt(issuer)
        parcel.writeString(recipient)
        parcel.writeString(giftReason)
        parcel.writeString(description)
        parcel.writeString(postscript)
        parcel.writeByteArray(signature)
    }

    override fun describeContents(): Int {
        return 0
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        return "ECertV1(cardHeader=$cardHeader," +
                " serialNumber=$serialNumber," +
                " timeStamp=$timeStamp," +
                " issuer=$issuer," +
                " recipient='$recipient'," +
                " giftReason='$giftReason'," +
                " description='$description'," +
                " postscript='$postscript'," +
                " signature=${signature.toHexString(HexFormat.UpperCase)})"
    }

    companion object CREATOR : Parcelable.Creator<ECertV1> {
        override fun createFromParcel(parcel: Parcel): ECertV1 {
            return ECertV1(parcel)
        }

        override fun newArray(size: Int): Array<ECertV1?> {
            return arrayOfNulls(size)
        }
    }


}