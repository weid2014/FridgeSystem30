package com.jhteck.icebox.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jhteck.icebox.R;

/**
 * @author wade
 * @Description:(自定义dialog)
 * @date 2023/7/12 0:30
 */

public class CustomDialogMain extends Dialog implements View.OnClickListener {

    private TextView mTitle, mMessage, mConfirm, mCancel;
    private String sTitle, sMessage, sConfirm, sCancel;
    private View.OnClickListener cancelListener, confirmListener;

    public CustomDialogMain setsTitle(String sTitle) {
        this.sTitle = sTitle;
        return this;
    }

    public CustomDialogMain setsMessage(String sMessage) {
        this.sMessage = sMessage;
        return this;
    }

    public CustomDialogMain setsConfirm(String sConfirm, View.OnClickListener listener) {
        this.sConfirm = sConfirm;
        this.confirmListener = listener;
        return this;
    }

    public CustomDialogMain setsCancel(String sCancel, View.OnClickListener listener) {
        this.sCancel = sCancel;
        this.cancelListener = listener;
        return this;
    }


    public CustomDialogMain(@NonNull Context context) {
        super(context);
    }

    public CustomDialogMain(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.layout_custom_dialog_main);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        //自定义Dialog宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) ((size.x) * 0.5);        //设置为屏幕的0.7倍宽度
        getWindow().setAttributes(p);


        mTitle = findViewById(R.id.title);
        mMessage = findViewById(R.id.message);
        mCancel = findViewById(R.id.cancel);
        mConfirm = findViewById(R.id.confirm);

        if (!TextUtils.isEmpty(sTitle)) {
            mTitle.setText(sTitle);
        }
        if (!TextUtils.isEmpty(sMessage)) {
            mMessage.setText(sMessage);
        }
        if (!TextUtils.isEmpty(sCancel)) {
            mCancel.setText(sCancel);
        }
        if (!TextUtils.isEmpty(sConfirm)) {
            mConfirm.setText(sConfirm);
        }

        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                if (confirmListener != null) {
                    confirmListener.onClick(view);
                }
                break;
            case R.id.cancel:
                if (cancelListener != null) {
                    cancelListener.onClick(view);
                }
                break;
        }
    }

}
