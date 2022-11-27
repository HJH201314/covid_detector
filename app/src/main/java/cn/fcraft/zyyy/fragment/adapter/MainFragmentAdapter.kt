package cn.fcraft.zyyy.fragment.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.fcraft.zyyy.fragment.DetectPageFragment
import cn.fcraft.zyyy.fragment.RecordListFragment

class MainFragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val list: List<Class<out Fragment>> = listOf(DetectPageFragment::class.java, RecordListFragment::class.java)

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return list[position].newInstance()
    }

}