package com.xy.shuhua.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.xy.shuhua.R;
import com.xy.shuhua.common_background.CommonModel;
import com.xy.shuhua.common_background.ServerConfig;
import com.xy.shuhua.ui.common.ActivityBaseNoSliding;
import com.xy.shuhua.util.*;
import com.xy.shuhua.util.okhttp.OkHttpUtils;
import com.xy.shuhua.util.okhttp.PrintHttpUrlUtil;
import com.xy.shuhua.util.okhttp.callback.StringCallback;
import okhttp3.Call;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoyu on 2016/3/15.
 */
public class ActivityRegister extends ActivityBaseNoSliding implements View.OnClickListener, Handler.Callback {
    private View backView;
    private EditText phoneNumber;
    private EditText password;
    private EditText verCode;
    private TextView getVerCode;
    private TextView register;

    private WeakHandler weakHandler = new WeakHandler(this);
    private static final int TotalSeconds = 60;
    private int currentSeconds = TotalSeconds;
    private static final int TimeSpan = 1000;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityRegister.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void getViews() {
        backView = findViewById(R.id.backView);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        verCode = (EditText) findViewById(R.id.verCode);
        getVerCode = (TextView) findViewById(R.id.getVerCode);
        register = (TextView) findViewById(R.id.register);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {
        backView.setOnClickListener(this);
        getVerCode.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                tryToRegister();
                break;
            case R.id.getVerCode:
                tryToGetVerCode();
                break;
            case R.id.backView:
                finish();
                break;
        }
    }

    private void tryToRegister() {
        final String phoneNumberStr = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.makeShortText("输入电话号码");
            return;
        }
        if (!CommonUtil.isPhoneNumberValid(phoneNumberStr)) {
            ToastUtil.makeShortText("电话号码不合法");
            return;
        }

        String verCodeStr = verCode.getText().toString();
        if (TextUtils.isEmpty(verCodeStr)) {
            ToastUtil.makeShortText("输入验证码");
            return;
        }
        final String pwdStr = password.getText().toString();
        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.makeShortText("输入密码");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneNumberStr);
        params.put("password", pwdStr);
        params.put("activationNumber", verCodeStr);
        DialogUtil.getInstance().showLoading(this);
        PrintHttpUrlUtil.printUrl(ServerConfig.BASE_URL + ServerConfig.URL_REGISTER, params);
        OkHttpUtils.post()
                .params(params)
                .url(ServerConfig.BASE_URL + ServerConfig.URL_REGISTER)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        DialogUtil.getInstance().dismissLoading(ActivityRegister.this);
                        ToastUtil.makeShortText("注册失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().dismissLoading(ActivityRegister.this);
                        CommonModel commonModel = GsonUtil.transModel(response,CommonModel.class);
                        if(commonModel != null && "1".equals(commonModel.result)){
                            ToastUtil.makeShortText("注册成功");
                            finish();
                        }else{
                            ToastUtil.makeShortText("注册失败");
                        }
                    }
                });
    }

    private void tryToGetVerCode() {
        String phoneStr = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtil.makeShortText("输入电话号码");
            return;
        }
        if (!CommonUtil.isPhoneNumberValid(phoneStr)) {
            ToastUtil.makeShortText("电话号码不合法");
            return;
        }
        DialogUtil.getInstance().showLoading(this);
        Map<String, String> params = new HashMap<>();
        params.put("phoneNumber", phoneStr);
        OkHttpUtils.post()
                .params(params)
                .url(ServerConfig.BASE_URL + ServerConfig.URL_GET_VER_CODE)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        DialogUtil.getInstance().dismissLoading(ActivityRegister.this);
                        ToastUtil.makeShortText("获取验证码失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().dismissLoading(ActivityRegister.this);
                        ToastUtil.makeShortText("验证码已经发送");
                        boolean clickable = getVerCode.isClickable();
                        if (clickable) {
                            getVerCode.setClickable(false);
                            currentSeconds = TotalSeconds;
                            getVerCode.setText(String.valueOf(TotalSeconds));
                            weakHandler.sendEmptyMessageDelayed(1, TimeSpan);
                        }
                    }
                });
    }

    @Override
    public boolean handleMessage(Message message) {
        if (currentSeconds > 0) {
            currentSeconds--;
            getVerCode.setText(String.valueOf(currentSeconds));
            weakHandler.sendEmptyMessageDelayed(1, TimeSpan);
        } else {
            getVerCode.setText("获取验证码");
            getVerCode.setClickable(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        weakHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
