package com.joelzhu.floatwindow;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for window's transparent.
 *
 * The value range is from {@link #MIN} to {@link #MAX}.
 */
@FloatRange(from = WindowTransparent.MIN, to = WindowTransparent.MAX)
@Retention(RetentionPolicy.SOURCE)
public @interface WindowTransparent {
    float MIN = 0f;
    float MAX = 1f;
}