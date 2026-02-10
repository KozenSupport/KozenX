package com.kozen.support.x.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kozen.support.x.R

class InputDialogFragment : DialogFragment() {

    private lateinit var etInput: EditText
    private var isDialogDismissing = false

    // 回调接口
    interface OnInputListener {
        fun onInputReceived(input: String)
        fun onDialogCancelled()
    }

    private var listener: OnInputListener? = null

    companion object {
        fun newInstance(
            title: String = "请输入",
            hint: String = "",
            defaultText: String = ""
        ): InputDialogFragment {
            return InputDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("hint", hint)
                    putString("defaultText", defaultText)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_input, null)

        // 初始化视图
        initViews(view)

        // 获取参数
        val title = arguments?.getString("title") ?: "请输入"
        val hint = arguments?.getString("hint") ?: ""
        val defaultText = arguments?.getString("defaultText") ?: ""

        // 设置UI
        view.findViewById<TextView>(R.id.tv_title).text = title
        etInput.hint = hint
        etInput.setText(defaultText)

        // 创建对话框
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun initViews(view: View) {
        etInput = view.findViewById(R.id.et_input)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        btnConfirm.setOnClickListener {
            val inputText = etInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                listener?.onInputReceived(inputText)
                safeDismiss()
            } else {
                etInput.error = "请输入内容"
                etInput.requestFocus()
            }
        }

        btnCancel.setOnClickListener {
            listener?.onDialogCancelled()
            safeDismiss()
        }
    }

    private fun safeDismiss() {
        if (isDialogDismissing) {
            return // 防止重复调用
        }

        isDialogDismissing = true

        try {
            // 先尝试正常关闭
            if (isAdded && dialog?.isShowing == true) {
                dismissAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isDialogDismissing = false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 重置标志
        isDialogDismissing = false
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        safeDismiss()
    }

    fun setOnInputListener(listener: OnInputListener) {
        this.listener = listener
    }
}