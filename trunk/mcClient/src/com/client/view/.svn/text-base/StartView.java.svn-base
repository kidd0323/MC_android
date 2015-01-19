package com.client.view;

import com.client.R;
import com.client.entity.LoginPackage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class StartView extends Activity {

	final String OK_MSG = "succeeded";
	
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_NICKNAME = "MAP_LOGIN_NICKNAME";
	private String SHARE_LOGIN_PHOTOURL = "MAP_LOGIN_PHOTOURL";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	
	private String SHARE_LOGIN_REGION = "MAP_LOGIN_REGION";
	private String SHARE_LOGIN_REGIONID = "MAP_LOGIN_REGIONID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	
	private boolean isNetError;
	private SharedPreferences lshare;
	
	private String userName;
	private String userID;
	private String photoUrl;
	private String loginState;
	private String nickname;
	private String campusID;
	private String campus;
	private String region;
	private String regionID;
	//private boolean threadDestropyFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startui);
		int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        if(8 < currentapiVersion)
        {
        	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()   // or .detectAll() for all detectable problems
            .penaltyLog()
            .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectAll()
            .penaltyLog()
            .penaltyDeath()
            .build());
        }
	}
	public void onStart(){
		LoginTask loginTask = new LoginTask();
		loginTask.execute();
		super.onStart();
	}
	public void onDestroy(){
		super.onDestroy();
	}
    
    class LoginTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... arg0) {

    		isNetError = false;
    		lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    		String userName = lshare.getString(SHARE_LOGIN_USERNAME, "");
    		String password = lshare.getString(SHARE_LOGIN_PASSWORD, "");
			if(password.equals(""))
			{
				StartView.this.finish();
				Intent intent = new Intent();
				intent.setClass(StartView.this, LoginView.class);
				startActivity(intent);
				return null;
			}
			else
			{
				String jsonData = "";
				jsonData = ConnUtil.validateAccount(userName, password);
				Log.e("LoginView.java submitListener", userName + " " + jsonData);
				if(!"error".equals(jsonData)){
					LoginPackage loginPackage = (LoginPackage)new JsonUtil().parseObjectFromJson(jsonData, LoginPackage.class);
					loginState = loginPackage.getErrorMessage();
					userID = loginPackage.getUserID();
					nickname = loginPackage.getNickname();
					campus = loginPackage.getCampus();
					campusID = loginPackage.getCampusID();
					region = loginPackage.getRegion();
					regionID = loginPackage.getRegionID();
					photoUrl = loginPackage.getBigPhotoUrl();
		    		lshare.edit().putString(SHARE_LOGIN_STATE, "true").commit();
					lshare.edit().putString(SHARE_LOGIN_PHOTOURL, photoUrl).commit();
					lshare.edit().putString(SHARE_LOGIN_NICKNAME, nickname).commit();
				}else{
					if("error".equals(jsonData))
						isNetError = true;
					else
						isNetError = false;
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("isNetError", isNetError);
					message.setData(bundle);
					loginHandler.sendMessage(message);
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(OK_MSG.equals(loginState)){
				lshare.edit().putString(SHARE_LOGIN_STATE, "true").commit();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("MAP_USERNAME", userName);
				bundle.putString("MAP_NICKNAME", nickname);
				bundle.putString("MAP_USERID", userID);
				bundle.putString("MAP_CAMPUS", campus);
				bundle.putString("MAP_CAMPUSID", campusID);
				bundle.putString("MAP_REGION", region);
				bundle.putString("MAP_REGIONID", regionID);
				intent.putExtras(bundle);
				// 转向登陆后的页面
				if(null == campusID || "0".equals(campusID)){
					intent.setClass(StartView.this, MyCampusView.class);
				}else{
					SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
					share.edit().putString(SHARE_LOGIN_USERID, userID).commit();
					share.edit().putString(SHARE_LOGIN_REGION, region).commit();
					share.edit().putString(SHARE_LOGIN_REGIONID, regionID).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOL, campus).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOLID, campusID).commit();
					share.edit().putString(SHARE_LOGIN_STATE, "true").commit();
					share = null;
					intent.setClass(StartView.this, BlocksView.class);
				}
				startActivity(intent);
			} else {

				lshare.edit().putString(SHARE_LOGIN_STATE, "false").commit();
				// 如果不是网络错误
				if (!isNetError) {
					clearSharePassword();
				}
				Intent intent = new Intent();
				intent.setClass(StartView.this, LoginView.class);
				startActivity(intent);
			}
			StartView.this.finish();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
    }
    
    private void clearSharePassword(){
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
    }
    
    Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (isNetError){
				Toast.makeText(StartView.this, "登录失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(StartView.this, "登录失败,请输入正确的用户名和密码!",
						Toast.LENGTH_SHORT).show();
				clearSharePassword();
			}
			isNetError = false;
			return;
		}
	};

}
