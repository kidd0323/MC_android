package com.client.view;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.client.R;
import com.client.util.ConnUtil;
import com.client.util.SysUtil;
import com.client.view.LoginView.LoginTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CommentBlogView extends Activity {

	private EditText contentET;
	private Button commentBT;
	Bundle bundle;
	private String userID;
	private String targetID;
	private String source;
	private String blogAuthorID;
	private String blogID;
	private String cmtTime;
	private String opeType;
	private String commentContent;
	protected static Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private String successMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.commentblogui);

		findViewsById();
	}

	private void findViewsById() {
		contentET = (EditText)findViewById(R.id.commentContent);
		commentBT = (Button)findViewById(R.id.finalcommentBT);
	}

	@Override
	protected void onStart() {

		bundle = getIntent().getExtras();
		blogID = bundle.getString("blogID");
		blogAuthorID = bundle.getString("blogAuthorID");
		targetID = bundle.getString("targetID");
		userID = bundle.getString("userID");
		opeType = bundle.getString("opeType");
		source = bundle.getString("source");
		if("replyBlog".equals(opeType)){
			successMsg = "评论成功!";
			commentBT.setBackgroundResource(R.drawable.button_comment_b);
		}
		else if("replyUser".equals(opeType)){
			successMsg = "回复成功!";
			commentBT.setBackgroundResource(R.drawable.button_replyb);
		}
		else{
			successMsg = "转发成功!";
			commentBT.setBackgroundResource(R.drawable.button_forward_b);
		}
		setListener();
		InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
		imm.toggleSoftInput(R.id.commentContent, R.id.llcommentblog);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
			imm.hideSoftInputFromWindow(CommentBlogView.this.getCurrentFocus().getWindowToken(), 0);
		}

		if(null != source && source.equals("message"))
		{
			source = "";
			finish();
			Intent intent = new Intent();
			intent.setClass(CommentBlogView.this, MsgDealView.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListener() {
		commentBT.setOnClickListener(commentListener);
	}

	private OnClickListener commentListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//proDialog = ProgressDialog.show(CommentBlogView.this, "连接中..", "连接中..请稍后....", true, true);

			CommentTask commentTask = new CommentTask();
			commentTask.execute();
		}
	};
	
	class CommentTask extends AsyncTask{
		
		private ProgressDialog proDialog;
		private String rtmsg;

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			commentContent = contentET.getText().toString();
			cmtTime = format.format(new Date());
			rtmsg = ConnUtil.commentBlog("commentBlog", blogID, blogAuthorID,
					userID, targetID, commentContent, cmtTime, -1, "");
			Log.e("CommentBlogView.java cmt", blogID +"; " + blogAuthorID +
					"; " +userID +"; " + targetID +"; " + rtmsg);

			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			proDialog.dismiss();
			Log.e("CommentBlogView return value", rtmsg);
			if(rtmsg.contains("succeeded")){
				InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(CommentBlogView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				Toast.makeText(CommentBlogView.this, "发布成功!",
					Toast.LENGTH_SHORT).show();
				finish();
			}
			else if(rtmsg.contains("failed"))
				Toast.makeText(CommentBlogView.this, "服务器繁忙，请稍后再试。",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(CommentBlogView.this, "网络断开，请检查您的网络连接。",
						Toast.LENGTH_SHORT).show();
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = ProgressDialog.show(CommentBlogView.this, "连接中..", "连接中..请稍后....", true, true);
			
			super.onPreExecute();
		}
		
	}

}
