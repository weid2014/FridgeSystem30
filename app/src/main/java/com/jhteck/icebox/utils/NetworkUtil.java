package com.jhteck.icebox.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author wade
 * @Description:(判断网络状态)
 * @date 2023/7/21 9:55
 */
public class NetworkUtil {


    /**
     * 判断当前网络是否可用(6.0以上版本)
     * 实时
     * @param context
     * @return
     */
    public static boolean isNetSystemUsable(Context context) {
        boolean isNetUsable = false;
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities =
                    manager.getNetworkCapabilities(manager.getActiveNetwork());
            if (networkCapabilities != null) {
                isNetUsable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        return isNetUsable;
    }

    public static String netWorkState(Context context) {
        return isNetSystemUsable(context) ? "网络正常" : "网络异常";
    }
    //获取安卓app信息




    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 x次, 如果 x 次都不成功，视为该地址不可用
     * @return true是可以上网，false是不能上网
     */
    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;
    public static boolean isNetOnline() {
        // Android 4.0 之后不能在主线程中请求HTTP请求
        int counts = 0;
        boolean isNetsOnline = true;
        while (counts < 2) {
            try {
                url = new URL("https://www.baidu.com");
                con = (HttpURLConnection) url.openConnection();
                state = con.getResponseCode();
                Log.d("FragmentNet", "isNetOnline counts: " + counts + "=state: " + state);
                if (state == 200) {
                    isNetsOnline = true;
                }
                break;
            } catch (Exception ex) {
                isNetsOnline = false;
                counts++;
                Log.d("FragmentNet", "isNetOnline URL不可用，连接第 " + counts + " 次");
                continue;
            }
        }
        return isNetsOnline;

    }
}
