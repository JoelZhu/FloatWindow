package com.joelzhu.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Sample code of the FloatView.
 */
public class MainActivity extends Activity {
    private FloatView mFloatView;
    LinearLayout linearLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.rootView);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
            // No permission gained, jump to request permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
        
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        view.setBackgroundColor(Color.BLUE);
        
        mFloatView = new FloatView.Builder(this, LauncherFloat.class)
                .moveDirection(MoveDirection.FIRST_DIRECTION)
                .parent(linearLayout)
                .transparent(0.7f)
                .create();
        
        mFloatView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        mFloatView.setBackgroundColor(Color.YELLOW);
        
        TextView textView = new TextView(this);
        textView.setText("Child View");
        textView.setBackgroundColor(Color.RED);
        mFloatView.addView(textView);
        linearLayout.addView(mFloatView);
    }
}