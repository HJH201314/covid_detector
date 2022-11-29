package cn.fcraft.zyyy.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.fcraft.zyyy.ObjectBox
import cn.fcraft.zyyy.R
import cn.fcraft.zyyy.api.DetectApi
import cn.fcraft.zyyy.databinding.FragmentDetectPageBinding
import cn.fcraft.zyyy.entity.DiagnoseRecord
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ResourceUtils
import kotlinx.coroutines.*
import java.lang.Double
import java.util.*


class DetectPageFragment : Fragment() {

    private lateinit var binding: FragmentDetectPageBinding
    private lateinit var fileUri: String
    private lateinit var docProviderLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //根据要求,registerForActivityResult需要和.launch解耦,不能在onCreate之后register
        docProviderLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                binding.btnChoose.isEnabled = false
                binding.progress.visibility = View.VISIBLE
                fileUri = it.toString()
                var recordObj = DiagnoseRecord(uploadTime = Date(), imageLocalUri = fileUri)
                val id = ObjectBox.recordBox.put(recordObj)

                lifecycleScope.launch {
                    //请求检测
                    val result = DetectApi.detectXray(it).getOrNull() ?: return@launch
                    val resultStrRes = when(DetectApi.findBestResult(result)) {
                        "normal" -> R.string.type_normal
                        "pneumonia" -> R.string.type_viral
                        "covid" -> R.string.type_covid19
                        "opacity" -> R.string.type_opacity
                        "[default]" -> R.string.type_default
                        else -> R.string.type_default
                    }
                    //检测完毕后
                    binding.progress.visibility = View.GONE//移除加载条
                    this@DetectPageFragment.context?.let { context ->
                        AlertDialog.Builder(context).apply {
                            setTitle(R.string.dialog_title_success)
                            //setMessage(resources.getString(R.string.dialog_subtitle_success) + "\n" + result.getOrNull())
                            //setView(R.layout.dialog_success)
                            val view = layoutInflater.inflate(R.layout.dialog_success, null, false).apply {
                                findViewById<TextView>(R.id.tv_dialog_success_type).apply {
                                    setText(resultStrRes)
                                    //使用ContextCompat是因为resources.getColor弃用
                                    setTextColor(ContextCompat.getColor(context, when(resultStrRes) {
                                        R.string.type_normal -> R.color.ant_green
                                        R.string.type_viral -> R.color.ant_orange
                                        R.string.type_opacity -> R.color.ant_orange
                                        R.string.type_covid19 -> R.color.ant_red
                                        R.string.type_default -> R.color.ant_blue
                                        else -> R.color.ant_blue
                                    }))
                                }
                                findViewById<LinearLayout>(R.id.tv_dialog_success_list).apply {
                                    for (item in result.list) {
                                        this.addView(
                                            layoutInflater.inflate(R.layout.dialog_success_score, this, false).apply {
                                                val percent = String.format("%.2f", item.score * 100)
                                                this.findViewById<ProgressBar>(R.id.progress).progress =
                                                    (Double.parseDouble(percent) * 100).toInt()
                                                this.findViewById<TextView>(R.id.tv_score).setText(percent + "%")
                                                this.findViewById<TextView>(R.id.tv_name).text = item.name
                                        })
                                    }
                                }
                                setPadding(
                                    ConvertUtils.dp2px(24F),
                                    ConvertUtils.dp2px(16F),
                                    ConvertUtils.dp2px(24F),
                                    0
                                )
                            }
                            setView(view)
                            setPositiveButton("OK",
                                DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                            show()
                        }
                        recordObj.apply {
                            this.status = true
                            this.result = resources.getString(resultStrRes)
                            ObjectBox.recordBox.put(this)
                        }
                        RecordListFragment.refreshList()
                    }
                    binding.btnChoose.isEnabled = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectPageBinding.inflate(layoutInflater, container, false)
        initializeView()
        //return inflater.inflate(R.layout.fragment_detect_page, container, false)
        return binding.root
    }

    private fun initializeView() {
        binding.btnChoose.setOnClickListener {
            binding.tvFile.text = ""
            docProviderLauncher.launch("image/*")
        }
    }
}