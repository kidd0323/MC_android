package com.client.view;

import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.client.R;
import com.client.R.drawable;
import com.client.entity.AD;
import com.client.entity.Blog;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelView extends Activity {

	private final String SHARE_CHANNEL_TAG = "MAP_SHARE_CHANNEL_TAG";
	private final String SHARE_CHANNEL = "MAP_CHANNEL";
	private final String SHARE_CHANNELID = "MAP_CHANNELID";
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private Spinner channelSPN;
	private TextView ADTV;
	private Button refreshBT;
	private Button msgBT;
	private Button imgBT;
	private Button setupBT;
	private Button postBT;
	private ListView blogs;
	private ProgressDialog proDialog;
	private Handler messageHandler;  
	private String channel;
	private String channelID;
	private int channelIndex;
	private String blogID;
	private String authorID;
	private String blogContent;
	private String blogAuthor;
	private String blogAuthorID;
	private String photoUrl;
	private String blogTime;
	private Bundle bundle;
	private String sImgUrl;
	private String mImgUrl;
	private String oImgUrl;
	private String schoolID;
	private boolean threadDestropyFlag;
	private View footerView;
	private TextView more;
	private LinearLayout loading;
	private BlogLVAdapter blogLVAdapter;

	private int offset;
	private String loginState;

	private final int adWindowSize = 20;
	private final int windowSize = 50;
	private int rtNum = 0;

	private LoadBlogsHandler loadBlogsHandler;
	private Thread loadBlogsThread;
	private Thread loadMonitor;
	
	private int loadSum;
	private boolean loadCompleted;
	private boolean sufficient;
	private int loadRoundNum;
	private int loadedNum;
	private int reTryTimes;

	ArrayList<String> channels;
	ArrayList<String> channelIDs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channelui);
        SharedPreferences share = getSharedPreferences(SHARE_CHANNEL_TAG, 0);
        bundle = getIntent().getExtras();
        if(bundle != null)
        {
        	channel = bundle.getString("channel");
        }else{
        	channel = share.getString(SHARE_CHANNEL, "");
        }
		Log.e("debug channel init", channel);
		channelID = share.getString(SHARE_CHANNELID, "");
		findViewsById();
	}
	
	public void onStart(){
		if(null != proDialog)
			proDialog.dismiss();
    	offset = 0;
    	loadSum = 0;
    	reTryTimes = 0;
    	loadCompleted = true;
    	sufficient = true;
		SharedPreferences loginShare = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		schoolID = loginShare.getString(SHARE_LOGIN_SCHOOLID, "");
    	loginState = loginShare.getString(SHARE_LOGIN_STATE, "");
        Looper looper = Looper.myLooper();
        messageHandler = new ADMessageHandler(looper);
        loadBlogsHandler = new LoadBlogsHandler(looper);
		ArrayAdapter<String> channelAdapter;
    	initList();
    	channelAdapter = new ArrayAdapter<String>(this, R.layout.channelspinner, channels);
    	channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	channelSPN.setAdapter(channelAdapter);
    	channelIndex = channelIDs.indexOf(channel);
    	channelSPN.setSelection(channelIndex, true);
		initListView();
		initView(channel);
		setListener();
		super.onStart();
	}

	private void initList()
	{
		channels = new ArrayList<String>();
		channelIDs = new ArrayList<String>();
		channelIDs.add(0, "1");
		channelIDs.add(1, "2");
		channelIDs.add(2, "3");
		channelIDs.add(3, "4");
		channelIDs.add(4, "6");
		channelIDs.add(5, "7");
		channelIDs.add(6, "8");
		channels.add(0,"热点");
		channels.add(1,"公告");
		channels.add(2,"活动");
		channels.add(3,"美食");
		channels.add(4,"招聘");
		channels.add(5,"考试");
		channels.add(6,"讨论");
	}
	
	public void onDestroy(){
		
		if(null != loadBlogsThread)
			loadBlogsHandler.removeCallbacks(loadBlogsThread);
		
		//kill thread loadMonitor.
		loadSum = windowSize + 1;
		super.onDestroy();
	}
	
	public void onStop(){
		threadDestropyFlag = true;
		super.onStop();
	}

	private void setListener() {
		blogs.setOnItemClickListener(itemClickListener);
		refreshBT.setOnClickListener(refreshListener);
		msgBT.setOnClickListener(msgClickListener);
    	setupBT.setOnClickListener(setupListener);
    	imgBT.setOnClickListener(sceneListener);
    	postBT.setOnClickListener(postListener);
		channelSPN.setOnItemSelectedListener(channelItemSeletedListener);
	}

	private OnItemSelectedListener channelItemSeletedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			//kill thread loadMonitor.
			loadSum = windowSize + 1;
			if(null != loadBlogsThread)
				loadBlogsHandler.removeCallbacks(loadBlogsThread);
			
			blogLVAdapter.removeAll();
			if(blogs.getFooterViewsCount() == 0)
					blogs.addFooterView(footerView);
			else{
				blogs.removeFooterView(footerView);
				blogs.addFooterView(footerView);
				
			}
	    	blogs.setAdapter(blogLVAdapter);
			
			if(null != more)
				more.setVisibility(View.GONE);
			if(null != loading)
				loading.setVisibility(View.VISIBLE);
			
			if(proDialog != null)
				proDialog.dismiss();			
			channel =(String)channelSPN.getItemAtPosition(arg2);			
			channelID =(String)channelIDs.get(arg2);			
	    	//offset = 0;
	    	loadSum = 0;
	    	loadCompleted = true;
	    	sufficient = true;
			ChannelView.this.initView(channelID);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};

	private void findViewsById() {
		channelSPN = (Spinner)findViewById(R.id.channel);
		ADTV = (TextView)findViewById(R.id.AD);
		refreshBT = (Button)findViewById(R.id.refresh);
		msgBT = (Button)findViewById(R.id.message);
		imgBT = (Button)findViewById(R.id.image);
		setupBT = (Button)findViewById(R.id.setup);
		postBT = (Button)findViewById(R.id.post);
		blogs = (ListView)findViewById(R.id.blogs);
		footerView = LayoutInflater.from(this).inflate(R.layout.listfooterview, null);
	}
	
	private OnClickListener postListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0){
			Intent intent = new Intent();
			intent.setClass(ChannelView.this, PostBlogView.class);
			startActivity(intent);
		}
	};

    private void initView(String channelID){
		channel = channelID;
		SharedPreferences share = getSharedPreferences(SHARE_CHANNEL_TAG, 0);
		int channelIdx = channelIDs.indexOf(channel);
		String channelName = channelIDs.get(channelIdx);
		share.edit().putString(SHARE_CHANNEL, channelName);
		Log.e("channel initview",channel);
		offset = 0;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if("8".equals(channel)){
			postBT.setVisibility(View.VISIBLE);
			MarginLayoutParams mp = null;
			mp = new MarginLayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			if(DisplayMetrics.DENSITY_HIGH == dm.densityDpi){
				mp.setMargins(12, 0, 0, 0);
			}else if(DisplayMetrics.DENSITY_MEDIUM == dm.densityDpi){
				mp.setMargins(24, 0, 0, 0);
			}
			postBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
			msgBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
			imgBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
			setupBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
		}else{
			postBT.setVisibility(View.GONE);
			MarginLayoutParams mp = null;
			mp = new MarginLayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			if(DisplayMetrics.DENSITY_HIGH == dm.densityDpi){
				mp.setMargins(45, 0, 0, 0);
			}else if(DisplayMetrics.DENSITY_MEDIUM == dm.densityDpi){
				mp.setMargins(45, 0, 0, 0);
			}
			msgBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
			imgBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
			setupBT.setLayoutParams(new LinearLayout.LayoutParams(mp));
		}

		threadDestropyFlag = false;
		Thread ADThread = new Thread(new ADHandleThread());
		ADThread.start();
		loadMonitor = new Thread(new LoadMonitor());
		loadMonitor.start();
    }
    

    class LoadMonitor implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			loadSum = 0;
			loadRoundNum = 10;
			loadedNum = 0;
			while(true){
				if((loadSum > windowSize) || (!sufficient))
					break;
				if(loadCompleted && sufficient && (loadSum <= windowSize)){
					loadCompleted = false;
					loadBlogsThread = new Thread(new LoadBlogsHandleThread());
					loadBlogsThread.start();
				}
			}
			return;

		}
    	
    }

    private void initListView(){
    	
    	
    	ArrayList<HashMap<String,Object>> mData= new ArrayList<HashMap<String,Object>>();  
    	
    	blogLVAdapter = new BlogLVAdapter(this, mData,R.layout.bloglistui,
    			new String[]{"author","time","bigPhotoUrl","content","smallImgUrl", "commentNum","forwardNum"},
    			new int[]{R.id.cvBlogAuthor,R.id.time,R.id.bloglistphoto,R.id.content,R.id.blogImage, R.id.commentNum,
    			R.id.forwardNum});


		if(blogs.getFooterViewsCount() == 0)
				blogs.addFooterView(footerView);
		else{
			blogs.removeFooterView(footerView);
			blogs.addFooterView(footerView);
			
		}
    	blogs.setAdapter(blogLVAdapter);

		more = (TextView)findViewById(R.id.more);
		if(null != more)
			more.setOnClickListener(moreListener);
		loading = (LinearLayout)findViewById(R.id.loading);
		
		if(null != more)
			more.setVisibility(View.GONE);
		if(null != loading)
			loading.setVisibility(View.VISIBLE);
    	
    }

    private void loadData(){

    	
    	ArrayList<HashMap<String,Object>> mData= new ArrayList<HashMap<String,Object>>();  
    	String blogData = ConnUtil.getBlogInfo("getChannelBlogs", schoolID, channel, Integer.toString(offset), loadRoundNum, "");
    	Log.e("ChannelView.java loaddata", schoolID + " " + channel + " " + offset + " " + blogData);

    	if((!"error".equals(blogData)) && (!"exception".equals(blogData))){
	    	JsonUtil jsonUtil = new JsonUtil();
	    	ArrayList<Blog> blogInfo = jsonUtil.parseBlogListFromJson(blogData);
	    	if(null == blogInfo && (!"[]".equals(blogInfo)) && (!"".equals(blogInfo))){

	    		Log.e("channelview.java ", blogData + ": ");
				Message msg = new Message();
				msg.arg1 = 2;
				exceptionHandler.sendMessage(msg);
				return;
	    	}
	    		
	    	int num = blogInfo.size();
	    	rtNum  = num;
	    	for(int i = 0; i < num; i ++){
	    		HashMap<String,Object> item = new HashMap<String,Object>();
	    		item.put("blogID", blogInfo.get(i).getBlogID());
	    		item.put("author", blogInfo.get(i).getAuthor());
	    		item.put("authorID", blogInfo.get(i).getAuthorID());
	    		item.put("time", blogInfo.get(i).getReleaseTime());
	    		item.put("smallPhotoUrl", blogInfo.get(i).getSmallPhotoUrl());
	    		item.put("middlePhotoUrl", blogInfo.get(i).getMiddlePhotoUrl());
	    		item.put("bigPhotoUrl", blogInfo.get(i).getBigPhotoUrl());
	    		item.put("content", blogInfo.get(i).getContent());
	    		item.put("smallImgUrl", blogInfo.get(i).getSmallImgUrl());
	    		item.put("middleImgUrl", blogInfo.get(i).getMiddleImgUrl());
	    		item.put("originalImgUrl", blogInfo.get(i).getOriginalImgUrl());
	    		item.put("commentNum",  "评论: " + blogInfo.get(i).getCommentNum());
	    		item.put("forwardNum", "转发: " + blogInfo.get(i).getForwardNum());
	    		//item.put("device", "device: " + blogInfo.get(i).getDevice());
	    		mData.add(item);
	    	}

	    	if(num > 0)
		    	offset = offset + num;

    		Message blogsMessage = Message.obtain();
    		blogsMessage.obj = mData;
    		loadBlogsHandler.sendMessage(blogsMessage);

    	}else{
			Message msg = new Message();
			msg.arg1 = 1;
			exceptionHandler.sendMessage(msg);
    	}
    	
    	
    }
	

    private OnClickListener sceneListener =new OnClickListener()
    {
    	@Override
    	public void onClick (View arg0){

		proDialog = ProgressDialog.show(ChannelView.this, "连接中..", "连接中..请稍后....", true, true);
		Intent intent = new Intent();
		intent.setClass(ChannelView.this, picshow.class);
		startActivity(intent);
    	}
    };
    private OnClickListener setupListener =new OnClickListener()
    {
    	@Override
    	public void onClick (View arg0){
		proDialog = ProgressDialog.show(ChannelView.this, "连接中..", "连接中..请稍后....", true, true);
		Intent intent = new Intent();
		intent.setClass(ChannelView.this, set.class);
		startActivity(intent);

    	}
    };

	private OnClickListener moreListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {

			if(null != more)
				more.setVisibility(View.GONE);
			if(null != loading)
				loading.setVisibility(View.VISIBLE);
			loadMonitor = new Thread(new LoadMonitor());
			loadMonitor.start();

		}

	};

	private OnClickListener msgClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			proDialog = ProgressDialog.show(ChannelView.this, "读取中", "读取中,请稍后....", true, true);
			
	    	if("true".equals(loginState)){
				Intent intent = new Intent();
				intent.setClass(ChannelView.this, MsgDealView.class);
	    		startActivity(intent);
	    	}else{
	    		new AlertDialog.Builder(ChannelView.this).setTitle("").setCancelable(true).
	    		setMessage("请您先登录").setNeutralButton("登录", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
						intent.setClass(ChannelView.this, LoginView.class);
			    		startActivity(intent);
						
						
					}
				}).setNegativeButton("返回", null).show();
				//Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();
	    	}

		}
	};
	private OnClickListener refreshListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			
			SysUtil sysUtil = new SysUtil(ChannelView.this);
			Bundle bundle = new Bundle();
			bundle.putString("channel", channelID);
			sysUtil.refresh(ChannelView.class, bundle);

		}
	};
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e("ChannelView.java", blogs.getCount() + " " + arg2);

				int listBound;
				if(blogs.getFooterViewsCount() > 0)
					listBound = blogs.getCount() - 1;
				else
					listBound = blogs.getCount();
				if(arg2 < listBound){
					proDialog = ProgressDialog.show(ChannelView.this, "读取中", "读取中,请稍后....", true, true);
					HashMap<String,Object> item=(HashMap<String,Object>)blogs.getItemAtPosition(arg2);
					blogID = (String)item.get("blogID");
					authorID = (String)item.get("authorID");
					blogAuthor = (String)item.get("author");
					blogContent = (String)item.get("content");
					photoUrl = (String)item.get("bigPhotoUrl");
					sImgUrl = (String)item.get("smallImgUrl");
					mImgUrl = (String)item.get("middleImgUrl");
					oImgUrl = (String)item.get("originalImgUrl");
	
					if(blogs.getFooterViewsCount() > 0)
						blogs.removeFooterView(footerView);
					Intent intent = new Intent();
					intent.setClass(ChannelView.this, BlogView.class);
					Bundle bundle = new Bundle();
					bundle.putString("blogContent", blogContent);
					bundle.putString("blogID", blogID);
					bundle.putString("authorID", authorID);
					bundle.putString("blogAuthor", blogAuthor);
					bundle.putString("photoUrl", photoUrl);
					bundle.putString("blogTime", blogTime);
					bundle.putString("smallImgUrl", sImgUrl);
					bundle.putString("middleImgUrl", mImgUrl);
					bundle.putString("originalImgUrl", oImgUrl);
					bundle.putString("source", "channel");
					intent.putExtras(bundle);
		    		startActivity(intent);
		    		//建立下一个页面
				}else{
//					if(more != null)
//						more.setVisibility(View.GONE);
//					if(loading != null)
//						loading.setVisibility(View.VISIBLE);
				}
			}
	};

	class ADMessageHandler extends Handler{
		public ADMessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			
			ADTV.setText((String)msg.obj);
		}
		
	}
    class ADHandleThread implements Runnable{

		@Override
		public void run() {
			
	    	String ADData = ConnUtil.getADs("getPushedADs", adWindowSize,schoolID, "other");

	    	if((!"error".equals(ADData)) && (!"exception".equals(ADData))){
		    	JsonUtil jsonUtil = new JsonUtil();
		    	AD ads = (AD)jsonUtil.parseObjectFromJson(ADData, AD.class);
		    	if(ads == null)
		    		return;
		    	int num = ads.getADNum();
		    	int i = 0;
		    	for(i = 0;; i ++)
		    	{
		    		if(threadDestropyFlag)
		    			return;
		    			
		    		if(num > 0){
			    		Message message = Message.obtain();
			    		message.obj = ads.getADList().get(i%num);
			    		messageHandler.sendMessage(message);
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

	public class BlogLVAdapter extends BaseAdapter {

		private class viewHolder{
			private TextView authorTV;
			private ImageView photoIV;
			private TextView timeTV;
			private TextView contentTV;
			private ImageView imgIV;
			private TextView cmtNumTV;
			private TextView forwardNumTV;
			private TextView deviceTV;

		}
		
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private viewHolder holder;
		
		public BlogLVAdapter(Context mContext, ArrayList<HashMap<String, Object>> mList,
				int resource, String[] from, int[] to) {
			super();
			
			this.mList = mList;
			this.mContext = mContext;
			this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			keyString = new String[from.length];
			valueViewID = new int[to.length];
	        System.arraycopy(from, 0, keyString, 0, from.length);
	        System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}
	    public void addItems(ArrayList<HashMap<String,Object>> items){
	    	mList.addAll(items);
	        this.notifyDataSetChanged();
	    }

	    public void removeItem(int position){
	        mList.remove(position);
	        this.notifyDataSetChanged();
	    }

	    public void removeAll(){
	    	mList.clear();
	    }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView != null) {
	            holder = (viewHolder) convertView.getTag();
	        } else {
	            convertView = mInflater.inflate(R.layout.bloglistui, null);
	            holder = new viewHolder();
	            holder.authorTV = (TextView)convertView.findViewById(valueViewID[0]);
	            holder.timeTV = (TextView)convertView.findViewById(valueViewID[1]);
	            holder.photoIV = (ImageView)convertView.findViewById(valueViewID[2]);
	            holder.contentTV = (TextView)convertView.findViewById(valueViewID[3]);
	            holder.imgIV = (ImageView)convertView.findViewById(valueViewID[4]);
	            holder.cmtNumTV = (TextView)convertView.findViewById(valueViewID[5]);
	            holder.forwardNumTV = (TextView)convertView.findViewById(valueViewID[6]);
	            //holder.deviceTV = (TextView)convertView.findViewById(valueViewID[6]);
	            convertView.setTag(holder);
	        }
	        
	        HashMap<String, Object> appInfo = mList.get(position);
	        if (appInfo != null) {
	            
	    		String author = (String)appInfo.get(keyString[0]);
	    		String time = (String)appInfo.get(keyString[1]);
	    		String photoUrl = (String)appInfo.get(keyString[2]);
	    		String content = (String)appInfo.get(keyString[3]);
	    		final String url = (String)appInfo.get(keyString[4]);
	    		String cmtNum = (String)appInfo.get(keyString[5]);
	    		String forwardNum = (String)appInfo.get(keyString[6]);
	    		//String device = (String)appInfo.get(keyString[6]);
	    		holder.authorTV.setText(author);
	    		holder.timeTV.setText(time);
	    		holder.contentTV.setText(content);
	    		holder.cmtNumTV.setText(cmtNum);
	    		holder.forwardNumTV.setText(forwardNum);
	    		holder.photoIV.setTag(photoUrl);
	    		holder.imgIV.setTag(url);
	    		//holder.deviceTV.setText(device);
	    		
	    		holder.imgIV.setVisibility(View.GONE);
	    		//Log.e("ChannelView.java url", photoUrl);
	    		if("".equals(url)||url == null){
	    		}
	    		else{
	    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.photoIV, 50,   
	    					50, "photo");   
	    			BitmapManager.INSTANCE.loadBitmap(url, holder.imgIV, 50,   
	    					50, "image");   
//	    			new GetImageTask(holder.photoIV).execute(photoUrl);
//	    			new GetImageTask(holder.imgIV).execute(url);

	    		}

	        }        
	        return convertView;

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
				resultView.setImageDrawable(result);
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
		        Log.d("DrawImg", e.getMessage());  
		    }
		    if (drawable == null) {  
//		        Log.d("DrawImg", "BlogView null drawable");
//		        Resources r = ChannelView.this.getResources();
//		        drawable = r.getDrawable(R.drawable.ic_launcher);
		    }
		    return drawable ;  
		}

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
	
	

    class LoadBlogsHandleThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			loadData();

		}
    	
    }

	
	class LoadBlogsHandler extends Handler{
		public LoadBlogsHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

	    	blogLVAdapter.addItems((ArrayList<HashMap<String,Object>>)msg.obj);

			loadedNum = rtNum;
			loadSum += loadedNum;
			if(loadedNum < loadRoundNum)
				sufficient = false;
			else
				sufficient = true;
			if(loadRoundNum < 20)
				loadRoundNum += 10;
			
			if(!sufficient){
				if(blogs.getFooterViewsCount() > 0)
					blogs.removeFooterView(footerView);
			}
			else{
				if(loadSum >= windowSize){
					more.setVisibility(View.VISIBLE);
					loading.setVisibility(View.GONE);
				}
				loadCompleted = true;
			}
		}
		
	}


	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1){
				Toast.makeText(ChannelView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}

			if(msg.arg1 == 2){
				if(reTryTimes < 4){
					loadBlogsThread = new Thread(new LoadBlogsHandleThread());
					loadBlogsThread.start();
					reTryTimes ++;
				}else{
					reTryTimes = 0;
				}
			}
			return;
		}
	};


}
