package com.jhteck.icebox.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

/**
 * @author wade
 * @Description:(计算分辨率 px和dp的关系)
 * @date 2023/7/1 19:25
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void backgroundAlpha(Activity context, float bgAlpha) {
        if (context != null) {
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.alpha = bgAlpha;
            if (bgAlpha == 1f) {
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//划重点 ==>> 不移除该Flag的话,可能出现黑屏的bug
            } else {
                context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
            }
            context.getWindow().setAttributes(lp);
        }
    }

}
