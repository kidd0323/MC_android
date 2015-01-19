package com.client.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import com.client.R;
import com.client.R.id;
import com.client.R.layout;
import com.client.entity.AD;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.BlocksView.BADMessageHandler;
import com.client.view.BlocksView.UADMessageHandler;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Iterator;

//TODO: 干掉
public class password extends Activity {
	
	private String oripw;
	private String newpw;
	private String newpw2;
	
	private Button confirmBT;
	private Button cancelBT;
	private EditText oripwET;
	private EditText newpwET;
	private EditText newpw2ET;
	private TextView errorTextET;
	
	

	
	private Looper looper;
	private Handler uMessageHandler;  
	private Handler bMessageHandler;  
	private boolean isNetError;
	
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String username;
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
	}
	
    public void onStart(){
    	int flag = getIntent().getIntExtra("flag", 0);
    	if(flag == SysUtil.EXIT_APPLICATION)
    		finish();

        isNetError = false;
        findViewsById();
        initView();
        setListener();
        super.onStart();
    	
    }
    private void findViewsById(){
    	oripwET = (EditText)findViewById(R.id.oripw);
    	newpwET = (EditText)findViewById(R.id.newpw); 
    	newpw2ET = (EditText)findViewById(R.id.newpw2);
    	confirmBT = (Button)findViewById(R.id.confirm);
    	cancelBT = (Button)findViewById(R.id.cancel);
    	errorTextET = (TextView)findViewById(R.id.errorText);
    }
    private void initView(){
    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
    	username = share.getString(SHARE_LOGIN_USERNAME, "");
		
    }
    private void setListener(){


    	confirmBT.setOnClickListener(confirmListener);
    	cancelBT.setOnClickListener(cancelListener);

    }
    
    private OnClickListener confirmListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			oripw = oripwET.getText().toString();
			newpw = newpwET.getText().toString();
			newpw2 = newpw2ET.getText().toString();
			
			String loginState = new ConnUtil().validateAccount(username, oripw);
			if("true".equals(loginState)){
				if (newpw.equals(newpw2)){
					//写入后台
					errorTextET.setText("修改成功");
				}
				else{
					errorTextET.setText("新密码输入不一致");
					newpwET.setText("");
					newpw2ET.setText("");
					
				}
					
			}
			else{

				errorTextET.setText("当前密码输入错误");
			}
		}

    	
    };
    private OnClickListener cancelListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(password.this, BlocksView.class);
			startActivity(intent);
			
		}
    	
    };

    
    
}

