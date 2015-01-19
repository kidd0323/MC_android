package com.client.view;

import java.util.Date;

import com.client.R;
import com.client.R.id;
import com.client.R.layout;
import com.client.util.ConnUtil;
import com.client.util.SysUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CommentView extends Activity {

	private String blogID;
	private String blogContent;
	private String blogAuthor;
	private String blogAuthorID;
	private String blogTime;
	private String targetID;
	private String targetName;
	private EditText commentET;
	private Button submitBT;
	private String userName;
	private String password;
	private String cmtContent;
	private String userID;
	private boolean isNetError;
	private ProgressDialog proDialog;

	private final int COMMENT_TYPE = 1;
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.commentui);
		isNetError = false;
		Bundle bundle = getIntent().getExtras();
		blogID = bundle.getString("blogID");
		blogContent = bundle.getString("blogContent");
		blogAuthorID = bundle.getString("authorID");
		blogAuthor = bundle.getString("blogAuthor");
		blogTime = bundle.getString("blogTime");
		targetID = bundle.getString("targetID");
		targetName = bundle.getString("targetName");

		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userName = share.getString(SHARE_LOGIN_USERNAME, "");
		
		findViewsById();
		setListener();
	}


	private void setListener() {
		submitBT.setOnClickListener(submitListener);
		//cancelBT.setOnClickListener(cancelListener);
	}

	private OnClickListener cancelListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			CommentView.this.finish();

		}
	};
	private OnClickListener submitListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
	    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG,0);
	    	String userID = share.getString(SHARE_LOGIN_USERID, "");
			cmtContent = commentET.getText().toString();
			String commentTime = new Date().toString();
			
	    	String cmtResult = new ConnUtil().commentBlog("commentBlog", blogID, blogAuthorID
	    			, userID, targetID,
	    			cmtContent, commentTime, COMMENT_TYPE, "");

	    	if("true".equals(cmtResult)){
	    		CommentView.this.finish();
	    	}else {
	    		
	    		isNetError = true;
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				commentHandler.sendMessage(message);
			}
		}
	};

	private void findViewsById() {
		// TODO Auto-generated method stub
		commentET = (EditText)findViewById(R.id.blogComment);
		submitBT = (Button)findViewById(R.id.cmtSubmit);
		//cancelBT = (Button)findViewById(R.id.cmtCancel);
	}
	
	Handler commentHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (isNetError) {
				Toast.makeText(CommentView.this, "评论失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

}
