package cn.fcraft.zyyy.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.Date

@Entity
data class DiagnoseRecord(
    @Id
    var id: Long = 0,
    var status: Boolean = false,
    var result: String = "Waiting...",
    var uploadTime: Date? = null,
    var imageLocalUri: String? = "",
    var imageRemoteUrl: String? = "",
)