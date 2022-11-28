package cn.fcraft.zyyy.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.fcraft.zyyy.ObjectBox
import cn.fcraft.zyyy.api.DetectApi
import cn.fcraft.zyyy.databinding.FragmentDetectPageBinding
import cn.fcraft.zyyy.entity.DiagnoseRecord
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
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
                binding.progress.visibility = View.VISIBLE
                fileUri = it.toString()
                //binding.tvFile.text = fileUri
                ObjectBox.recordBox.put(DiagnoseRecord(uploadTime = Date(), imageLocalUri = fileUri))
                RecordListFragment.refreshList()

                lifecycleScope.launch {
                    val result = DetectApi.detectXray(it)
                    //val result = submitImage()
                    binding.progress.visibility = View.GONE
                    binding.tvFile.text = result.getOrNull()
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
            docProviderLauncher.launch("image/*")
        }
    }

    private suspend fun submitImage(): Result<String> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build()
            var result: String
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) Result.success("failed")

                for ((name, value) in response.headers) {
                    Log.d("header", "$name: $value")
                }
                result = response.body!!.string()//body.string()只能调用一次
            }
            Result.success(result)
        }
    }
}