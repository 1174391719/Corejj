package com.zyzxsp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyzxsp.R;
import com.zyzxsp.constant.ConstantUrl;
import com.zyzxsp.bean.LoginOutResData;
import com.zyzxsp.view.HeaderTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import zxsp.com.netlibrary.CallBackUtil;
import zxsp.com.netlibrary.OkhttpUtil;

public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = "ModifyPasswordActivity";
    private HeaderTitleView mHeaderTitleView;
    private EditText mInputOldPasswordEdit;
    private ImageView mSeeOldPasswordImage;
    private ImageView mClearOldPasswordImage;

    private EditText mInputNewPasswordEdit;
    private ImageView mSeeNewPasswordImage;
    private ImageView mClearNewPasswordImage;

    private EditText mInputConfirmPasswordEdit;
    private ImageView mSeeConfirmPasswordImage;
    private ImageView mClearConfirmPasswordImage;

    private Button mModifyPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        mHeaderTitleView = findViewById(R.id.modify_password_header_view);
        mInputOldPasswordEdit = findViewById(R.id.input_old_password_edit);
        mSeeOldPasswordImage = findViewById(R.id.see_old_password);
        mClearOldPasswordImage = findViewById(R.id.clear_old_password);
        mSeeOldPasswordImage.setOnClickListener(this);
        mClearOldPasswordImage.setOnClickListener(this);

        mInputNewPasswordEdit = findViewById(R.id.input_new_password_edit);
        mSeeNewPasswordImage = findViewById(R.id.see_input_new_password);
        mClearNewPasswordImage = findViewById(R.id.clear_input_new_password);
        mSeeNewPasswordImage.setOnClickListener(this);
        mClearNewPasswordImage.setOnClickListener(this);

        mInputConfirmPasswordEdit = findViewById(R.id.confirm_password_edit);
        mSeeConfirmPasswordImage = findViewById(R.id.see_confirm_password);
        mClearConfirmPasswordImage = findViewById(R.id.clear_confirm_password);
        mSeeConfirmPasswordImage.setOnClickListener(this);
        mClearConfirmPasswordImage.setOnClickListener(this);

        mModifyPasswordBtn = findViewById(R.id.modify_password_btn);
        mModifyPasswordBtn.setOnClickListener(this);

        mHeaderTitleView.setmOnHeaderTitleViewClick(new HeaderTitleView.onHeaderTitleViewClick() {
            @Override
            public void leftClick(View view) {
                finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });

        mInputOldPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else{
                    //失去焦点
                    String oldPass = mInputOldPasswordEdit.getText().toString().trim();
                    validatePassword(oldPass);
                }
            }
        });

        mInputOldPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                judgeModifyBtnClick();
            }
        });

        mInputNewPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                judgeModifyBtnClick();
            }
        });

        mInputConfirmPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                judgeModifyBtnClick();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.see_old_password:
                setEditTextVisableOrnot(mInputOldPasswordEdit,mSeeOldPasswordImage);
                break;
            case R.id.clear_old_password:
                mInputOldPasswordEdit.setText("");
                break;
            case R.id.see_input_new_password:
                setEditTextVisableOrnot(mInputNewPasswordEdit,mSeeNewPasswordImage);
                break;
            case R.id.clear_input_new_password:
                mInputNewPasswordEdit.setText("");
                break;
            case R.id.see_confirm_password:
                setEditTextVisableOrnot(mInputConfirmPasswordEdit,mSeeConfirmPasswordImage);
                break;
            case R.id.clear_confirm_password:
                mInputConfirmPasswordEdit.setText("");
                break;

            case R.id.modify_password_btn:
                String oldPasStr = mInputOldPasswordEdit.getText().toString().trim();
                String newPassStr = mInputNewPasswordEdit.getText().toString().trim();
                String newPassConfirmStr = mInputConfirmPasswordEdit.getText().toString().trim();
                if(!newPassStr.equals(newPassConfirmStr)){
                    Toast.makeText(ModifyPasswordActivity.this,"两次输入密码不一致",Toast.LENGTH_LONG).show();
                }else{
                    modifyPassword(oldPasStr,newPassStr);
                }
                break;
        }

    }

    public void judgeModifyBtnClick(){
        String oldPasStr = mInputOldPasswordEdit.getText().toString().trim();
        String newPassStr = mInputNewPasswordEdit.getText().toString().trim();
        String newPassConfirmStr = mInputConfirmPasswordEdit.getText().toString().trim();
        if(oldPasStr.length() > 0 && newPassStr.length() > 0 && newPassConfirmStr.length() > 0){
            mModifyPasswordBtn.setClickable(true);
        }else{
            mModifyPasswordBtn.setClickable(false);
        }
    }

    public void setEditTextVisableOrnot(EditText editText,ImageView visImage){
        int isVisible = editText.getInputType();
        int cursorPosition = editText.length();
        Log.d("setEditTextVisableOrnot", "isVisible: " + isVisible);
        Log.d("setEditTextVisableOrnot", "TYPE_TEXT_VARIATION_PASSWORD: "+InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Log.d("setEditTextVisableOrnot", "TYPE_TEXT_VARIATION_VISIBLE_PASSWORD: "+InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        if (isVisible == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            visImage.setImageResource(R.drawable.icon_loading);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setSelection(cursorPosition);
        }else{
            visImage.setImageResource(R.drawable.icon_warn);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setSelection(cursorPosition);
        }
    }

    /**
     * 验证旧密码
     * @param oldPassword
     */
    public void validatePassword(String oldPassword){

        String url = ConstantUrl.headerUrl + ConstantUrl.validatePassword;
        Log.d(TAG, "validatePassword: 验证旧密码 url  " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("password",oldPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String  objectStr = object.toString();

        Log.d(TAG, "validatePassword:   请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url,objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                Log.d(TAG, "onFailure:  "+ e.toString());

            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:  "+ response);
                if(response == null){
                    return;
                }
                Gson json = new Gson();
                LoginOutResData dataBean = json.fromJson(response,LoginOutResData.class);
                if("0".equals(dataBean.getReturnCode())){

                }else{
                    String errorMess = dataBean.getReturnMessage();
                    if(!TextUtils.isEmpty(errorMess)){
                        Toast.makeText( ModifyPasswordActivity.this,errorMess,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    /**
     * 修改密码
     * @param oldPassword
     */
    public void modifyPassword(String oldPassword,String newPassword){

        String url = ConstantUrl.headerUrl + ConstantUrl.modifyPassword;
        Log.d(TAG, "modifyPassword: 修改密码 url  " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("oldPassword",oldPassword);
            object.put("newPassword",oldPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String  objectStr = object.toString();

        Log.d(TAG, "validatePassword:   请求参数是  " + objectStr);
        OkhttpUtil.okHttpPostJson(url,objectStr, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Call mcall = call;
                Log.d(TAG, "onFailure:  "+ e.toString());

            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:  "+ response);
                if(response == null){
                    return;
                }
                Gson json = new Gson();
                LoginOutResData dataBean = json.fromJson(response,LoginOutResData.class);
                if("0".equals(dataBean.getReturnCode())){
                    Toast.makeText( ModifyPasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                }else{
                    String errorMess = dataBean.getReturnMessage();
                    if(!TextUtils.isEmpty(errorMess)){
                        Toast.makeText( ModifyPasswordActivity.this,errorMess,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
