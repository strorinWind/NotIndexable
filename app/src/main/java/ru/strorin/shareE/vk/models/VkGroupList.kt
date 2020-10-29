package ru.strorin.shareE.vk.models

import ru.strorin.shareE.vk.models.VkGroup


data class VkGroupList(
    val count: Int,
    val items: List<VkGroup>
)
