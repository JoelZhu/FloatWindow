package com.joelzhu.floatwindow;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for first moving direction, only will worked when moving direction set to
 * {@link MoveDirection#FIRST_DIRECTION}.
 *
 * The first directions are as below:
 * 1) {@link #NO_DIRECTION}: The moving direction has not been decided yet, it will be decided by the next moving event.
 * 2) {@link #FIRST_X}: The same result as set moving direction to {@link MoveDirection#X_AXIS}.
 * 3) {@link #FIRST_Y}: The same result as set moving direction to {@link MoveDirection#Y_AXIS}.
 */
@IntDef({
        FirstDirection.NO_DIRECTION,
        FirstDirection.FIRST_X,
        FirstDirection.FIRST_Y
})
@Retention(RetentionPolicy.SOURCE)
public @interface FirstDirection {
    int NO_DIRECTION = 0;
    int FIRST_X = 1;
    int FIRST_Y = 2;
}