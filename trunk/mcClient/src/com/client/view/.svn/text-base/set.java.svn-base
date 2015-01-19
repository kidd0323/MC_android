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

public class set extends Activity {
	private Looper looper;
	private Handler uMessageHandler;  
	private Handler bMessageHandler;  
	private boolean isNetError;
	
	private Button loginoutBT;
	private Button modifyinfoBT;
	private Button linkxlBT;
	private Button feedbackBT;
	private Button guideBT;
	private Button officialBT;
	private Button aboutBT;
	private Button inviteFriendBT;
	private Button cancelBT;
	private String userID;
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String username;
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.set);
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
    	loginoutBT = (Button)findViewById(R.id.loginout);
    	modifyinfoBT = (Button)findViewById(R.id.modifyinfo);
    	feedbackBT = (Button)findViewById(R.id.feedback);
    	guideBT = (Button)findViewById(R.id.guide);
    	officialBT = (Button)findViewById(R.id.official);
    	aboutBT = (Button)findViewById(R.id.about);
    	inviteFriendBT = (Button)findViewById(R.id.inviteFriend);    
    	cancelBT=(Button)findViewById(R.id.setback);
    }
    private void initView(){
    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
    	username = share.getString(SHARE_LOGIN_USERNAME, "");
		
    }
    private void setListener(){
    	loginoutBT.setOnClickListener(loginoutListener);
    	modifyinfoBT.setOnClickListener(modifyinfoListener);
    	feedbackBT.setOnClickListener(feedbackListener);
    	guideBT.setOnClickListener(guideListener);
    	officialBT.setOnClickListener(officialListener);
    	aboutBT.setOnClickListener(aboutListener);
    	inviteFriendBT.setOnClickListener(inviteFriendListener);
    	cancelBT.setOnClickListener(cancelListener);

    }
    
    private OnClickListener cancelListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, BlocksView.class);
			startActivity(intent);
			
		}
    	
    };
    private OnClickListener loginoutListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			clearSharePassword();
			Intent intent = new Intent();
			//intent.addFlags(Intent.flag);
			intent.setClass(set.this, LoginView.class);
			startActivity(intent);
		}
    };
    private OnClickListener modifyinfoListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, modify.class);
			startActivity(intent);
			
		}
    	
    };
//    private OnClickListener linkxlListener = new OnClickListener(){
//
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//    	
//    };
    private OnClickListener feedbackListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, feedback.class);
			startActivity(intent);
			
		}
    	
    };
    private OnClickListener guideListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, guide.class);
			startActivity(intent);
			
		}
    	
    };
    private OnClickListener officialListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			String ret = ConnUtil.love(userID);
			if(ret.equals("error"))
			{
				Toast.makeText(getApplicationContext(), "网络传输错误，请重试。", Toast.LENGTH_SHORT).show();
			}
			else if(ret.equals("1"))
			{
				Toast.makeText(getApplicationContext(), "关注成功，感谢您的支持！", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "关注失败，请重试。", Toast.LENGTH_SHORT).show();
			}
		}
    	
    };
    private OnClickListener aboutListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, about.class);
			startActivity(intent);
			
		}
    	
    };
    private OnClickListener inviteFriendListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(set.this, inviteFriend.class);
			startActivity(intent);
		}
    };
    
    private void clearSharePassword(){

		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
    	
    }
    
}
