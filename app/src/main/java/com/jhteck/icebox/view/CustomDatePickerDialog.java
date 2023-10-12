package com.jhteck.icebox.view;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/10/12 9:01
 */
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CustomDatePickerDialog extends DatePickerDialog {

    private static final int DEFAULT_WIDTH = 600; // 默认宽度
    private static final int DEFAULT_HEIGHT = 800; // 默认高度
    public CustomDatePickerDialog(Context context) {
        super(context);
    }
    public CustomDatePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public CustomDatePickerDialog(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public CustomDatePickerDialog(Context context, int themeResId, OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, themeResId, listener, year, month, dayOfMonth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(DEFAULT_WIDTH, DEFAULT_HEIGHT); // 设置对话框的宽度和高度
    }
}
