package cn.fcraft.zyyy.api

import android.util.Log
import cn.hutool.core.date.DateTime
import cn.hutool.json.JSONUtil
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Date

class AccessToken {

    companion object{

        private val API_KEY = "wVcGaxAsm6Lf69vy3I17ST3F"

        private val SECRET_KEY = "llr8Gjc9ULFqt7YnqiClIrllN4wijdGR"

        fun getAccessToken(): String {
            val kv: MMKV = MMKV.defaultMMKV()
            if (!kv.containsKey("access_token") || !kv.containsKey("access_token_expires")) {
                queryAccessToken()
            } else if (kv.getLong("access_token_expires", 0) <= Date().time) {
                queryAccessToken()
            }
            return kv.getString("access_token", "error")!!
        }

        private fun queryAccessToken(): Unit {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=$API_KEY&client_secret=$SECRET_KEY")
                .post("".toRequestBody())
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return

                val result: String = response.body!!.string()//body.string()只能调用一次
                JSONUtil.parseObj(result).apply {
                    val kv: MMKV = MMKV.defaultMMKV()
                    if (this.containsKey("access_token") && this.containsKey("expires_in")) {
                        kv.putLong("access_token_expires", DateTime.now().time + this.getLong("expires_in") * 1000 - 3600000)//预留一小时的
                        kv.putString("access_token", this.getStr("access_token"))
                    } else {
                        kv.putLong("access_token_expires", DateTime.now().time)
                        kv.putString("access_token", "error")
                    }
                }
            }
        }
    }

}