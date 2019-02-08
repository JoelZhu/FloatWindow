package com.joelzhu.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import java.lang.ref.WeakReference;

/**
 * Base class of the float window.
 *
 * We can make the trigger that pop up the window as the way we want, if we implements this class, and override the
 * method {@link #doOnDispatchEvent(MotionEvent)} and the method {@link #doOnTouchEvent(MotionEvent)}.
 */
public abstract class BaseFloatWindow {
    protected WeakReference<Context> mWeakRefContext;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    
    // Window's view.
    protected View mWindowView;
    // FloatView's parent view group.
    protected ViewGroup mParentView;
    
    // Window's moving direction.
    @MoveDirection
    protected int mMoveDirection;
    // Window's moving direction, it will work when the mMoveDirection set as FIRST_DIRECTION.
    @FirstDirection
    protected int mFirstDirection;
    // Window's layout type.
    @LayoutType
    protected int mLayoutType;
    
    // Is window showing.
    private boolean mIsWindowShowing = false;
    
    // Float window's margin left and margin right.
    protected int mFloatViewLeft;
    protected int mFloatViewTop;
    
    // Screen size.
    private int mScreenWidth;
    private int mScreenHeight;
    
    // Coordinate that the last dispatch event fired.
    private int mLastX;
    private int mLastY;
    
    /**
     * Lifecycle: When FloatWindow created.
     */
    protected void onWindowCreate() {
        this.mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        
        // Calculate the screen's size when create the window.
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        
        // Set base attribute in LayoutParams.
        this.mLayoutParams = new WindowManager.LayoutParams();
        // Make this window transparent.
        this.mLayoutParams.format = PixelFormat.RGBA_8888;
        // Make this window above all the other applications.
        this.mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }
    
    /**
     * Lifecycle: When FloatWindow destroyed.
     */
    protected void onWindowDestroy() {
        mWeakRefContext = null;
        
        mLastX = 0;
        mLastY = 0;
        
        mIsWindowShowing = false;
        
        // Reset the first direction.
        mFirstDirection = FirstDirection.NO_DIRECTION;
    }
    
    /**
     * Pop up the window from the origin view's position.
     */
    protected void popupWindowFromOrigin() {
        final int left = mFloatViewLeft + mLayoutParams.width / 2;
        final int top = mFloatViewTop + mLayoutParams.height / 2;
        popupWindow(left, top);
    }
    
    /**
     * Pop up the window at coordinates(args1, args2).
     *
     * In {@link LayoutParams}, the coordinates on the center of the screen is (0, 0), but in screen layout, (0, 0) is
     * the coordinates of the top-left point.
     *
     * @param layoutX The coordinates on X-Axis.
     * @param layoutY The coordinates on Y-Axis.
     */
    protected void popupWindow(int layoutX, int layoutY) {
        if (mWindowManager == null) {
            FloatWindowHelper.printErrorLog("Window manager is null.");
            return;
        }
        if (mWindowView == null) {
            FloatWindowHelper.printErrorLog("View is null.");
            return;
        }
        if (mLayoutParams == null) {
            FloatWindowHelper.printErrorLog("LayoutParams is null.");
            return;
        }
        
        // Convert coordinates from that in LayoutParams to that in screen layout.
        else {
            mLayoutParams.x = layoutX - mScreenWidth / 2;
            mLayoutParams.y = layoutY - mScreenHeight / 2;
        }
        
        // Remove the view itself from parent view group first.
        if (mLayoutType == LayoutType.ITSELF && mParentView != null) {
            mParentView.removeView(mWindowView);
        }
        // Pop up the window.
        mWindowManager.addView(mWindowView, mLayoutParams);
        mIsWindowShowing = true;
        FloatWindowHelper.printInfoLog("Float window pop up succeed.");
    }
    
    /**
     * Move the window by distance deltaX on X-Axis and deltaY on Y-Axis.
     *
     * There're two situations here:
     * 1) {@link BaseFloatWindow#mMoveDirection} set as {@link MoveDirection#X_AXIS} or {@link MoveDirection#Y_AXIS}
     * or {@link MoveDirection#ANY_DIRECTION}. In this case, moving direction specified as one direction or both two
     * directions, and can't be changed.
     * 2) {@link BaseFloatWindow#mMoveDirection} set as {@link MoveDirection#FIRST_DIRECTION}. In this case, moving
     * direction is not fixed, and the direction can be only one direction where moved towards at the beginning.
     * Between nearly two events, the moving directions can be different. For example, the first time moved towards
     * X-Axis, the second time can be moved towards either X-Axis or Y-Axis, and don't have to change the parameter
     * in FloatWindow.
     *
     * @param deltaX The moving distance on X-Axis.
     * @param deltaY The moving distance on Y-Axis.
     */
    protected void moveWindow(int deltaX, int deltaY) {
        if (mWindowManager == null) {
            FloatWindowHelper.printErrorLog("Window manager is null.");
            return;
        }
        if (mWindowView == null) {
            FloatWindowHelper.printErrorLog("View is null.");
            return;
        }
        if (mLayoutParams == null) {
            FloatWindowHelper.printErrorLog("LayoutParams is null.");
            return;
        }
        
        // Update layout params though delta value on X-Axis.
        if (mMoveDirection == MoveDirection.X_AXIS || mMoveDirection == MoveDirection.ANY_DIRECTION ||
                (mMoveDirection == MoveDirection.FIRST_DIRECTION && mFirstDirection == FirstDirection.FIRST_X)) {
            mLayoutParams.x = mLayoutParams.x + deltaX;
        }
        // Update layout params though delta value on Y-Axis.
        if (mMoveDirection == MoveDirection.Y_AXIS || mMoveDirection == MoveDirection.ANY_DIRECTION ||
                (mMoveDirection == MoveDirection.FIRST_DIRECTION && mFirstDirection == FirstDirection.FIRST_Y)) {
            mLayoutParams.y = mLayoutParams.y + deltaY;
        }
        
        // TODO: mWindowView.isAttachedToWindow()
        mWindowManager.updateViewLayout(mWindowView, mLayoutParams);
    }
    
