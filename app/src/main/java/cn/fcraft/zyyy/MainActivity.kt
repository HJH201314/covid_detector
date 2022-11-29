package cn.fcraft.zyyy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import cn.fcraft.zyyy.databinding.ActivityMainBinding
import cn.fcraft.zyyy.enum.DetectMethod
import cn.fcraft.zyyy.fragment.RecordListFragment
import cn.fcraft.zyyy.fragment.adapter.MainFragmentAdapter
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var detectMethod: DetectMethod = DetectMethod.Xray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.apply {
            setSupportActionBar(this)
            //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        //vp负责控制fragment，MainFragmentAdapter继承自FragmentStateAdapter
        binding.vp.adapter = MainFragmentAdapter(this)
        //绑定vp和tab_layout,会覆盖xml中的tab_layout
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab: TabLayout.Tab, i: Int ->
            val nameList = listOf(R.string.main_tabs_item_detect, R.string.main_tabs_item_record)
            val iconList = listOf(R.drawable.ic_detection, R.drawable.ic_log)
            tab.text = getString(nameList[i])
            tab.icon = AppCompatResources.getDrawable(this, iconList[i])
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(this)
        applyDetectMethod()
    }

    //Initialize menu (must)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_item_method -> {
                AlertDialog.Builder(this).apply {
                    setTitle(R.string.main_toolbar_menu_method)
                    setItems(R.array.detect_methods) { _, pos ->
                        applyDetectMethod(when(pos) {
                            0 -> DetectMethod.Xray
                            1 -> DetectMethod.CT
                            2 -> DetectMethod.Cough
                            else -> DetectMethod.Xray
                        })
                    }
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyDetectMethod(method: DetectMethod = DetectMethod.Xray) {
        detectMethod = method
        //TODO: ui change
        binding.toolbar.setSubtitle(when(detectMethod) {
            DetectMethod.Xray -> R.string.method_xray
            DetectMethod.CT -> R.string.method_ct
            DetectMethod.Cough -> R.string.method_cough
        })

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        //切换tab的时候控制method的显示
        if (tab?.position == 0) {
            applyDetectMethod()
        } else {
            binding.toolbar.subtitle = String.format(resources.getString(R.string.toolbar_subtitle_record), ObjectBox.recordBox.count())
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {
        RecordListFragment.refreshList()
    }
}
