package com.client.view;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.client.R;
import com.client.util.ConnUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class inviteFriend extends Activity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_REGION = "MAP_LOGIN_REGION";
	private String SHARE_LOGIN_REGIONID = "MAP_LOGIN_REGIONID";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	private String region;
	private String campus;
	
	private EditText contentET;
	private Button postBT;
	private Button cancelBT;
	Bundle bundle;
	private String userID;
	private String postTime;
	private String postContent;
	private String location;
	private String imageUrl;
	protected static Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private int isChecked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.invitefriend);
		findViewsById();
	}

	private void findViewsById() {
		contentET = (EditText)findViewById(R.id.ifContent);
		postBT = (Button)findViewById(R.id.iSend);
		cancelBT = (Button)findViewById(R.id.iCancel);
	}
	

	@Override
	protected void onStart() {
    	SharedPreferences lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    	userID  = lshare.getString(SHARE_LOGIN_USERID, "");
		bundle = getIntent().getExtras();
		initView();
		setListener();
		InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
		imm.toggleSoftInput(R.id.postContent, R.id.llpostblog);
		super.onStart();
	}

	private void initView() {
		isChecked = 1;
    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
    	//username = share.getString(SHARE_LOGIN_USERNAME, "");
		//userID = share.getString(SHARE_LOGIN_USERID, "");
		region =share.getString(SHARE_LOGIN_REGION, "");
		campus =share.getString(SHARE_LOGIN_SCHOOL, "");
		
		contentET.setText("我正在使用爱校园微博，汇聚 "+campus+" 的热点消息，你还没有？？？快快注册，加入我们校园先锋的行列吧！！！@校园活动微直播");
		
	}

	private OnClickListener postListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			InviteFriendTask inviteFriendTask = new InviteFriendTask();
			inviteFriendTask.execute();
		}
	};
    private OnClickListener cancelListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(inviteFriend.this, set.class);
			startActivity(intent);
			
		}
    	
    };
	private void setListener() {
		postBT.setOnClickListener(postListener);
		cancelBT.setOnClickListener(cancelListener);
	}

	class InviteFriendTask extends AsyncTask{

		private ProgressDialog proDialog;
		private String rtmsg;
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
			postContent = contentET.getText().toString();
			postTime = format.format(new Date());

			rtmsg = ConnUtil.transferBlog("postABlog", userID, postContent,
					imageUrl, "", "", postTime, location, isChecked);

			Log.e("PostBlogView.java cmt", userID +"; " + postContent +"; " +imageUrl +"; "
					+ postTime +"; " + location +"; "
					+ isChecked + "; " + rtmsg);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			
			proDialog.dismiss();

			if(rtmsg.contains("succeeded")){
				Toast.makeText(inviteFriend.this, "发布成功!",
					Toast.LENGTH_SHORT).show();
				InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(inviteFriend.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
			}
			else if(rtmsg.contains("failed"))
				Toast.makeText(inviteFriend.this, "服务器繁忙，请稍后再试。",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(inviteFriend.this, "网络断开，请检查您的网络连接。",
						Toast.LENGTH_SHORT).show();
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = ProgressDialog.show(inviteFriend.this, "连接中..", "连接中..请稍后....", true, true);
			
			super.onPreExecute();
		}
		
	}

}