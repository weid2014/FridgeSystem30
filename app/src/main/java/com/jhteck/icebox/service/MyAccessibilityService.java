package com.jhteck.icebox.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/9/15 18:21
 */
public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理屏幕操作事件
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d("icebox", "onAccessibilityEvent: TYPE_VIEW_CLICKED");
                // 处理点击事件
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                // 处理滑动事件
                Log.d("icebox", "onAccessibilityEvent: TYPE_VIEW_SCROLLED");
                break;
            // 其他事件类型...
        }
    }

    @Override
    public void onInterrupt() {
        // 中断服务时的处理逻辑
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 连接服务时的处理逻辑
    }
}
