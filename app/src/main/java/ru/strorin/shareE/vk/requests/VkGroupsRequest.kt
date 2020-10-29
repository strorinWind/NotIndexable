package ru.strorin.shareE.vk.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.strorin.shareE.vk.models.VkGroup
import ru.strorin.shareE.vk.models.VkGroupList

class VkGroupsRequest: VKRequest<VkGroupList> {

    constructor(): super("groups.get") {
        addParam("extended", 1)
        addParam("count", 1000)
        addParam("fields", "description,members_count")
        addParam("offset", 14)
    }

    override fun parse(r: JSONObject): VkGroupList {
        val response = r.getJSONObject("response")
        val count = response.getInt("count")
        val groups = response.getJSONArray("items")

        val groupList = ArrayList<VkGroup>()

        for (i in 0 until groups.length()) {
            groupList.add(VkGroup.parse(groups.getJSONObject(i)))
        }

        return VkGroupList(
            count,
            groupList
        )
    }
}