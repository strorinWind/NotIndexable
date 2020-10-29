package ru.strorin.shareE.vk.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.strorin.shareE.vk.models.VkPost
import ru.strorin.shareE.vk.models.VkPostList

class VkLastWallRequest:VKRequest<VkPostList> {

    constructor(id: Int): super("wall.get") {
        addParam("owner_id", -id)
        addParam("count", 2)
    }

    override fun parse(r: JSONObject): VkPostList {
        val response = r.getJSONObject("response")
        val count = response.getInt("count")
        val posts = response.getJSONArray("items")

        val postList = ArrayList<VkPost>()

        for (i in 0 until posts.length()) {
            postList.add(VkPost.parse(posts.getJSONObject(i)))
        }

        return VkPostList(
            count,
            postList
        )
    }
}