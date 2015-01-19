package com.client.view;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.client.R;
import com.client.util.ConnUtil;
import com.client.view.CommentBlogView.CommentTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ForwardView extends Activity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	
	private EditText contentET;
	private Button forwardBT;
	private CheckBox cmtTooCB;
	Bundle bundle;
	private String userID;
	private String blogAuthorID;
	private String blogID;
	private String forwardTime;
	private String forwardContent;
	private String location;
	private String imageUrl;
	protected static Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private int isChecked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.forwardui);

		findViewsById();
	}

	private void findViewsById() {
		
		contentET = (EditText)findViewById(R.id.forwardContent);
		forwardBT = (Button)findViewById(R.id.forwardBT);
		cmtTooCB = (CheckBox)findViewById(R.id.cmtToo);
	}
	

	@Override
	protected void onStart() {
		

    	SharedPreferences lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    	userID  = lshare.getString(SHARE_LOGIN_USERID, "");
		bundle = getIntent().getExtras();
		blogID = bundle.getString("blogID");
		blogAuthorID = bundle.getString("blogAuthorID");
		initView();
		setListener();
		InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
		imm.toggleSoftInput(R.id.commentContent, R.id.llcommentblog);
		super.onStart();
	}

	private void initView() {
		
		cmtTooCB.setChecked(true);
		isChecked = 1;
	}

	private OnClickListener forwardListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			ForwardTask forwardTask = new ForwardTask();
			forwardTask.execute();
		}
	};
	private void setListener() {
		
		forwardBT.setOnClickListener(forwardListener);
	}

	class ForwardTask extends AsyncTask{
		
		private ProgressDialog proDialog;
		private String rtmsg;

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			forwardContent = contentET.getText().toString();
			forwardTime = format.format(new Date());
			if(cmtTooCB.isChecked())
				isChecked = 1;
			else
				isChecked = 0;

			Log.e("ForwardView.java cmt", userID +"; " + forwardContent +"; " +imageUrl +"; "
					+ blogID +"; " + blogAuthorID +"; " +forwardTime +"; " + location +"; "
					+ isChecked + "; ");

			rtmsg = ConnUtil.transferBlog("postABlog", userID, forwardContent,
					imageUrl, blogID, blogAuthorID, forwardTime, location, isChecked);
			Log.e("ForwardView.java transfer blog", rtmsg);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			proDialog.dismiss();
			if(rtmsg.contains("succeeded")){
				Toast.makeText(ForwardView.this, "发布成功!",
					Toast.LENGTH_SHORT).show();
				contentET.setText("");
				InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(ForwardView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
			}
			else if(rtmsg.contains("failed"))
				Toast.makeText(ForwardView.this, "服务器繁忙，请稍后再试。",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(ForwardView.this, "网络断开，请检查您的网络连接。",
						Toast.LENGTH_SHORT).show();
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = ProgressDialog.show(ForwardView.this, "连接中..", "连接中..请稍后....", true, true);
			
			super.onPreExecute();
		}
		
	}
}
