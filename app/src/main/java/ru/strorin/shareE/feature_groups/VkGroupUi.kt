package ru.strorin.shareE.feature_groups

import ru.strorin.shareE.vk.models.BaseVkGroup
import ru.strorin.shareE.vk.models.VkGroup
import java.io.Serializable

data class VkGroupUi(
    override val id: Int = 0,
    override val name: String = "",
    override val screen_name: String = "",
    override val is_admin: Boolean = false,
    override val photo_100: String = "",
    override val description: String = "",
    override val members_count: Int = 0,
    var selected: Boolean,
    var last_activity: Long = 0
): BaseVkGroup(
    id,
    name,
    screen_name,
    is_admin = is_admin,
    photo_100 = photo_100,
    description = description,
    members_count = members_count
), Serializable {

    companion object {
        fun from(group: VkGroup): VkGroupUi {
            return VkGroupUi(
                group.id,
                group.name,
                group.screen_name,
                group.is_admin,
                group.photo_100,
                group.description,
                group.members_count,
                false
            )
        }
    }
}