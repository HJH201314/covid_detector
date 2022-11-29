package cn.fcraft.zyyy.entity

data class XrayResult(
    var cnt: Int = 0,
    var list: MutableList<ResultItem> = mutableListOf()
) {
    data class ResultItem(var name: String, var score: Double)
}