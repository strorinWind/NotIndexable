package ru.strorin.shareE.ui.groups

interface GroupListView {

    fun setLoading(loading: Boolean)

    fun setGroupsList(list: List<VkGroupUi>)

    fun updateUnsubscribeVisibility()
}