package com.project5e.react.navigation.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.facebook.react.views.text.ReactTextView;

public class NativeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentView = new FrameLayout(this);
        contentView.addView(fakeRnButton());
        setContentView(contentView);
    }

    private ReactTextView fakeRnButton() {
        ReactTextView btn = new ReactTextView(this);
        btn.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(16, 16, 16, 16);
        btn.setIncludeFontPadding(true);
        btn.setBorderRadius(2);
        btn.setAllCaps(true);
        btn.setBackgroundColor(0xFF2196F3);
        btn.setTextColor(Color.WHITE);
        btn.setText("pop");
        btn.setOnClickListener(v -> onBackPressed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) btn.setElevation(4);
        return btn;
    }
}
