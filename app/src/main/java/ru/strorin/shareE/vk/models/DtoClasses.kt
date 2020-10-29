package ru.strorin.shareE.vk.models

enum class GroupType{
    group,
    page,
    event
}

open class BaseVkGroup(
    open val id: Int = 0,
    open val name: String = "",
    open val screen_name: String = "",
    open val is_closed: Boolean = false,
    open val type: String = "", ///TODO
    open val is_admin: Boolean = false,
    open val is_member: Boolean = false,
    open val is_advertiser: Boolean = false,
    open val photo_100: String = "",
    open val description: String = "",
    open val members_count: Int = 0
)