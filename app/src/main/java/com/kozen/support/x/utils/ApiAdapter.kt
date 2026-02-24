package com.kozen.support.x.utils

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Method
import androidx.core.content.withStyledAttributes
import com.kozen.support.x.utils.CommonTools.setTextWithColors

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
        val methodClass= getMethodClass(method.declaringClass.toString())
        // 此处 position + 1 是当前显示列表的序号
//        holder.tv.text = "${position + 1}. $methodClass -- ${method.name}"
        holder.tv.setTextWithColors(
            "${position + 1}. " to Color.BLACK,
            "$methodClass " to Color.BLACK,
            "\n --> ${method.name}" to Color.BLUE
        )
        holder.itemView.setOnClickListener { onClick(method) }
    }

    fun getMethodClass(declaringClass : String) : String{

        return declaringClass.substringAfterLast('.').trim()
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