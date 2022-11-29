package cn.fcraft.zyyy.api

import android.net.Uri
import android.util.Base64
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import cn.fcraft.zyyy.entity.XrayResult
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
        suspend fun detectXray(uri: Uri): Result<XrayResult> {
            return withContext(Dispatchers.IO) {

                val json = JSONUtil.createObj()
                json.set("image", Base64.encodeToString(UriUtils.uri2Bytes(uri), Base64.DEFAULT))
                json.set("top_num", 4)

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/classification/detect_covid?access_token=${AccessToken.getAccessToken()}")
                    .header("Content-Type", "application/json")
                    .post(json.toString().toRequestBody())
                    .build()
                //var resultStr = ""
                var resultObj = XrayResult();
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext Result.success(XrayResult(0, mutableListOf()))
                    val res = JSONUtil.parseObj(response.body!!.string())
                    if (res.containsKey("error_msg")) {
                        Log.w("detect_xray", "${res["error_code"]}: ${res["error_msg"]}")
                    } else {
                        //正确返回结果
                        val resultArray = res.getJSONArray("results")
                        resultObj.cnt = resultArray.size
                        for (each in resultArray.jsonIter()) {
                            //resultStr += "${each["name"]}: ${each["score"]}\n"
                            resultObj.list.add(XrayResult.ResultItem(each.getStr("name"), each.getDouble("score")))
                        }
                    }
                }
                Result.success(resultObj)
            }
        }
        fun findBestResult(result: XrayResult): String {
            result.run {
                if (cnt < 4) return "error"
                if (list[0].score >= 0.5) return list[0].name
                if (list[0].score - list[1].score < 0.05) return "error"
            }
            return "error"
        }
    }
}