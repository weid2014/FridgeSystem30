package com.jhteck.icebox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.hele.mrd.app.lib.base.BaseApp;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import es.dmoral.toasty.Toasty;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/8/8 23:34
 */
public final class ToastUtils {
    private static final String TAG = "ToastUtil";
    private static Toast mToast;
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private static boolean sIsHookFieldInit = false;
    private static final String FIELD_NAME_TN = "mTN";
    private static final String FIELD_NAME_HANDLER = "mHandler";

    private static void showToast(final Context context, final CharSequence text,
                                  final int duration, final boolean isShowCenterFlag) {
        ToastRunnable toastRunnable = new ToastRunnable(context, text, duration, isShowCenterFlag);
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                activity.runOnUiThread(toastRunnable);
            }
        } else {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(toastRunnable);
        }
    }

    public static void shortToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT, false);
    }

    public static void longToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG, false);
    }

    public static void shortToast(String msg) {
        showToast(BaseApp.app, msg, Toast.LENGTH_SHORT, false);
    }

    public static void shortToast(@StringRes int resId) {
        showToast(BaseApp.app, BaseApp.app.getText(resId),
                Toast.LENGTH_SHORT, false);
    }

    public static void centerShortToast(@NonNull String msg) {
        showToast(BaseApp.app, msg, Toast.LENGTH_SHORT, true);
    }

    public static void centerShortToast(@StringRes int resId) {
        showToast(BaseApp.app, BaseApp.app.getText(resId),
                Toast.LENGTH_SHORT, true);
    }

    public static void cancelToast() {
        Looper looper = Looper.getMainLooper();
        if (looper.getThread() == Thread.currentThread()) {
            mToast.cancel();
        } else {
            new Handler(looper).post(() -> mToast.cancel());
        }
    }

    private static void hookToast(Toast toast) {
        try {
            if (!sIsHookFieldInit) {
                sField_TN = Toast.class.getDeclaredField(FIELD_NAME_TN);
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField(FIELD_NAME_HANDLER);
                sField_TN_Handler.setAccessible(true);
                sIsHookFieldInit = true;
            }
            Object tn = sField_TN.get(toast);
            Handler originHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWrapper(originHandler));
        } catch (Exception e) {
            Log.e(TAG, "Hook toast exception=" + e);
        }
    }

    private static class ToastRunnable implements Runnable {
        private Context context;
        private CharSequence text;
        private int duration;
        private boolean isShowCenter;

        public ToastRunnable(Context context, CharSequence text, int duration, boolean isShowCenter) {
            this.context = context;
            this.text = text;
            this.duration = duration;
            this.isShowCenter = isShowCenter;
        }

        @Override
        @SuppressLint("ShowToast")
        public void run() {
            mToast = Toasty.info(context, text);
            /*if (mToast == null) {
//                mToast = Toast.makeText(context, text, duration);
                mToast = Toasty.info(context, text);
            } else {
                mToast.setText(text);
                if (isShowCenter) {
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                }
                mToast.setDuration(duration);
            }*/
            hookToast(mToast);
//            mToast.show();
        }
    }

    private static class SafelyHandlerWrapper extends Handler {
        private Handler originHandler;

        public SafelyHandlerWrapper(Handler originHandler) {
            this.originHandler = originHandler;
        }

        @Override
        public void dispatchMessage(@NotNull Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                Log.e(TAG, "Catch system toast exception:" + e);
            }
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            if (originHandler != null) {
                originHandler.handleMessage(msg);
            }
        }
    }
}
