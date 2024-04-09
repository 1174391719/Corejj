package com.maxi.corejj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maxi.corejj.infrastucture.utils.AnimationUtils;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnimationUtils.rotation(findViewById(R.id.iv));
    }
}