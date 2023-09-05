package com.jhteck.icebox.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 * 获取上下文
 */
public class ContextUtils {
    private static Context applicationContext = null;

    public static Context getApplicationContext() {
        if (null != applicationContext) {
            return applicationContext;
        }

        final Object activityThread = getActivityThread();
        if (null != activityThread) {
            try {
                final Method getApplication = activityThread.getClass().getDeclaredMethod("getApplication");
                getApplication.setAccessible(true);
                applicationContext = (Context) getApplication.invoke(activityThread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applicationContext;
    }

    private static Object getActivityThread() {
        try {
            final Class<?> clz = Class.forName("android.app.ActivityThread");
            final Method method = clz.getDeclaredMethod("currentActivityThread");
            method.setAccessible(true);
            final Object activityThread = method.invoke(null);
            return activityThread;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
