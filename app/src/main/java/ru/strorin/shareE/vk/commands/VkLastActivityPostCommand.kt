package ru.strorin.shareE.vk.commands

import android.util.Log
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject


class VkLastActivityPostCommand(private val groupIds: List<Int>): ApiCommand<List<Long>>()  {
    override fun onExecute(manager: VKApiManager): List<Long> {

        val chunks = groupIds.chunked(CHUNK_LIMIT)

        val code = """
var grous = ([%s]);

var res = "";
var r = ([]);
var i = 0;
while(i < grous.length) {
    var a = API.wall.get({"owner_id": -grous[i], "count": 2});
    i = i + 1;
    var b1  = a.items[1].date;
    var b2 = a.items[2].date;
    if (b1 >= b2) {
        res = res + b1 +",";
        r = r + [b1];
    } else {
        res = res + b2 +",";
        r = r + [b2];
    }
};
return r;
        """

        val result = ArrayList<Long>()
        for (chunk in chunks) {
            val call = VKMethodCall.Builder()
                .method("execute")
                .args("code", String.format(code, chunk.joinToString(",")))
                .version(manager.config.version)
                .build()
            result.addAll(manager.execute(call, ResponseApiParser()))
        }
        return result
    }

    companion object {
        const val CHUNK_LIMIT = 24
    }

    private class ResponseApiParser : VKApiResponseParser<ArrayList<Long>> {
        override fun parse(response: String): ArrayList<Long> {
            try {
                val result = ArrayList<Long>()
                val r =  JSONObject(response).getJSONArray("response")
                for (i in 0 until r.length()) {
                    result.add(r.optLong(i))
                }
                return result
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}