    /**
     * Dismiss the window.
     */
    protected void dismissWindow() {
        if (mWindowManager == null) {
            FloatWindowHelper.printErrorLog("Window manager is null.");
            return;
        }
        if (mWindowView == null) {
            FloatWindowHelper.printErrorLog("View is null.");
            return;
        }
        
        // Remove the window from WindowManager immediately.
        mWindowManager.removeViewImmediate(mWindowView);
        // Re-add view to parent view group.
        if (mLayoutType == LayoutType.ITSELF && mParentView != null) {
            mParentView.addView(mWindowView, mLayoutParams);
        }
        FloatWindowHelper.printInfoLog("Float window remove succeed.");
        
        onWindowDestroy();
    }
    
    /**
     * Update the window's transparent.
     */
    protected void updateWindowTransparent(float transparent) {
        this.mLayoutParams.alpha = transparent;
    }
    
    /**
     * Do something before {@link View#dispatchTouchEvent(MotionEvent)}. Can't be override.
     */
    protected final void doBeforeOnDispatchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Update the last event's coordinates.
                mLastX = getEventX(event);
                mLastY = getEventY(event);
                break;
            
            case MotionEvent.ACTION_MOVE:
                // If first direction was been specified, ignore the direction setting operation.
                if (mFirstDirection != FirstDirection.NO_DIRECTION) {
                    break;
                }
                
                // Get current coordinates.
                final int currentX = getEventX(event);
                final int currentY = getEventY(event);
                // Set first direction as First-X.
                if (Math.abs(currentX - mLastX) > Math.abs(currentY - mLastY)) {
                    FloatWindowHelper.printInfoLog("Will set first direction as First-X.");
                    mFirstDirection = FirstDirection.FIRST_X;
                }
                // Set first direction as First-Y.
                else {
                    FloatWindowHelper.printInfoLog("Will set first direction as First-Y.");
                    mFirstDirection = FirstDirection.FIRST_Y;
                }
                break;
            
            case MotionEvent.ACTION_UP:
                break;
        }
    }
    
    /**
     * Do something after {@link View#dispatchTouchEvent(MotionEvent)}. Can't be override.
     */
    protected final void doAfterOnDispatchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            
            case MotionEvent.ACTION_MOVE:
                // Update the last event's coordinates.
                mLastX = getEventX(event);
                mLastY = getEventY(event);
                break;
        }
    }
    
    /**
     * Do something when received {@link ViewGroup#dispatchTouchEvent(MotionEvent)}.
     */
    @EventResult
    protected int doOnDispatchEvent(MotionEvent event) {
        return EventResult.SUPER;
    }
    
    /**
     * Do something when received {@link ViewGroup#onInterceptTouchEvent(MotionEvent)}.
     */
    @EventResult
    protected int doOnInterceptTouchEvent(MotionEvent event) {
        return EventResult.SUPER;
    }
    
    /**
     * Do something when received {@link ViewGroup#onTouchEvent(MotionEvent)}.
     */
    @EventResult
    protected int doOnTouchEvent(MotionEvent event) {
        return EventResult.SUPER;
    }
    
    /**
     * Get raw coordinates on X-Axis. We can't use {@link MotionEvent#getX()}, due to the layout, getX() will got the
     * relative position to the layout, not the coordinates on screen.
     */
    protected int getEventX(MotionEvent event) {
        return (int) event.getRawX();
    }
    
    /**
     * Get raw coordinates on Y-Axis. We can't use {@link MotionEvent#getY()}, due to the layout, getY() will got the
     * relative position to the layout, not the coordinates on screen.
     */
    protected int getEventY(MotionEvent event) {
        return (int) event.getRawY();
    }
    
    /**
     * Get coordinate on X-Axis when last event fired.
     */
    public int getLastX() {
        return mLastX;
    }
    
    /**
     * Get coordinate on Y-Axis when last event fired.
     */
    public int getLastY() {
        return mLastY;
    }
    
    public boolean isWindowShowing() {
        return mIsWindowShowing;
    }
    
    /**
     * Get {@link Context} instance.
     */
    private Context getContext() {
        if (mWeakRefContext == null) {
            // Throw exception when context is null.
            throw new RuntimeException("Context is null.");
        }
        return mWeakRefContext.get();
    }
}