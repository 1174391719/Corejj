package com.zyzxsp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.log.L;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ainemo.sdk.otf.ConnectNemoCallback;
import com.ainemo.sdk.otf.LoginResponseData;
import com.ainemo.sdk.otf.NemoSDK;
import com.zyzxsp.R;
import com.zyzxsp.UserBean;
import com.zyzxsp.fragment.AddressListFragment;
import com.zyzxsp.fragment.FileFragment;
import com.zyzxsp.fragment.HomeFragment;
import com.zyzxsp.fragment.MineFragment;
import com.zyzxsp.utils.PermissionUtils;
import com.zyzxsp.utils.StatusBarUtils;
import com.zyzxsp.utils.Utils;
import com.zyzxsp.utils.ZLog;

import java.util.ArrayList;
import java.util.List;

public class ZyHomeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    public static final String TAG = "ZyHomeActivity";
    public static final String HOME_FRAGMENT_TAG = "home_fragment_tag";
    public static final String ADDRESSLIST_FRAGMENT_TAG = "addresslist_fragment_tag";
    public static final String FILE_FRAGMENT_TAG = "file_fragment_tag";
    public static final String MINE_FRAGMENT_TAG = "mine_fragment_tag";
    public static final UserBean sUserBean = new UserBean();

    private FrameLayout mFrameLayout;
    private RadioGroup mRadioGroup;
    private RadioButton mHomeRadioButton;
    private RadioButton mAddressLsitRadioButton;
    private RadioButton mFileRadioButton;
    private RadioButton mMineRadioButton;
    private List<Fragment> mFragmentList;
    private HomeFragment mHomeFragment;
    // private AddressListFragment mAddressListFragment;
    //  private FileFragment mFileFragment;
    private MineFragment mMineFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private NemoSDK nemoSDK = NemoSDK.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        setContentView(R.layout.activity_home_layout);
        ZLog.i("Init...");
        PermissionUtils.checkBasePermission(this);
        mFrameLayout = findViewById(R.id.framelayout_container);
        mRadioGroup = findViewById(R.id.radio_group_button);
        mHomeRadioButton = findViewById(R.id.radio_button_home);
//        mAddressLsitRadioButton = findViewById(R.id.radio_button_address_list);
//        mFileRadioButton = findViewById(R.id.radio_button_file);
        mMineRadioButton = findViewById(R.id.radio_button_mine);
        mHomeRadioButton.setChecked(true);
        mRadioGroup.setOnCheckedChangeListener(this);

        initFragment();
        setTabSelection(0);

        Intent intent = getIntent();
        String myNumber = intent.getStringExtra("MY_NUMBER");
        String displayName = intent.getStringExtra("displayName");
        L.i(TAG, "displayNameCallActivity11=" + displayName);


//        nemoSDK.loginExternalAccount("方中信", sAccount, new ConnectNemoCallback() {
//            @Override
//            public void onFailed(int i) {
//                ZLog.e("i:" + i);
//            }
//
//            @Override
//            public void onSuccess(LoginResponseData loginResponseData, boolean b) {
//                ZLog.d("loginResponseData:" + loginResponseData);
//            }
//
//            @Override
//            public void onNetworkTopologyDetectionFinished(LoginResponseData loginResponseData) {
//                ZLog.e("loginResponseData:" + loginResponseData);
//            }
//        });

        if (myNumber != null)
            mHomeFragment.setMyNumber(myNumber);

        if (displayName != null)
            mHomeFragment.setDisplayName(displayName);
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();

        mFragmentList = new ArrayList<>();
        mHomeFragment = new HomeFragment();
//        mAddressListFragment = new AddressListFragment();
//        mFileFragment = new FileFragment();
        mMineFragment = new MineFragment();

        mFragmentList.add(mHomeFragment);
//        mFragmentList.add(mAddressListFragment);
//        mFragmentList.add(mFileFragment);
        mFragmentList.add(mMineFragment);


        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.add(R.id.framelayout_container, mHomeFragment, HOME_FRAGMENT_TAG);
//        mTransaction.add(R.id.framelayout_container, mAddressListFragment, ADDRESSLIST_FRAGMENT_TAG);
//        mTransaction.add(R.id.framelayout_container, mFileFragment, FILE_FRAGMENT_TAG);
        mTransaction.add(R.id.framelayout_container, mMineFragment, MINE_FRAGMENT_TAG);
        mTransaction.commit();

    }

    /**
     * 根据传入的index参数来设置选中的tab页
     *
     * @param index 每个tab页对应的下标。首页，通讯录，文件夹，我的
     */
    private void setTabSelection(int index) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (mFragmentList == null) {
            Log.d(TAG, "setTabSelection:  mFragmentList == null");
            return;
        }
        Log.d(TAG, "setTabSelection:  mFragmentList size " + mFragmentList.size() + " index  " + index);
        hideFragment(mFragmentList, mFragmentTransaction);
        if (index < mFragmentList.size()) {
            if (mFragmentList.get(index) != null) {
                Log.d(TAG, "setTabSelection:  mFragmentList 不为null  show    " + " index  " + index);
                mFragmentTransaction.show(mFragmentList.get(index));
            } else {
                if (index == 0) {
                    mHomeFragment = new HomeFragment();
                    mFragmentTransaction.add(R.id.framelayout_container, mHomeFragment, HOME_FRAGMENT_TAG);
                }
//                else if (index == 1) {
//                    mAddressListFragment = new AddressListFragment();
//                    mFragmentTransaction.add(R.id.framelayout_container, mAddressListFragment, ADDRESSLIST_FRAGMENT_TAG);
//                } else if (index == 2) {
//                    mFileFragment = new FileFragment();
//                    mFragmentTransaction.add(R.id.framelayout_container, mFileFragment, FILE_FRAGMENT_TAG);
//                }

                else if (index == 1) {
                    mMineFragment = new MineFragment();
                    mFragmentTransaction.add(R.id.framelayout_container, mMineFragment, MINE_FRAGMENT_TAG);
                }
            }
            mFragmentTransaction.commit();
        }
    }

    private void hideFragment(List<Fragment> fragments, FragmentTransaction transaction) {
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i) != null) {
                    transaction.hide(fragments.get(i));
                }
            }
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_button_home:
                setTabSelection(0);
                break;
//            case R.id.radio_button_address_list:
//                setTabSelection(1);
//                break;
//            case R.id.radio_button_file:
//                setTabSelection(2);
//                break;
            case R.id.radio_button_mine:
                setTabSelection(1);
                break;
        }

    }

    @Override
    public void onClick(View v) {

    }
}
