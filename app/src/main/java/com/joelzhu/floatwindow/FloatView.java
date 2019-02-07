package com.joelzhu.floatwindow;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**
 * Floating Window ViewGroup.
 *
 * This is a view group, which means can contains views in it, such as TextView, Button, Image, etc. But differ from
 * other view group, when touching inside or something else, it will pop up a float window above all the
 * applications. We can make the trigger that pop up the window as the way we want, if we extend
 * {@link BaseFloatWindow}, and override the method {@link BaseFloatWindow#doOnDispatchEvent(MotionEvent)} and the
 * method {@link BaseFloatWindow#doOnTouchEvent(MotionEvent)}.
 *
 * The layout of the window can be specified by ourselves, all we need is to set the parameter
 * {@link BaseFloatWindow#mView}, there're three ways to set the parameter:
 * 1) Do nothing: In this case, we will use the {@link FloatView} itself as it's window's view. It will remove the
 *      view from it's parent view group first. So, we must call {@link Builder#parent(ViewGroup)} at this layout type.
 * 2) {@link Builder#layout(int)}: Use Android layout files to inflate the view.
 * 3) {@link Builder#layout(View)}: Use {@link View} instance as the window's view.
 *
 * Here's the simplified sample code:
 * FloatView mFloatView = new FloatView.Builder(this, [The class extends {@link BaseFloatWindow}].class)
 *              .layout({@link View})
 *              .moveDirection({@link MoveDirection})
 *              .transparent({@link Float})
 *              .create();
 *
 * Attention: extends {@link LinearLayout} here, actually, we can replace it with other view group.
 * TODO: We should extends Div, etc. replace the LinearLayout in weex
 */
public class FloatView extends LinearLayout {
    // Float window instance.
    private BaseFloatWindow mFloatWindow;

    /**
     * Builder: to create the {@link FloatView} instance.
     */
    public static class Builder {
        private Context mContext;

        // The instance must be the child of BaseFloatWindow.
        private Class<? extends BaseFloatWindow> mClazz;

        // The view to be shown.
        private View mWindowView;

        // The view's parent view group.
        private ViewGroup mParentView;

        // Window's moving direction.
        @MoveDirection
        private int mMoveDirection = MoveDirection.ANY_DIRECTION;

        // Window's transparent, from 0f to 1f, which will convert to 0 - 255 as alpha.
        @WindowTransparent
        private float mWindowTransparent = WindowTransparent.MAX;

        // Where the window's view created from.
        @LayoutType
        private int mLayoutType = LayoutType.ITSELF;

        // Window's layout coordinates when been created.
        private int mWindowX = FloatWindowHelper.DEFAULT_WINDOW_X;
        private int mWindowY = FloatWindowHelper.DEFAULT_WINDOW_Y;

        // Window's size.
        private int mWindowWidth;
        private int mWindowHeight;

        public Builder(Context context, Class<? extends BaseFloatWindow> clazz) {
            this.mContext = context;
            this.mClazz = clazz;
        }

        public Builder moveDirection(@MoveDirection int moveDirection) {
            this.mMoveDirection = moveDirection;
            return this;
        }

        public Builder transparent(@WindowTransparent float windowTransparent) {
            this.mWindowTransparent = windowTransparent;
            return this;
        }

        public Builder layout(View windowView) {
            this.mWindowView = windowView;
            this.mLayoutType = LayoutType.VIEW;
            return this;
        }

        public Builder layout(@LayoutRes int layoutResId) {
            if (mContext == null) {
                throw new RuntimeException("Context is null, can't create view instance from layout resource.");
            }
            LayoutInflater inflater = LayoutInflater.from(mContext);
            this.mWindowView = inflater.inflate(layoutResId, null, false);
            this.mLayoutType = LayoutType.RESOURCE;
            return this;
        }

        public Builder parent(ViewGroup parentView) {
            this.mParentView = parentView;
            return this;
        }

        public Builder windowX(int windowX) {
            this.mWindowX = windowX;
            return this;
        }

        public Builder windowY(int windowY) {
            this.mWindowY = windowY;
            return this;
        }

        public Builder windowWidth(int windowWidth) {
            this.mWindowWidth = windowWidth;
            return this;
        }

        public Builder windowHeight(int windowHeight) {
            this.mWindowHeight = windowHeight;
            return this;
        }

        public FloatView create() {
            if (mWindowWidth == 0 || mWindowHeight == 0) {
                FloatWindowHelper.printInfoLog("The value of window's width or height is 0, will create window by " +
                        "it's actual size.");
                return new FloatView(mContext, mClazz, mMoveDirection, mWindowTransparent, mLayoutType, mWindowView,
                        mParentView, mWindowX, mWindowY);
            }
            FloatWindowHelper.printInfoLog("Will create window by specified size.");
            return new FloatView(mContext, mClazz, mMoveDirection, mWindowTransparent, mLayoutType, mWindowView,
                    mParentView, mWindowX, mWindowY, mWindowWidth, mWindowHeight);
        }
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Do not allow create instance from layout files.
        throw new RuntimeException("Not support create instance thought layout files.");
    }

    private FloatView(Context context, Class<? extends BaseFloatWindow> clazz, @MoveDirection int moveDirection,
            @WindowTransparent float windowTransparent, @LayoutType int layoutType, View windowView,
            ViewGroup parentView, int windowX, int windowY) {
        super(context);

        // Initialize view without specified window size.
        initialize(context, clazz, moveDirection, windowTransparent, layoutType, windowView, parentView,
                windowX, windowY, 0, 0);
    }

    private FloatView(Context context, Class<? extends BaseFloatWindow> clazz, @MoveDirection int moveDirection,
            @WindowTransparent float windowTransparent, @LayoutType int layoutType, View windowView,
            ViewGroup parentView, int windowX, int windowY, int windowWidth, int windowHeight) {
        super(context);

        // Initialize view with window size.
        initialize(context, clazz, moveDirection, windowTransparent, layoutType, windowView, parentView,
                windowX, windowY, windowWidth, windowHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && mFloatWindow.mLayoutType == LayoutType.ITSELF) {
            int mViewWidth = r - l;
            int mViewHeight = b - t;
            FloatWindowHelper.printInfoLog("Set window's size by the view group itself size.");
            setWindowSize(mViewWidth, mViewHeight);
        }
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * Initialize the view.
     */
    private void initialize(Context context, Class<? extends BaseFloatWindow> clazz, @MoveDirection int moveDirection,
            @WindowTransparent float windowTransparent, @LayoutType int layoutType, View windowView,
            ViewGroup parentView, int windowX, int windowY, int windowWidth, int windowHeight) {
        if (layoutType == LayoutType.ITSELF && parentView == null) {
            throw new RuntimeException("Please call the method parent() to set the view itself as the window's layout");
        }

        if (context == null) {
            FloatWindowHelper.printErrorLog("Context is null, check the parameter when creating the instance.");
            return;
        }

        // Create instance.
        try {
            mFloatWindow = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            FloatWindowHelper.printErrorLog("Create instance failed, error: " + e.getMessage());
        }

        // Set the window's layout.
        if (layoutType == LayoutType.ITSELF) {
            mFloatWindow.mView = this;
        } else {
            mFloatWindow.mView = windowView;
        }

        // Set other values.
        mFloatWindow.mWeakRefContext = new WeakReference<>(context);
        mFloatWindow.mMoveDirection = moveDirection;
        mFloatWindow.mFirstDirection = FirstDirection.NO_DIRECTION;
        mFloatWindow.mWindowX = windowX;
        mFloatWindow.mWindowY = windowY;
        mFloatWindow.mParentView = parentView;
        mFloatWindow.mLayoutType = layoutType;

        // On window create.
        mFloatWindow.onWindowCreate();

        // Set window's size.
        if (layoutType == LayoutType.ITSELF) {
            FloatWindowHelper.printInfoLog("Window's size calculating, will set later.");
        } else if (windowWidth != 0 && windowHeight != 0) {
            FloatWindowHelper.printInfoLog("Set window's size by custom size.");
            setWindowSize(windowWidth, windowHeight);
        } else {
            FloatWindowHelper.printInfoLog("Set window's size by it's actual size.");
            setWindowSize(layoutType, windowView);
        }

        // Set window's transparent.
        mFloatWindow.updateWindowTransparent(windowTransparent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Do sth. before dispatch event been called.
        mFloatWindow.doBeforeOnDispatchEvent(event);

        // Call the method override by the instance class.
        int result = mFloatWindow.doOnDispatchEvent(event);

        // Do sth. after dispatch event been called.
        mFloatWindow.doAfterOnDispatchEvent(event);

        // Return different value according to the result.
        switch (result) {
            case EventResult.TRUE:
                return true;
            case EventResult.FALSE:
                return false;
            case EventResult.SUPER:
                return super.dispatchTouchEvent(event);
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Call the method override by the instance class.
        int result = mFloatWindow.doOnInterceptTouchEvent(event);
        // Return different value according to the result.
        switch (result) {
            case EventResult.TRUE:
                return true;
            case EventResult.FALSE:
                return false;
            case EventResult.SUPER:
                return super.onInterceptTouchEvent(event);
            default:
                return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the method override by the instance class.
        int result = mFloatWindow.doOnTouchEvent(event);
        // Return different value according to the result.
        switch (result) {
            case EventResult.TRUE:
                return true;
            case EventResult.FALSE:
                return false;
            case EventResult.SUPER:
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    /**
     * Set window's size from specified size, no matter what the size is from layout.
     */
    private void setWindowSize(int windowWidth, int windowHeight) {
        mFloatWindow.mLayoutParams.width = windowWidth;
        mFloatWindow.mLayoutParams.height = windowHeight;
        FloatWindowHelper.printInfoLog("Window's width & height: " + mFloatWindow.mLayoutParams.width + ", " +
                mFloatWindow.mLayoutParams.height);
    }

    /**
     * Set window's size from specified layout, both from resource layout and view instance.
     */
    private void setWindowSize(@LayoutType int layoutType, View view) {
        if (view == null) {
            FloatWindowHelper.printErrorLog("Host view is null.");
            return;
        }

        switch (layoutType) {
            case LayoutType.VIEW:
                // Get window's size from view instance.
                mFloatWindow.mLayoutParams.width = view.getLayoutParams().width;
                mFloatWindow.mLayoutParams.height = view.getLayoutParams().height;
                break;

            case LayoutType.RESOURCE:
                // Get window's size from measuring result which calculated thought view instance.
                // TODO: With some problems when get view's width & height.
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
                view.measure(width, height);
                mFloatWindow.mLayoutParams.width = view.getMeasuredWidth();
                mFloatWindow.mLayoutParams.height = view.getMeasuredHeight();
                break;

            default:
                break;
        }

        FloatWindowHelper.printInfoLog("Window's width & height: " + mFloatWindow.mLayoutParams.width + ", " +
                mFloatWindow.mLayoutParams.height);
    }
}
