package com.lifesense.mobsmssdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SignUpActivity extends Activity {


    private EditText etPhoneNum;
    private EditText etValidCode;
    private TextView tvCountryCode;

    private Button btnGetCode;
    private int remainSendCodeTime = 60;
    private static final int REMAIN_WAIT_CODE = 15648;
    private static final int CAN_RESEND_CODE = 654982;
    private static final int REQ_COUNRYT_CODE = 6542;
    private ProgressDialog dialog4;
    private EventHandler eventHandler;
    private String TAG = SignUpActivity.class.getSimpleName();
    private final static String EXTRA_COUNTRY_CODE = "extra_code";


    // 填写注册得到的APPKEY
    private static String APPKEY = "";

    // 填写注册得到的APPSECRET
    private static String APPSECRET = "";


    public static Intent makeIntent(Context mContext) {
        return new Intent(mContext, SignUpActivity.class);
    }

    public static Intent makeResultIntent(String countrycode) {
        return new Intent().putExtra(EXTRA_COUNTRY_CODE, countrycode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        initialView();

        /**
         *在使用前，请输入你自己的账号的信息
         *   // 填写注册得到的APPKEY
         private static String APPKEY = "";

         // 填写注册得到的APPSECRET
         private static String APPSECRET = "";

         */
        initSDK();

    }


    private void initialView() {
        etPhoneNum = (EditText) findViewById(R.id.asu_telephone_et);
        etValidCode = (EditText) findViewById(R.id.asu_validCode_et);
        tvCountryCode = (TextView) findViewById(R.id.asu_country_code_tv);
        btnGetCode = (Button) findViewById(R.id.asu_getcode_btn);

    }


    /**
     *
     */
    private void initSDK() {

        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                Log.e(TAG, "  event=" + event + " result=" + result + "  data=" + data);
                handler.sendMessage(msg);
            }
        };

    }


    public void onSelectCountryCodeClick(View view) {

        startActivityForResult(CountryListActivity.makeIntent(this), REQ_COUNRYT_CODE);
    }

    public void onRegClick(View view) {


        String veriCode = etValidCode.getText().toString();
        String countryCode = tvCountryCode.getText().toString();
        String phoneNums = etPhoneNum.getText().toString();


        if (StringUtil.isEmpty(phoneNums) || StringUtil.isEmpty(veriCode)) {
            return;
        }
        SMSSDK.submitVerificationCode(countryCode, phoneNums, veriCode);


        dialog4 = ProgressDialog.show(this, "", "Regeistering ..",
                false, true);


    }

    public void onGetValidCodeClick(View view) {

        view.setClickable(false);


        String phoneNums = etPhoneNum.getText().toString();

        if (StringUtil.isEmpty(phoneNums)) {
            return;
        }
        SMSSDK.getVerificationCode("86", phoneNums);


        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; remainSendCodeTime > 0; remainSendCodeTime--) {
                    handler.sendEmptyMessage(REMAIN_WAIT_CODE);
                    if (remainSendCodeTime <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(CAN_RESEND_CODE);
            }
        }).start();


    }


    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(eventHandler);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "on stop - unregist eventhandle");
        SMSSDK.unregisterEventHandler(eventHandler);
        SMSSDK.unregisterAllEventHandler();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == REMAIN_WAIT_CODE) {
                btnGetCode.setText("Resend(" + remainSendCodeTime-- + ")");
            } else if (msg.what == CAN_RESEND_CODE) {
                btnGetCode.setText("get Code");
                btnGetCode.setClickable(true);
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//

                        dialog4.cancel();
                        Toast.makeText(getApplicationContext(), "submit code ",
                                Toast.LENGTH_SHORT).show();

//                        Intent intent = new Intent(RegisterActivity.this,
//                                MainActivity.class);
//                        startActivity(intent);

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "get code Successful",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //请务必注释掉这一行，因为在onPause中注销掉了handler依旧会发消息到这里来！！导致奔溃，呵呵
//                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQ_COUNRYT_CODE) {
            String counCode = data.getStringExtra(EXTRA_COUNTRY_CODE);
            tvCountryCode.setText(counCode + "");
        }
    }
}
