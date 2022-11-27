package cn.fcraft.zyyy.fragment.item

import android.util.TimeUtils
import java.time.LocalDateTime
import kotlin.random.Random

data class RecordItem(val diagnoseType: String, val uploadTime: String) {
    companion object {
        fun randomItems(count: Int): MutableList<RecordItem> {
            val list: MutableList<RecordItem> = mutableListOf()
            repeat(count) {
                val r = Random.nextInt(0, 3)
                list.add(RecordItem(when(r) {
                    0 -> "Waiting..."
                    1 -> "COVID-19"
                    2 -> "Pneumonia"
                    else -> "None"
                }, LocalDateTime.now().toString()))
            }
            return list
        }
    }
}