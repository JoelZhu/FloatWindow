package com.joelzhu.floatwindow;

import android.support.annotation.IntDef;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for the return result of the {@link View#dispatchTouchEvent(MotionEvent)} and
 * {@link View#onTouchEvent(MotionEvent)}.
 *
 * The result types are as below:
 * 1) {@link #SUPER}: It will return super.{@link View#dispatchTouchEvent(MotionEvent)} or
 *      super.{@link View#onTouchEvent(MotionEvent)}.
 * 2) {@link #TRUE}: It will return {@link Boolean#TRUE}.
 * 3) {@link #FALSE}: It will return {@link Boolean#FALSE}.
 */
@IntDef({
        EventResult.SUPER,
        EventResult.TRUE,
        EventResult.FALSE
})
@Retention(RetentionPolicy.SOURCE)
public @interface EventResult {
    int SUPER = 0;
    int TRUE = 1;
    int FALSE = 2;
}