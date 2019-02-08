package com.joelzhu.floatwindow;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for moving direction.
 *
 * The moving direction types are as below:
 * 1) {@link #X_AXIS}: Only work on the delta value on X-Axis, ignore the moving delta value on Y-Axis.
 * 2) {@link #Y_AXIS}: Only work on the delta value on Y-Axis, ignore the moving delta value on X-Axis.
 * 3) {@link #FIRST_DIRECTION}: Can move towards both two axes(X-Axis and Y-Axis), but only one axis worked, it was
 * decided by the axis when moving at the first time. But the specified axis is temporary, it can be changed at the
 * next moving event.
 * 4) {@link #ANY_DIRECTION}: No more special conditions, it can be moved towards any where in the screen.
 */
@IntDef({
        MoveDirection.X_AXIS,
        MoveDirection.Y_AXIS,
        MoveDirection.FIRST_DIRECTION,
        MoveDirection.ANY_DIRECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface MoveDirection {
    int X_AXIS = 1;
    int Y_AXIS = 2;
    int FIRST_DIRECTION = 3;
    int ANY_DIRECTION = 9;
}