package com.hele.mrd.app.lib.common.ext

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

fun Context.toast(content: String?){
    val mytoast=Toast.makeText(this,content ?: return,Toast.LENGTH_SHORT)
//    mytoast.setGravity(Gravity.CENTER , 0, 0);  //设置显示位置
    var textView = mytoast.view?.findViewById<TextView>(android.R.id.message);
    textView?.setTextSize(22f)
//    textView?.setTextColor(Color.YELLOW);
    mytoast.show()
//    v.setTextColor(Color.YELLOW);     //设置字体颜色
}

