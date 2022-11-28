package cn.fcraft.zyyy.api

import android.net.Uri
import android.util.Base64
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import cn.hutool.json.JSONUtil
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.UriUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class DetectApi {
    companion object {
        suspend fun detectXray(uri: Uri): Result<String> {
            return withContext(Dispatchers.IO) {

                val json = JSONUtil.createObj()
                json.set("image", Base64.encodeToString(UriUtils.uri2Bytes(uri), Base64.DEFAULT))
                json.set("top_num", 5)

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/classification/detect_covid?access_token=${AccessToken.getAccessToken()}")
                    .header("Content-Type", "application/json")
                    .post(json.toString().toRequestBody())
                    .build()
                var resultStr = ""
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext Result.success("failed")
                    val res = JSONUtil.parseObj(response.body!!.string())
                    if (res.containsKey("error_msg")) {
                        Log.w("detect_xray", "${res["error_code"]}: ${res["error_msg"]}")
                    } else {
                        //正确返回结果
                        val ress = res.getJSONArray("results")
                        for (each in ress.jsonIter()) {
                            resultStr += "${each["name"]}: ${each["score"]}\n"
                        }
                    }
                }
                Result.success(resultStr)
            }
        }
    }
}