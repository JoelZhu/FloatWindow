# FloatWindow

## Introduction:
This is a view group, which means can contains views in it, such as TextView, Button, Image, etc.<p>
But differ from other view group, when touching inside or something else, it will pop up a float window above all the applications. We can make the trigger that pop up the window as the way we want, if we extend {@link BaseFloatWindow}, and override the method {@link BaseFloatWindow#doOnDispatchEvent(MotionEvent)} and the method {@link BaseFloatWindow#doOnTouchEvent(MotionEvent)}.

## Sample:
```Java
FloatView mFloatView = new FloatView.Builder(this, [The class extends {@link BaseFloatWindow}].class)
             .layout({@link View})
             .moveDirection({@link MoveDirection})
             .transparent({@link Float})
             .create();
```
The second args of the constructor of the Builder, is the class which override the method that how to pop up the window or something else we want.

## Display:
![showing_page.gif](showing_page.gif)
