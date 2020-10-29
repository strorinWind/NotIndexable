package ru.strorin.shareE.feature_groups.ui

import ru.strorin.shareE.feature_groups.VkGroupUi

interface GroupListView {

    fun setLoading(loading: Boolean)

    fun setGroupsList(list: List<VkGroupUi>)

    fun updateUnsubscribeVisibility()
}