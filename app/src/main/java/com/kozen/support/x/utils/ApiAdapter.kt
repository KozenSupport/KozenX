package com.test.sdkproject

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Method
import androidx.core.content.withStyledAttributes

class ApiAdapter(
    private val originalList: List<Method>,
    private val onClick: (Method) -> Unit
) : RecyclerView.Adapter<ApiAdapter.VH>() {



    // 使用副本进行展示
    private var displayList: MutableList<Method> = originalList.toMutableList()

    class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val context = parent.context
        val tv = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(50, 40, 50, 40)
            textSize = 16f

            // 动态获取系统点击波纹效果 (SelectableItemBackground)
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            context.withStyledAttributes(null, attrs) {
                val backgroundResource = getResourceId(0, 0)
                setBackgroundResource(backgroundResource)
            }

            isClickable = true
            isFocusable = true
        }


        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val method = displayList[position]
        // 注意：此处 position + 1 是当前显示列表的序号
        holder.tv.text = "${position + 1}. ${method.name}"
        holder.itemView.setOnClickListener { onClick(method) }
    }

    override fun getItemCount(): Int = displayList.size

    /**
     * 公开搜索过滤方法
     */
    fun filter(query: String) {
        val filterPattern = query.lowercase().trim()
        displayList = if (filterPattern.isEmpty()) {
            originalList.toMutableList()
        } else {
            // 基于原始总表进行过滤
            originalList.filter {
                it.name.lowercase().contains(filterPattern)
            }.toMutableList()
        }
        // 关键：通知观察者刷新 UI
        notifyDataSetChanged()
    }

    /**
     * 如果需要获取当前展示的数据对象（可选）
     */
    fun getItem(position: Int): Method = displayList[position]
}