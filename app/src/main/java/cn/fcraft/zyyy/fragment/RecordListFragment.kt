package cn.fcraft.zyyy.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.fcraft.zyyy.ObjectBox
import cn.fcraft.zyyy.R
import cn.fcraft.zyyy.fragment.adapter.RecordItemAdapter
import com.blankj.utilcode.util.FragmentUtils

class RecordListFragment : Fragment() {

    private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.record_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = listAdapter
            }
        }
        refreshList()
        return view
    }

    companion object {
        val listAdapter = RecordItemAdapter()
        fun refreshList() {
            listAdapter.submitList(ObjectBox.recordBox.all)
        }
    }
}