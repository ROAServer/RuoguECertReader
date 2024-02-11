package icu.takeneko.eCert.reader.data

import android.os.Parcel
import android.os.Parcelable
import kotlin.properties.Delegates

class CardHeader(parcel: Parcel?) :Parcelable {
    constructor(version: Byte) : this(null){
        this.version = version
    }

    var version by Delegates.notNull<Byte>()

    init {
        if (parcel != null){
            version = parcel.readByte()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardHeader> {
        override fun createFromParcel(parcel: Parcel): CardHeader {
            return CardHeader(parcel)
        }

        override fun newArray(size: Int): Array<CardHeader?> {
            return arrayOfNulls(size)
        }
    }
}