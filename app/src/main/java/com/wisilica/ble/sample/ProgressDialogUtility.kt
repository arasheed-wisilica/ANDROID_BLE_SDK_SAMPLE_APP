package com.wisilica.ble.sample

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class ProgressDialogUtility {
    var dialog: Dialog? = null
    var textView: TextView? = null;
    fun show(ctx: Activity, msg: String) {

        val llPadding = 30
        val ll = LinearLayout(ctx)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(ctx)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        textView = TextView(ctx)
        textView?.text = msg
        textView?.setTextColor(Color.parseColor("#000000"))
        textView?.textSize = 20f
        textView?.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(textView)

        val builder = AlertDialog.Builder(ctx)
        builder.setCancelable(true)
        builder.setView(ll)

        dialog = builder.create()
        dialog?.show()
        val window = dialog?.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog?.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog?.window!!.attributes = layoutParams
        }
    }

    fun setMessage(msg: String) {
        if (textView != null) {
            textView?.text = msg
        }
    }

    fun dismiss() {
        if (dialog != null) {
            dialog?.dismiss()
        }
    }
}