package cn.fcraft.zyyy

import android.app.Application
import android.content.Context
import cn.fcraft.zyyy.entity.DiagnoseRecord
import cn.fcraft.zyyy.entity.MyObjectBox
import com.tencent.mmkv.MMKV
import io.objectbox.Box
import io.objectbox.BoxStore

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
        MMKV.initialize(this)
    }
}

object ObjectBox {
    lateinit var store: BoxStore
        private set
    lateinit var recordBox: Box<DiagnoseRecord>
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
        recordBox = store.boxFor(DiagnoseRecord::class.java)
    }
}