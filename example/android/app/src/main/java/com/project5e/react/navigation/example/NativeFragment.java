package com.project5e.react.navigation.example;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.react.views.text.ReactTextView;

public class NativeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout contentView = new FrameLayout(inflater.getContext());
        contentView.setBackgroundColor(Color.WHITE);
        contentView.addView(fakeRnButton());
        return contentView;
    }

    private ReactTextView fakeRnButton() {
        ReactTextView btn = new ReactTextView(getContext());
        btn.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(16, 16, 16, 16);
        btn.setIncludeFontPadding(true);
        btn.setBorderRadius(2);
        btn.setAllCaps(true);
        btn.setBackgroundColor(0xFF2196F3);
        btn.setTextColor(Color.WHITE);
        btn.setText("pop");
//        btn.setOnClickListener(v -> Navigation.findNavController(this.getView()).navigateUp());
        btn.setOnClickListener(v -> getActivity().onBackPressed());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) btn.setElevation(4);
        return btn;
    }

}
