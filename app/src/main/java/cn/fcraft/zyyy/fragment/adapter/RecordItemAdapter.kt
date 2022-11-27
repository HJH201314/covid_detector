package cn.fcraft.zyyy.fragment.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cn.fcraft.zyyy.ObjectBox
import cn.fcraft.zyyy.databinding.RecordItemBinding
import cn.fcraft.zyyy.entity.DiagnoseRecord

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 */
class RecordItemAdapter() : ListAdapter<DiagnoseRecord, RecordItemAdapter.ViewHolder>(
    diffCallback
) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<DiagnoseRecord>() {
            override fun areItemsTheSame(oldItem: DiagnoseRecord, newItem: DiagnoseRecord): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: DiagnoseRecord, newItem: DiagnoseRecord): Boolean {
                //如果新数据和旧数据的名称和年龄相同,则视为两个item的内容相同
                return oldItem.uploadTime.toString() == newItem.uploadTime.toString();
            }
        }
    }

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            RecordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvDiagnoseType.text = item.result
        holder.tvUploadTime.text = item.uploadTime.toString()
        holder.card.setOnLongClickListener() {
            AlertDialog.Builder(context).apply {
                setTitle("请选择操作")
                setItems(arrayOf("删除记录")) { _, menu_pos ->
                    when(menu_pos) {
                        0 -> {
                            ObjectBox.recordBox.remove(item.id)
                            this@RecordItemAdapter.submitList(ObjectBox.recordBox.all)
                        }
                    }
                }
                show()
            }
            true
        }
    }

    inner class ViewHolder(binding: RecordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDiagnoseType: TextView = binding.tvDiagnoseType
        val tvUploadTime: TextView = binding.tvUploadTime
        val card: CardView = binding.card

        override fun toString(): String {
            return super.toString() + " ${tvUploadTime.text}:${tvDiagnoseType.text}"
        }
    }

}