package ru.strorin.shareE.vk.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VkLeaveGroupRequest: VKRequest<Int> {

    constructor(id: Int): super("groups.leave") {
        addParam("group_id", id)
    }

    override fun parse(r: JSONObject): Int {
        return r.getInt("response")
    }
}