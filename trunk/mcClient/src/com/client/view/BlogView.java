package com.client.view;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.client.R;
import com.client.R.drawable;
import com.client.entity.Comments;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.ChannelView.BitmapManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BlogView extends Activity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private String SHARE_LOGIN_PHOTOURL = "MAP_LOGIN_PHOTOURL";
	
	private String blogID;
	private String blogContent;
	private String blogAuthor;
	private String blogAuthorID;
	private String blogTime;
	private String photoUrl;
	private String username;
	private String userID;
	private TextView authorTV;
	private TextView contentTV;
	private TextView timeTV;
	private ImageView mImgIV;
	private ImageView photoIV;
	private Button forwardBT;
	private Button commentBT;
	private ProgressDialog proDialog;
	private String school;
	private String comment;
	private String cmtTime;
	private String sImgUrl;
	private String mImgUrl;
	private String oImgUrl;
	private String myPhotoUrl;
	private String source = "";
	Bundle bundle;
	
	private String loginState;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.blogui);
		findViewsById();
	}
	public void onStart(){
		if(null != proDialog)
			proDialog.dismiss();
		bundle = getIntent().getExtras();
		blogID = bundle.getString("blogID");
		blogAuthor = bundle.getString("blogAuthor");
		blogAuthorID = bundle.getString("authorID");
		photoUrl = bundle.getString("photoUrl");
		blogContent = bundle.getString("blogContent");
		blogTime = bundle.getString("blogTime");
		sImgUrl = bundle.getString("smallImgUrl");
		mImgUrl = bundle.getString("middleImgUrl");
		oImgUrl = bundle.getString("originalImgUrl");
		source = bundle.getString("source");

		initView();
		setListener();
		super.onStart();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if(null != proDialog)
			proDialog.dismiss();
		super.onDestroy();
	}
	private void setListener() {
		
		mImgIV.setOnClickListener(imgClickListener);
		authorTV.setOnClickListener(authorClickListener);
		forwardBT.setOnClickListener(forwardClickListener);
		commentBT.setOnClickListener(commentListener);
		photoIV.setOnClickListener(authorClickListener);
	}

	private OnClickListener forwardClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

	    	if("true".equals(loginState)){
				proDialog = ProgressDialog.show(BlogView.this, "连接中..", "连接中..请稍后....", true, true);
				Intent intent = new Intent();
				intent.setClass(BlogView.this, ForwardView.class);
				Bundle bundle = new Bundle();
				bundle.putString("blogID", blogID);
				bundle.putString("blogAuthorID", blogAuthorID);
				intent.putExtras(bundle);
	
	    		startActivity(intent);

	    	}else{
	    		new AlertDialog.Builder(BlogView.this).setTitle("").setCancelable(true).
	    		setMessage("请您先登录").setNeutralButton("登录", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
						intent.setClass(BlogView.this, LoginView.class);
			    		startActivity(intent);
						
						
					}
				}).setNegativeButton("返回", null).show();
	    	}
			
		}
	};

	private OnClickListener authorClickListener = new OnClickListener() {	
		@Override
		public void onClick(View arg0) {
	    	if("true".equals(loginState)){
				proDialog = ProgressDialog.show(BlogView.this, "连接中..", "连接中..请稍后....", true, true);
				Intent intent = new Intent();
				intent.setClass(BlogView.this, ChatView.class);
				Bundle bundle = new Bundle();
				bundle.putString("otherUserName", blogAuthor);
				bundle.putString("otherUserID", blogAuthorID);
				bundle.putString("herPhotoUrl", photoUrl);
				bundle.putString("myPhotoUrl", myPhotoUrl);
				intent.putExtras(bundle);
	    		startActivity(intent);

	    		//proDialog = ProgressDialog.show(HotChannel.this, "连接中..", "连接中..请稍后....", true, true);
	    		//建立下一个页面
	    	}

		}
	};

	private OnClickListener imgClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			proDialog = ProgressDialog.show(BlogView.this, "连接中..", "连接中..请稍后....", true, true);
			
			Intent intent = new Intent();
			intent.setClass(BlogView.this, OriginalImgView.class);
			Bundle bundle = new Bundle();
			bundle.putString("originalImgUrl", oImgUrl);
			intent.putExtras(bundle);
    		startActivity(intent);


		}
	};


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && null != source && source.equals("picshow"))
		{
			source = "";
			finish();
			Intent intent = new Intent();
			intent.setClass(BlogView.this, picshow.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return false;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK && source.equals("message"))
		{
			source = "";
			finish();
			
			Intent intent = new Intent();
			intent.setClass(BlogView.this, MsgDealView.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		return false;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	
	private OnClickListener commentListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	    	/*if("true".equals(loginState)){
	    	}else{
	    		new AlertDialog.Builder(BlogView.this).setTitle("").setCancelable(true).
	    		setMessage("请您先登录").setNeutralButton("登录", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
						intent.setClass(BlogView.this, LoginView.class);
			    		startActivity(intent);

					}
				}).setNegativeButton("返回", null).show();
				//Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();
	    	}*/

			proDialog = ProgressDialog.show(BlogView.this, "连接中..", "连接中..请稍后....", true, true);
			Intent intent = new Intent();
			intent.setClass(BlogView.this, BlogCommentsView.class);
			Bundle bundle = new Bundle();
			bundle.putString("blogID", blogID);
			bundle.putString("blogAuthor", blogAuthor);
			bundle.putString("authorID", blogAuthorID);
			bundle.putString("opeType", "replyBlog");
			intent.putExtras(bundle);
    		startActivity(intent);
		}
	};


	private void initView() {
		

    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);

    	username = share.getString(SHARE_LOGIN_USERNAME, "");
    	userID = share.getString(SHARE_LOGIN_USERID, "");
    	school = share.getString(SHARE_LOGIN_SCHOOL, "");
    	loginState = share.getString(SHARE_LOGIN_STATE, "");
    	myPhotoUrl = share.getString(SHARE_LOGIN_PHOTOURL, "");

		authorTV.setText(blogAuthor);
		contentTV.setText(blogContent);
		timeTV.setText(blogTime);
		mImgIV.setVisibility(View.GONE);
		BitmapManager.INSTANCE.loadBitmap(photoUrl, photoIV, 50, 50, "photo");

		if("".equals(mImgUrl) || mImgUrl==null){
			mImgIV.setVisibility(View.GONE);
		}
		else{

			BitmapManager.INSTANCE.loadBitmap(mImgUrl, mImgIV, 50, 50, "image");
			//new GetImageTask(mImgIV).execute(mImgUrl);

		}
			
	}


	private void findViewsById() {
		

		authorTV = (TextView)findViewById(R.id.author);
		contentTV = (TextView)findViewById(R.id.content);
		forwardBT = (Button)findViewById(R.id.forward);
		commentBT = (Button)findViewById(R.id.comment);
		timeTV = (TextView)findViewById(R.id.time);
		mImgIV = (ImageView)findViewById(R.id.blogMImg);
		photoIV = (ImageView)findViewById(R.id.blogphoto);
	}
	

	public enum BitmapManager {
		INSTANCE;

		private final Map<String, SoftReference<Bitmap>> cache;
		private final ExecutorService pool;
		private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
		//private Bitmap placeholder;

		BitmapManager() {
			cache = new HashMap<String, SoftReference<Bitmap>>();
			pool = Executors.newFixedThreadPool(5);
		}

		public void setPlaceholder(Bitmap bmp) {
			//placeholder = bmp;
		}

		public Bitmap getBitmapFromCache(String url) {
			if (cache.containsKey(url)) {
				return cache.get(url).get();
			}

			return null;
		}

		public void queueJob(final String url, final ImageView imageView,
				final int width, final int height, final String type) {
			/* Create handler in UI thread. */
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					String tag = imageViews.get(imageView);
					if (tag != null && tag.equals(url)) {
						if (msg.obj != null) {
							imageView.setVisibility(View.VISIBLE);
							imageView.setImageBitmap((Bitmap) msg.obj);
						} else {
							if("photo".equals(type))
								imageView.setImageResource(drawable.default_head);
							if("image".equals(type))
								imageView.setImageResource(drawable.loading);
							Log.d(null, "fail " + url);
						}
					}
				}
			};

			pool.submit(new Runnable() {
				@Override
				public void run() {
					final Bitmap bmp = downloadBitmap(url, width, height);
					Message message = Message.obtain();
					message.obj = bmp;
					Log.d(null, "Item downloaded: " + url);

					handler.sendMessage(message);
				}
			});
		}

		public void loadBitmap(final String url, final ImageView imageView,
				final int width, final int height, final String type) {
			imageViews.put(imageView, url);
			Bitmap bitmap = getBitmapFromCache(url);

			// check in UI thread, so no concurrency issues
			if (bitmap != null) {
				Log.d(null, "Item loaded from cache: " + url);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(bitmap);
			} else {
				if("photo".equals(type))
					imageView.setImageResource(drawable.default_head);
				if("image".equals(type))
					imageView.setImageResource(drawable.loading);
				queueJob(url, imageView, width, height, type);
			}
		}

		private Bitmap downloadBitmap(String url, int width, int height) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						url).getContent());
				//bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
				cache.put(url, new SoftReference<Bitmap>(bitmap));
				return bitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	


	private class GetImageTask extends AsyncTask<String, Void, Drawable>{
		private ImageView resultView;
		

		public GetImageTask(ImageView resultView) {

			this.resultView = resultView;
		}
		@Override
		protected Drawable doInBackground(String... arg0) {
			
			return loadImageFromNetwork(arg0[0]);
		}
		protected void onPostExecute(Drawable result){
			if(null != resultView){
				resultView.setVisibility(View.VISIBLE);
				resultView.setImageDrawable(result);
			}
		}
		
	}
	private Drawable loadImageFromNetwork(String imageUrl)  
	{  
	    Drawable drawable = null;  
	    try {  
	        // 可以在这里通过文件名来判断，是否本地有此图片  
	        drawable = Drawable.createFromStream(
	                new URL(imageUrl).openStream(), "sImage.jpg");  
	    } catch (IOException e) {
	    	String strMsg = e.getMessage();
	    	if(strMsg != null)
	        	Log.d("DrawImg", strMsg);  
	    }
	    if (drawable == null) {  
	        Log.e("DrawImg", "BlogView null drawable");
	        Resources r = this.getBaseContext().getResources();
	        //drawable = r.getDrawable(R.drawable.ic_launcher);
	    }
	      
	    return drawable ;  
	}
}
