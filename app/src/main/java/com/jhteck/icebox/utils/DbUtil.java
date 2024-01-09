package com.jhteck.icebox.utils;

import android.content.Context;
import android.widget.PopupWindow;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jhteck.icebox.repository.AppDataBase;

import java.lang.reflect.Method;

public class DbUtil {

    private static AppDataBase db = null;
    private static Context context = ContextUtils.getApplicationContext();


    public static AppDataBase getDb() {
        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDataBase.class, "icebox-system-08")
//                    .addMigrations(MIGRATION_34_35)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
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

    static final Migration MIGRATION_34_35 = new Migration(34, 35) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 在这里执行数据库的更新操作，例如添加新的列
            database.execSQL("ALTER TABLE 't_avail_rfid' ADD COLUMN 'last_fridge_first_sync_at' TEXT");
            database.execSQL("ALTER TABLE 't_fridges_info' ADD COLUMN 'door_style' INTEGER NOT NULL DEFAULT 0");
        }
    };

}
