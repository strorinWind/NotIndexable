package ru.strorin.shareE.vk.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VkGroup(
    override val id: Int = 0,
    override val name: String = "",
    override val screen_name: String = "",
    override val is_closed: Boolean = false,
    override val type: String = "", ///TODO
    override val is_admin: Boolean = false,
    override val is_member: Boolean = false,
    override val is_advertiser: Boolean = false,
    override val photo_100: String = "",
    override val description: String = "",
    override val members_count: Int = 0
): BaseVkGroup(
    id,
    name,
    screen_name,
    is_closed,
    type,
    is_admin,
    is_member,
    is_advertiser,
    photo_100,
    description,
    members_count
), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(screen_name)
        parcel.writeByte(if (is_closed) 1 else 0)
        parcel.writeString(type)
        parcel.writeByte(if (is_admin) 1 else 0)
        parcel.writeByte(if (is_member) 1 else 0)
        parcel.writeByte(if (is_advertiser) 1 else 0)
        parcel.writeString(photo_100)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VkGroup> {
        override fun createFromParcel(parcel: Parcel): VkGroup {
            return VkGroup(parcel)
        }

        override fun newArray(size: Int): Array<VkGroup?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject)
                = VkGroup(id = json.optInt("id", 0),
            name = json.optString("name", ""),
            screen_name = json.optString("screen_name", ""),
            is_closed = getBoolean(json, "is_closed"),
            type = json.optString("type", ""),
            is_admin = getBoolean(json, "is_admin"),
            is_member = getBoolean(json, "is_member"),
            is_advertiser = getBoolean(json, "is_advertiser"),
            photo_100 = json.optString("photo_100", ""),
            description = json.optString("description", ""),
            members_count = json.optInt("members_count", 0))

        private fun getBoolean(j: JSONObject, name: String): Boolean {
            return j.optInt(name, 0) == 1
        }
    }
}