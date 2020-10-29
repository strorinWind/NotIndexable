package ru.strorin.shareE.vk.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VkPost(
    val id: Int = 0,
    val date: Long = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(date)
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VkPost> {
        override fun createFromParcel(parcel: Parcel): VkPost {
            return VkPost(parcel)
        }

        override fun newArray(size: Int): Array<VkPost?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject)
                = VkPost(
            id = json.optInt("id", 0),
            date = json.optLong("date", 0))
    }
}