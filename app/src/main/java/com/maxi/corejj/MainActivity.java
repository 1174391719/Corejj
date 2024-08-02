package com.maxi.corejj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.maxi.corejj.broadcast.BroadcastPresenter;
import com.maxi.corejj.infrastucture.utils.AnimationUtils;
import com.maxi.corejj.infrastucture.utils.FilePathUtils;
import com.maxi.corejj.infrastucture.utils.L;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.k(TAG, "onCreate.***************************************");

        //AnimationUtils.rotation(findViewById(R.id.iv));
        //BroadcastPresenter.instance().main(this);
        L.d(TAG, FilePathUtils.internalRoot().getAbsolutePath());
        L.d(TAG, FilePathUtils.internalFile().getAbsolutePath());
        L.d(TAG, FilePathUtils.internalCache().getAbsolutePath());
        L.d(TAG, FilePathUtils.externalPrivateFile("").getAbsolutePath());
        L.d(TAG, FilePathUtils.externalPrivateCache().getAbsolutePath());
        L.d(TAG, FilePathUtils.externalPublicRoot().getAbsolutePath());
        L.d(TAG, FilePathUtils.systemRoot().getAbsolutePath());
        L.d(TAG, FilePathUtils.systemData().getAbsolutePath());
    }
}