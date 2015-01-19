package com.client.view;

import com.client.R;
import com.client.entity.LoginPackage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView extends Activity {
	private String SHARE_LOGIN_REGION = "MAP_LOGIN_REGION";
	private String SHARE_LOGIN_REGIONID = "MAP_LOGIN_REGIONID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	
	final String OK_MSG = "succeeded";
	private String userName;
	private String userID;
	private String nickname;
	private String password;
	private String photoUrl;
	private String loginState;

	private String campusID;
	private String campus;
	private String region;
	private String regionID;
	private String sessionID;
	String strResult; 

	/** 以下是UI */
	private EditText usernameET;
	private EditText passwordET;
	private Button submitBT;
	private Button registerBT;
	private CheckBox rememberMeCB;
	

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_NICKNAME = "MAP_LOGIN_NICKNAME";
	private String SHARE_LOGIN_PHOTOURL = "MAP_LOGIN_PHOTOURL";
	
	private SharedPreferences lshare;


	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	
	/** 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */
	private boolean isNetError;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginui);
        
        /**The following code block is prepared for the SDK 3.0 or higher, 
         * more strict on the disk access and network visit
        */
        int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        if(8 < currentapiVersion)
        {
	        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
	        .penaltyLog()
	        .build());
	        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	        .detectAll()
	        .penaltyLog()
	        .penaltyDeath()
	        .build());
        }
        findViewsById();
    }
    
    public void onStart(){
//    	if(null != proDialog)
//    		proDialog.dismiss();
        isNetError = false;
        initView();
        setListener();
        super.onStart();
    }
    
    private void findViewsById(){
    	usernameET = (EditText)findViewById(R.id.usernameEditText);
    	passwordET = (EditText)findViewById(R.id.passwordEditText);
    	submitBT = (Button)findViewById(R.id.submit);
    	rememberMeCB = (CheckBox)findViewById(R.id.rememberMeCB);
    	registerBT = (Button)findViewById(R.id.toRegister);
    }
    
    private void initView(){
    	lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    	
    	String username = lshare.getString(SHARE_LOGIN_USERNAME, "");
    	String password = lshare.getString(SHARE_LOGIN_PASSWORD, "");
    	String state = lshare.getString(SHARE_LOGIN_STATE, "");
    	
    	if("false".equals(state)){
    		username = "";
    		password = "";
    	}
    	
    	if(!"".equals(username)){
    		usernameET.setText(username);
    	}
		if (!"".equals(password)) {
			passwordET.setText(password);
		}
		rememberMeCB.setChecked(true);
    }

    private void setListener(){
    	submitBT.setOnClickListener(submitListener);
    	registerBT.setOnClickListener(registerListener);
    	usernameET.setOnKeyListener(usernameChangeListener);
    }
    
    private OnKeyListener usernameChangeListener = new OnKeyListener(){
		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
			passwordET.setText("");
			rememberMeCB.setChecked(true);
			return false;
		}
    };
    
    private OnClickListener submitListener = new OnClickListener(){
    	public void onClick(View v){
			LoginTask loginTask = new LoginTask();
			loginTask.execute();
    	}
    };

    private OnClickListener registerListener = new OnClickListener(){
    	public void onClick(View v){
			clearSharePassword();
			Intent intent = new Intent();
			intent.setClass(LoginView.this, web.class);
//			Bundle bundle = new Bundle();
//			bundle.putString("MAP_USERNAME", username);
//			intent.putExtras(bundle);
			// 转向登陆后的页面
			startActivity(intent);

    	}
    };
    
    class LoginTask extends AsyncTask{
    	/** 登录loading提示框 */
    	private ProgressDialog proDialog;
    	private String jsonData;

		@Override
		protected Object doInBackground(Object... arg0) {
    		isNetError = false;
    		userName = usernameET.getText().toString();
			password = passwordET.getText().toString();
			jsonData = ConnUtil.validateAccount(userName, password);
			Log.e("LoginView.java submitListener", userName + " " + jsonData);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(!"error".equals(jsonData)){
				LoginPackage loginPackage = (LoginPackage)new JsonUtil().parseObjectFromJson(jsonData, LoginPackage.class);
				if(null == loginPackage){

					Message msg = new Message();
					msg.arg1 = 2;
					exceptionHandler.sendMessage(msg);
					loginState = "false";
					return;
				}
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
				lshare.edit().putString(SHARE_LOGIN_USERID, userID).commit();
				if (!rememberMeCB.isChecked()) {
					clearSharePassword();
				}
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
				Toast.makeText(LoginView.this, "登录失败，请检查账号是否异常", Toast.LENGTH_SHORT);
			}
			
			if(OK_MSG.equals(loginState)){
				if (isRememberMe()) {
					saveSharePreferences(true, true);
				} else {
					saveSharePreferences(true, false);
				}
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
					intent.setClass(LoginView.this, MyCampusView.class);
				}else{
					SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
					share.edit().putString(SHARE_LOGIN_USERID, userID).commit();
					share.edit().putString(SHARE_LOGIN_REGION, region).commit();
					share.edit().putString(SHARE_LOGIN_REGIONID, regionID).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOL, campus).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOLID, campusID).commit();
					share.edit().putString(SHARE_LOGIN_STATE, "true").commit();
					share = null;
					intent.setClass(LoginView.this, BlocksView.class);
				}
				LoginView.this.finish();
				startActivity(intent);
			} else {

				lshare.edit().putString(SHARE_LOGIN_STATE, "false").commit();
				// 如果不是网络错误
				if (!isNetError) {
					clearSharePassword();
				}
			}

			proDialog.dismiss();
			Log.e("LoginView.java", loginState+" " + loginState.equals("failed"));
			if(loginState.equals("failed")){
				Toast.makeText(LoginView.this, "用户名或密码错误，请检查输入", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
    		proDialog = ProgressDialog.show(LoginView.this, "", "正在验证，请稍后...",true, true);
			super.onPreExecute();
		}
    	
    }
    
    
    private boolean isRememberMe(){
    	return rememberMeCB.isChecked();
    }
    
    private void saveSharePreferences(boolean saveUsername, boolean savePassword){
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUsername) {
			share.edit().putString(SHARE_LOGIN_USERNAME, usernameET.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit().putString(SHARE_LOGIN_PASSWORD, passwordET.getText().toString()).commit();
		}
		//share.edit().putString(SHARE_LOGIN_SCHOOL, "Peking University").commit();
		share = null;
    	
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
				Toast.makeText(LoginView.this, "登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(LoginView.this, "登陆失败,请输入正确的用户名和密码!",
						Toast.LENGTH_SHORT).show();
				// 清除以前的SharePreferences密码
				clearSharePassword();
			}
			isNetError = false;
			return;
		}
	};
	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 2){
				Toast.makeText(LoginView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}
			return;
		}
	};
	
}