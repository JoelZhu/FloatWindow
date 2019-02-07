package com.joelzhu.floatwindow;

import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joelzhu.floatwindow.FloatView.Builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for layout type.
 *
 * The layout types are as below:
 * 1) {@link #ITSELF}: The window's layout is the {@link ViewGroup} itself, in this case, will remove the view from
 *      it's parent view group first. So, we must call {@link Builder#parent(ViewGroup)} at this layout type.
 * 2) {@link #VIEW}: The window's layout was created by a {@link View} instance.
 * 3) {@link #RESOURCE}: The window's layout was create by the {@link LayoutInflater} from xml files.
 */
@IntDef({
        LayoutType.ITSELF,
        LayoutType.VIEW,
        LayoutType.RESOURCE
})
@Retention(RetentionPolicy.SOURCE)
public @interface LayoutType {
    int ITSELF = 1;
    int VIEW = 2;
    int RESOURCE =3;
}