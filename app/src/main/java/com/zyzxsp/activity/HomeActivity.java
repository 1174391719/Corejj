package com.zyzxsp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zyzxsp.R;
import com.zyzxsp.UserBean;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button ThreePartyAccountNumber;//三方
    private Button XYLink_Account_Number;//小鱼
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ThreePartyAccountNumber = (Button) findViewById(R.id.bt_ThreeParty_Account_Number);
        XYLink_Account_Number = (Button) findViewById(R.id.bt_XYLink_Account_Number);

        ThreePartyAccountNumber.setOnClickListener(this);
        XYLink_Account_Number.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_ThreeParty_Account_Number:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_XYLink_Account_Number:
//                intent=new Intent(this,LoginActivity.class);
                intent = new Intent(this, MyTestActivity.class);
                startActivity(intent);
                break;

        }
    }
}
