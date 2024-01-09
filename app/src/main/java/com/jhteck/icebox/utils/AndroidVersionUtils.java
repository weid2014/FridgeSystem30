package com.jhteck.icebox.utils;

import static com.jhteck.icebox.api.AppConstantsKt.FRIDGEID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.jhteck.icebox.repository.dao.FridgesInfoDao;
import com.jhteck.icebox.repository.entity.FridgesInfoEntity;

public class AndroidVersionUtils {

    // 获取 Android 版本号
    public static String getAndroidVersion() {
        return "Android版本:"+Build.VERSION.RELEASE;
    }

    // 获取设备的型号
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    // 获取设备的制造商
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    // 获取应用程序版本名
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    // 获取应用程序版本号
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 获取应用程序名称
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return context.getApplicationInfo().loadLabel(packageManager).toString();
    }

    // 获取冰箱信息
    public static String getFridgeInfo(Context context) {
        FridgesInfoEntity fridgesInfoEntity = DbUtil.getDb().fridgesInfoDao()
                .getById(SharedPreferencesUtils.getPrefString(context, FRIDGEID, ""));
        return fridgesInfoEntity.getDevice_alias();
    }

    // 获取串口信息
    public static String getPortInfo() {
        String nfcPortInfo="nfcPort:/dev/ttyS0,BaudRate:9600";
        String lockPortInfo="lockPort:/dev/ttyS8,BaudRate:115200";
        String rfidPortInfo="rfidPort:/dev/ttyS2,BaudRate:115200";
        String rfidPortInfoTest="串口正常";
        return nfcPortInfo+lockPortInfo+rfidPortInfo;
    }


}
