package com.kits.kowsarapp.activity.base;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kits.kowsarapp.R;

public class Base_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_activity_main);
    }
}