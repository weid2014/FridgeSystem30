package com.jhteck.icebox.utils;

import android.content.Context;
import android.widget.PopupWindow;

import androidx.room.Room;

import com.jhteck.icebox.repository.AppDataBase;

import java.lang.reflect.Method;

public class DbUtil {

    private static AppDataBase db = null;
    private static Context context = ContextUtils.getApplicationContext();


    public static AppDataBase getDb() {
        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDataBase.class, "icebox-system-08").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return db;
    }

    public static void setContext(Context context) {
        DbUtil.context = context;
    }


    public static void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
