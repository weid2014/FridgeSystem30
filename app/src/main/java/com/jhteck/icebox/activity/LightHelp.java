package com.jhteck.icebox.activity;

import android.util.Log;

/**
 * @author wade
 * @Description:(测试调用java方法)
 * @date 2023/6/29 1:54
 */
public class LightHelp {

    public static void testStatic(){

    }

    public void Test(){
        Log.d("lalala","aaa");

        KotlinHelp.testStatic.getNameStatic();
        new KotlinHelp().testKotlin();
    }
}
