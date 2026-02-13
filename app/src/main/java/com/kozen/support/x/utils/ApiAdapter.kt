package com.kozen.support.x.utils

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
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
            val attrs = intArrayOf(R.attr.selectableItemBackground)
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
        // 此处 position + 1 是当前显示列表的序号
        holder.tv.text = "${position + 1}. ${method.name}"
        holder.itemView.setOnClickListener { onClick(method) }
    }

    override fun getItemCount(): Int = displayList.size


    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val filterPattern = query.lowercase().trim()
        displayList = if (filterPattern.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(filterPattern)
            }.toMutableList()
        }
        // 通知观察者刷新 UI
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Method = displayList[position]
}