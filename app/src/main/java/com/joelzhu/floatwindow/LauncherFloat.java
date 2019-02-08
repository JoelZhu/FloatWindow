package com.joelzhu.floatwindow;

import android.view.MotionEvent;

/**
 * Sample implemented class.
 */
public final class LauncherFloat extends BaseFloatWindow {
    @Override
    public int doOnDispatchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Popup the window from origin view.
                popupWindowFromOrigin();
                return EventResult.TRUE;

            case MotionEvent.ACTION_MOVE:
                final int currentX = getEventX(event);
                final int currentY = getEventY(event);
                moveWindow(currentX - getLastX(), currentY - getLastY());
                return EventResult.TRUE;

            case MotionEvent.ACTION_UP:
                dismissWindow();
                return EventResult.TRUE;
        }
        return EventResult.SUPER;
    }
}