package com.client.view;

import com.client.R;
import com.client.entity.AD;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class BlocksView extends Activity {

	private final String SHARE_CHANNEL_TAG = "MAP_SHARE_CHANNEL_TAG";
	private final String SHARE_CHANNEL = "MAP_CHANNEL";
	private final String SHARE_CHANNELID = "MAP_CHANNELID";
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private Button hotBT;
	private Button annonceBT;
	private Button activityBT;
	private Button cakeBT;
	private Button sceneBT;
	private Button employBT;
	private Button examBT;
	private Button bbsBT;
	private Button msgBT;
	private Button setupBT;
	private TextView campusTV;
	private TextView ADTV;
	private TextView BADTV;
	
	private Handler uMessageHandler;  
	private Handler bMessageHandler;  
	
	private ProgressDialog proDialog;
	private Looper looper;
	private String channel;
	private String username;
	private String userID;
	private String nickname;
	private String school;
	private String schoolID;
	private String ADPosition;
	private boolean threadDestropyFlag;
	private String loginState;
	Thread ADThread;
	String jsonData;
	private final int adWindowSize = 20;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
        	userID = bundle.getString("MAP_USERID");
        	username = bundle.getString("MAP_USERNAME");
        	nickname =  bundle.getString("MAP_NICKNAME");
        }
        setContentView(R.layout.blocksui);
        findViewsById();
	}
    private void initView(){
    	campusTV.setText(school + "/" + nickname);
    }
	
	public void onStart(){
		if(null != proDialog)
			proDialog.dismiss();
        looper = Looper.myLooper();
        uMessageHandler = new UADMessageHandler(looper);
        bMessageHandler = new BADMessageHandler(looper);
        threadDestropyFlag = false;
		ADThread = new Thread(new ADHandleThread());
		ADThread.start();
    	SharedPreferences lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    	loginState = lshare.getString(SHARE_LOGIN_STATE, "");
    	school = lshare.getString(SHARE_LOGIN_SCHOOL, "");
    	schoolID = lshare.getString(SHARE_LOGIN_SCHOOLID, "");
    	Log.e("BlocksView.java", loginState);
        initView();
        setListener();
        super.onStart();
	}
	public void onStop(){
		threadDestropyFlag = true;
		super.onStop();
	}
	public void onDestroy(){

		if(null != proDialog)
			proDialog.dismiss(); 
	         //将线程销毁掉   
		if(null != ADThread){
			uMessageHandler.removeCallbacks(ADThread);
			bMessageHandler.removeCallbacks(ADThread);  
		}
		super.onDestroy();
	}

    private OnClickListener blockListener = new OnClickListener(){
		public void onClick(View v) {
			proDialog = ProgressDialog.show(BlocksView.this, "连接中..", "连接中..请稍后....", true, true);
			Intent intent = new Intent();
			intent.setClass(BlocksView.this, ChannelView.class);
			if(v.getId() == R.id.hot)
				channel = "1";
			if(v.getId() == R.id.annonce)
				channel = "2";
			if(v.getId() == R.id.activity)
				channel = "3";
			if(v.getId() == R.id.cake)
				channel = "4";
			if(v.getId() == R.id.scene)
				channel = "5";
			if(v.getId() == R.id.employ)
				channel = "6";
			if(v.getId() == R.id.exam)
				channel = "7";
			if(v.getId() == R.id.bbs)
				channel = "8";
			SharedPreferences share = getSharedPreferences(SHARE_CHANNEL_TAG, 0);
			share.edit().putString(SHARE_CHANNEL, channel).commit();
			share.edit().putString(SHARE_CHANNELID, channel).commit();	
    		startActivity(intent);
		}
    };

    private void setListener(){
		hotBT.setOnClickListener(blockListener);
		annonceBT.setOnClickListener(blockListener);
    	activityBT.setOnClickListener(blockListener);
    	cakeBT.setOnClickListener(blockListener);
    	sceneBT.setOnClickListener(sceneListener);
    	employBT.setOnClickListener(blockListener);
    	examBT.setOnClickListener(blockListener);
    	bbsBT.setOnClickListener(blockListener);
    	msgBT.setOnClickListener(msgClickListener);
    	setupBT.setOnClickListener(setupListener);
    }
    
    private OnClickListener sceneListener =new OnClickListener()
    {
    	@Override
    	public void onClick (View arg0){

		proDialog = ProgressDialog.show(BlocksView.this, "连接中..", "连接中..请稍后....", true, true);
		Intent intent = new Intent();
		intent.setClass(BlocksView.this, picshow.class);
		startActivity(intent);
    	}
    };
    
    private OnClickListener setupListener =new OnClickListener()
    {
    	@Override
    	public void onClick (View arg0){
		proDialog = ProgressDialog.show(BlocksView.this, "连接中..", "连接中..请稍后....", true, true);
		Intent intent = new Intent();
		intent.setClass(BlocksView.this, set.class);
		startActivity(intent);

    	}
    };
	private OnClickListener msgClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {

			proDialog = ProgressDialog.show(BlocksView.this, "连接中..", "连接中..请稍后....", true, true);
	    	if("true".equals(loginState)){
				Intent intent = new Intent();
				intent.setClass(BlocksView.this, MsgDealView.class);
	    		startActivity(intent);
	    	}else{
	    		Log.e("BlocksView.java msgClickListener in", loginState);

	    		new AlertDialog.Builder(BlocksView.this).setTitle("").setCancelable(true).
	    		setMessage("请您先登录").setNeutralButton("登录", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
						intent.setClass(BlocksView.this, LoginView.class);
			    		startActivity(intent);
					}
				}).setNegativeButton("返回", null).show();
	    	}

		}
	};
    
	
	class UADMessageHandler extends Handler{
		public UADMessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			
			ADTV.setText((String)msg.obj);
		}
		
	}
	class BADMessageHandler extends Handler{
		public BADMessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			
			BADTV.setText((String)msg.obj);
		}
		
	}
    private void findViewsById(){
    	msgBT = (Button)findViewById(R.id.blockmsg);
    	hotBT = (Button)findViewById(R.id.hot);
    	annonceBT = (Button)findViewById(R.id.annonce);
    	activityBT = (Button)findViewById(R.id.activity);
    	cakeBT = (Button)findViewById(R.id.cake);
    	sceneBT = (Button)findViewById(R.id.scene);
    	employBT = (Button)findViewById(R.id.employ);
    	examBT = (Button)findViewById(R.id.exam);
    	bbsBT = (Button)findViewById(R.id.bbs);
    	campusTV = (TextView)findViewById(R.id.campus);
    	ADTV = (TextView)findViewById(R.id.AD);
    	BADTV = (TextView)findViewById(R.id.ADBelow);
    	setupBT =(Button)findViewById(R.id.setup);
    }
    

    class ADHandleThread implements Runnable{
		@Override
		public void run() {
			ADPosition = "uBlocks";
			AD uADs = null;
			AD bADs = null;
			int uNum = 0;
			int bNum = 0;
	    	String ADData = ConnUtil.getADs("getPushedADs", adWindowSize, schoolID, ADPosition);
	    	Log.e("blocksView.java uAD", schoolID + " " + ADPosition + " " + ADData);
	    	if((!"error".equals(ADData)) && (!"exception".equals(ADData))){
		    	JsonUtil jsonUtil = new JsonUtil();
		    	if(!"error".equals(ADData)){
		    		uADs = (AD)jsonUtil.parseObjectFromJson(ADData, AD.class);
		    		if(null == uADs)
		    			return;
		    		uNum = uADs.getADNum();
		    	}
				ADPosition = "bBlocks";
		    	ADData = ConnUtil.getADs("getPushedADs", adWindowSize, schoolID, ADPosition);
		    	Log.e("blocksView.java bAD", schoolID + " " + ADPosition + " " + ADData);
	
		    	if(!"error".equals(ADData)){
		    		bADs = (AD)jsonUtil.parseObjectFromJson(ADData, AD.class);
		    		if(null == bADs)
		    			return;
		    		bNum = bADs.getADNum();
		    	}
		    	int i = 0;
		    	for(i = 0;; i ++)
		    	{
		    		if(threadDestropyFlag)
		    			return;
		    		if(uNum > 0){
			    		Message uMessage = Message.obtain();
			    		uMessage.obj = uADs.getADList().get(i%uNum);
			    		uMessageHandler.sendMessage(uMessage);
		    		}
		    		if(bNum > 0){
			    		Message bMessage = Message.obtain();
			    		bMessage.obj = bADs.getADList().get((i+1)%bNum);
			    		bMessageHandler.sendMessage(bMessage);
					} 
		    		try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
		    		}
		    	}
	    	}
		}
    }
}
