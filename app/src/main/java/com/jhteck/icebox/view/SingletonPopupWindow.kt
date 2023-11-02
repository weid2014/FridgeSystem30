package com.jhteck.icebox.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.PopupWindow

class SingletonPopupWindow private constructor(context:Context){
    companion object{
        private var instance: PopupWindow? = null

        fun getInstance(context: Context): PopupWindow? {
            if (instance == null) {
                instance = PopupWindow(context)
                // 在这里配置你的 PopupWindow
                // 例如：instance?.width = 200
                instance?.width = ViewGroup.LayoutParams.WRAP_CONTENT
                instance?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                instance?.setOnDismissListener { null }
                instance?.isTouchable=true
                instance?.isFocusable = false
                instance?.isOutsideTouchable = false
                instance?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            }
            return instance
        }
    }
}