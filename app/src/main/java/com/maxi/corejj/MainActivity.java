package com.maxi.corejj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maxi.corejj.broadcast.BroadcastPresenter;
import com.maxi.corejj.infrastucture.utils.AnimationUtils;
import com.maxi.corejj.infrastucture.utils.L;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BroadcastPresenter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.k(TAG, "onCreate.***************************************");

        //AnimationUtils.rotation(findViewById(R.id.iv));
        BroadcastPresenter.instance().main(this);
    }
}