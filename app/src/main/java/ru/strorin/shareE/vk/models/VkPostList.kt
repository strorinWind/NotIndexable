package ru.strorin.shareE.vk.models

import ru.strorin.shareE.vk.models.VkPost

data class VkPostList(
    val count: Int,
    val items: List<VkPost>
)