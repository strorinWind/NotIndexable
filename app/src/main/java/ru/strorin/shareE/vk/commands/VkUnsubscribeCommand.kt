package ru.strorin.shareE.vk.commands

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VkUnsubscribeCommand(private val groupIds: IntArray): ApiCommand<List<Int>>() {
    override fun onExecute(manager: VKApiManager): List<Int> {

        val result = ArrayList<Int>()
        for (id in groupIds) {
            val call = VKMethodCall.Builder()
                .method("groups.leave")
                .args("group_id", id)
                .version(manager.config.version)
                .build()
            result.add(manager.execute(call, ResponseApiParser()))
        }
        return result
    }

    private class ResponseApiParser : VKApiResponseParser<Int> {
        override fun parse(response: String): Int {
            try {
                return JSONObject(response).getInt("response")
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}