package com.lifesense.mobsmssdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.lifesense.mobsmssdk.widget.CountryList.CountryListView;
import com.lifesense.mobsmssdk.widget.CountryList.GroupListView;
import com.lifesense.mobsmssdk.widget.CountryList.SearchEngine;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class CountryListActivity extends ActionBarActivity implements GroupListView.OnItemClickListener, View.OnClickListener, TextWatcher {


    private CountryListView lvCountry;
    private EventHandler handler;
    private HashMap<String, String> countryRules;
    private EditText etSearch;
    private View hintView;


    public static Intent makeIntent(Context mContext) {
        return new Intent(mContext, CountryListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        initView();
        SearchEngine.prepare(this, new Runnable() {
            public void run() {
                getSupportCountriesList();
            }
        });

        getSupportActionBar().hide();

    }


    private void initView() {

        lvCountry = (CountryListView) findViewById(R.id.lv_country_list);
        lvCountry.setOnItemClickListener(this);
        hintView = findViewById(R.id.rl_hint_view_loading);
        findViewById(R.id.ll_back).setOnClickListener(this);
        findViewById(R.id.ivSearch).setOnClickListener(this);
        findViewById(R.id.iv_clear).setOnClickListener(this);
        etSearch = (EditText) findViewById(R.id.et_put_identify);
        etSearch.addTextChangedListener(this);
    }


    private void getSupportCountriesList() {

        handler = new EventHandler() {

            public void afterEvent(int event, final int result, final Object data) {
                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Log.e("ok ", " get complete list");
                        onCountryListGot((ArrayList<HashMap<String, Object>>) data);
                    } else {
                        Toast.makeText(CountryListActivity.this, "网络错误，请链接网络", Toast.LENGTH_SHORT).show();

//                        finish();
                    }
                }
            }
        };
        // 注册回调接口
        SMSSDK.registerEventHandler(handler);
        // 获取国家列表
        SMSSDK.getSupportedCountries();
    }

    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        Log.e("countlIs", " parce the cou");
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }
            if (countryRules == null) {
                countryRules = new HashMap<String, String>();
            }
            countryRules.put(code, rule);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lvCountry.setVisibility(View.VISIBLE);
                hintView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(GroupListView parent, View view, int group, int position) {
        if (position >= 0) {
            String[] country = lvCountry.getCountry(group, position);
            if (countryRules != null && countryRules.containsKey(country[1])) {
                Toast.makeText(this, "code=" + country[1], Toast.LENGTH_SHORT).show();
                String countryCode = country[1];
                setResult(RESULT_OK, SignUpActivity.makeResultIntent(countryCode));
                finish();
            } else {
                Toast.makeText(this, "暂时不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        lvCountry.onSearch(s.toString().toLowerCase());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        SMSSDK.unregisterEventHandler(handler);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_back) {
            finish();
        } else if (id == R.id.ivSearch) {
            // 搜索
            findViewById(R.id.llTitle).setVisibility(View.GONE);
            findViewById(R.id.llSearch).setVisibility(View.VISIBLE);
            etSearch.getText().clear();
            etSearch.requestFocus();
        } else if (id == R.id.iv_clear) {
            etSearch.getText().clear();
        }
    }

}
