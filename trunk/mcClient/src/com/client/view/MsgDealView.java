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
import com.client.R.layout;
import com.client.entity.Blog;
import com.client.entity.CommentMsgPackage;
import com.client.entity.Comments;
import com.client.entity.PrivateMsgPackage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.ChatView.LoadChatHandleThread;
import com.client.view.ChatView.LoadChatHandler;

import android.R.drawable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MsgDealView extends TabActivity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_NICKNAME = "MAP_LOGIN_NICKNAME";
	private String SHARE_LOGIN_PHOTOURL = "MAP_LOGIN_PHOTOURL";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SUCCESS = "succeeded";

	ArrayList<HashMap<String,Object>> commentData= new ArrayList<HashMap<String,Object>>();  
	ArrayList<HashMap<String,Object>> privateData= new ArrayList<HashMap<String,Object>>();  
	private ListView commentMsgLV;
	private ListView privateMsgLV;
	private Button refreshBT;
	private Button commentBT;
	private Button messageBT;
	private TextView displayTV;
	private TextView priToTV;
	protected TabHost tabHost;
	private String myCampus;
	private String msgID;
	private String userName;
	private String userID;
	private String rblogID;
	private String rblogAuthor;
	private String rblogAuthorID;
	private String rcmtAuthor;
	private String rcmtAuthorID;
	private String rtargetName;
	private String rtargetID;
	private String rcomment;
	private String rcmtTime;
	private String pmsgID;
	private String pauthor;
	private String pauthorID;
	private String ptime;
	private String pmsgContent;
	private int mode;
	private int itemPos;
	private String prefix;
	private int remainNum;
	private int cmtOffset;
	private CmtLVAdapter commentAdapter;
	private PrivateMsgLVAdapter privateAdapter;
	private ProgressDialog proDialog;

	private int cmtMsgOffset;
	private int priMsgOffset;

	private View cmtMsgFooterView;
	private TextView cmtMsgMore;
	private LinearLayout cmtMsgLoading;
	private View priMsgFooterView;
	private TextView priMsgMore;
	private LinearLayout priMsgLoading;
	
	private String herPhotoUrl;
	private String myPhotoUrl;
	private LoadCmtMsgHandler loadCmtMsgHandler;
	private LoadPriMsgHandler loadPriMsgHandler;
	Thread loadCmtMsgThread;
	Thread loadPriMsgThread;
	
	protected static Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final int windowSize = 20;
	private int cRtNum = 0;
	private int pRtNum = 0;
	private int jumpBack = 0;
	
	private int cReTryTimes;
	private int pReTryTimes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.messageui);
		findViewsById();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(null != proDialog)
			proDialog.dismiss();

		if(null != loadCmtMsgThread)
			loadCmtMsgHandler.removeCallbacks(loadCmtMsgThread);
		if(null != loadPriMsgThread)
			loadPriMsgHandler.removeCallbacks(loadPriMsgThread);
		
		super.onDestroy();
	}


	private void setListener() {
		// TODO Auto-generated method stub
		commentBT.setOnClickListener(cmtBtnClickListener);
		messageBT.setOnClickListener(msgBtnClickListener);
		commentMsgLV.setOnItemClickListener(cmtMsgClickListener);
		privateMsgLV.setOnItemClickListener(privateMsgClickListener);
		refreshBT.setOnClickListener(refreshListener);
	}

	private OnClickListener refreshListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			proDialog = ProgressDialog.show(MsgDealView.this, "刷新中..", "刷新中,请稍后...", true, true);
			SysUtil sysUtil = new SysUtil(MsgDealView.this);
			sysUtil.refresh(MsgDealView.class, null);

		}
	};

	public void onStart(){

		Log.e("MsgDealView.java", "onStart");
        mode = 0;
        itemPos = -1;
        cReTryTimes = 0;
        pReTryTimes = 0;
		if(null != proDialog)
			proDialog.dismiss();
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userID = share.getString(SHARE_LOGIN_USERID, "");
		userName = share.getString(SHARE_LOGIN_USERNAME, "");
		myPhotoUrl = share.getString(SHARE_LOGIN_PHOTOURL, "");
		myCampus = share.getString(SHARE_LOGIN_SCHOOL, "");
		
		initView();
		setListener();
		
        Looper looper = Looper.myLooper();
        loadCmtMsgHandler = new LoadCmtMsgHandler(looper);
        loadPriMsgHandler = new LoadPriMsgHandler(looper);
		
		super.onStart();
	}
	public void onResume(){

		if(jumpBack == 1){
			initView();
			setListener();
			
	        Looper looper = Looper.myLooper();
	        loadCmtMsgHandler = new LoadCmtMsgHandler(looper);
	        loadPriMsgHandler = new LoadPriMsgHandler(looper);
	        jumpBack = 0;
		}
		super.onResume();
	}
	
	private void findViewsById() {
		// TODO Auto-generated method stub

		commentMsgLV = (ListView)findViewById(R.id.commentMsgList);
		privateMsgLV = (ListView)findViewById(R.id.priavteMsgList);
		commentBT = (Button)findViewById(R.id.button_Cmt);
		messageBT = (Button)findViewById(R.id.button_Msg);
		refreshBT = (Button)findViewById(R.id.msgRefresh);
		displayTV = (TextView)findViewById(R.id.msgMyCampus);
		priToTV = (TextView)findViewById(R.id.priTo);
		cmtMsgFooterView = LayoutInflater.from(this).inflate(R.layout.cmtmsgfooterview, null);
		priMsgFooterView = LayoutInflater.from(this).inflate(R.layout.primsgfooterview, null);
		
	}

	private void initView() {
		// TODO Auto-generated method stub

		displayTV.setText(myCampus);
		setTitle("消息处理");
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("评论").setIndicator("",getResources().getDrawable(R.drawable.comment_b)).setContent(R.id.commentMsgTab));
		tabHost.addTab(tabHost.newTabSpec("消息").setIndicator("",getResources().getDrawable(R.drawable.message_b)).setContent(R.id.privateMsgTab));

		initListView();
		loadCmtMsgThread = new Thread(new LoadCmtMsgHandleThread());
		loadCmtMsgThread.start();
		loadPriMsgThread = new Thread(new LoadPriMsgHandleThread());
		loadPriMsgThread.start();
	}

	private void initListView() {
		// TODO Auto-generated method stub
		cmtMsgOffset = 0;
		priMsgOffset = 0;

		ArrayList<HashMap<String,Object>> commentData= new ArrayList<HashMap<String,Object>>();   
    	commentAdapter = new CmtLVAdapter(this,commentData,R.layout.commentmsglistui,
    			new String[]{"cmtAuthor","blogContent","blogTime","comment",
    			"commentTime","reply","bigPhotoUrl"},
    			new int[]{R.id.cmtMsgAuthor,R.id.cmtMsgBlog,R.id.blogTime,R.id.cmtMsgComment,
    			R.id.commentTime, R.id.reply, R.id.cmtmsgphoto}
    	);
		commentMsgLV.addFooterView(cmtMsgFooterView);
    	commentMsgLV.setAdapter(commentAdapter);
		cmtMsgMore = (TextView)findViewById(R.id.cmtmsgmore);
		cmtMsgLoading = (LinearLayout)findViewById(R.id.cmtmsgloading);
		if(cmtMsgMore != null)
			cmtMsgMore.setOnClickListener(cmtMsgMoreListener);
		if(null != cmtMsgMore)
			cmtMsgMore.setVisibility(View.GONE);
		if(null != cmtMsgLoading)
			cmtMsgLoading.setVisibility(View.VISIBLE);


		ArrayList<HashMap<String,Object>> privateData= new ArrayList<HashMap<String,Object>>();
    	privateAdapter = new PrivateMsgLVAdapter(this,privateData,R.layout.privatemsglistui,
    			new String[]{"author","priMsg","msg","priReply","releaseTime","bigPhotoUrl"},
    			new int[]{R.id.priMsgAuthor,R.id.priMsg,R.id.priMsgContent,
    			R.id.privateReply,R.id.priMsgTime, R.id.primsgphoto}
    	);    	
		privateMsgLV.addFooterView(priMsgFooterView);
    	privateMsgLV.setAdapter(privateAdapter);
		priMsgMore = (TextView)findViewById(R.id.primsgmore);
		priMsgLoading = (LinearLayout)findViewById(R.id.primsgloading);
		if(null != priMsgMore){
			priMsgMore.setOnClickListener(priMsgMoreListener);
			priMsgMore.setVisibility(View.GONE);
		}
		if(null != priMsgLoading)
			priMsgLoading.setVisibility(View.VISIBLE);


	}

	private void loadCmtData() {
		// TODO Auto-generated method stub
    	
    	String jsonData;
    	
    	jsonData = ConnUtil.getMsgs("getCommentMsgs", userID, Integer.toString(cmtMsgOffset), windowSize, "");
    	Log.e("MsgDealView.java loadcmtData", userID + " " + cmtMsgOffset + " " + windowSize + " " + jsonData);

    	int num;
    	JsonUtil jsonUtil = new JsonUtil();
    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){
	    	ArrayList<CommentMsgPackage> commentMsgs = jsonUtil.parseMsgListFromJson(jsonData, "comment");
	    	if(null == commentMsgs && (!"[]".equals(commentMsgs)) && (!"".equals(commentMsgs))){

				Message msg = new Message();
				msg.arg1 = 2;
				exceptionHandler.sendMessage(msg);
				return;
	    	}
	    	num = commentMsgs.size();
	    	cRtNum = num;
	    	for(int i = 0; i < num; i ++){
	    		HashMap<String,Object> item = new HashMap<String,Object>();
	    		
	    		item.put("msgID", commentMsgs.get(i).getMsgID());
	    		Blog cmtBlog = commentMsgs.get(i).getBlogObj();
	    		item.put("blogID", cmtBlog.getBlogID());
	    		item.put("blogAuthor", cmtBlog.getAuthor());
	    		item.put("blogAuthorID", cmtBlog.getAuthorID());
	    		item.put("blogContent", cmtBlog.getContent());
	    		item.put("sImgUrl", cmtBlog.getSmallImgUrl());
	    		item.put("mImgUrl", cmtBlog.getMiddleImgUrl());
	    		item.put("oImgUrl", cmtBlog.getOriginalImgUrl());
	    		item.put("blogTime", cmtBlog.getReleaseTime());
	    		item.put("bigPhotoUrl", commentMsgs.get(i).getBigPhotoUrl());
	    		item.put("cmtAuthor", commentMsgs.get(i).getCommentAuthor());
	    		item.put("cmtAuthorID", commentMsgs.get(i).getCommentAuthorID());
	    		item.put("targetUser", commentMsgs.get(i).getTargetUser());
	    		item.put("targetUserID", commentMsgs.get(i).getTargetUserID());
	    		item.put("comment", commentMsgs.get(i).getComment());
	    		item.put("commentTime", commentMsgs.get(i).getCommentTime());
	    		commentData.add(item);
	    	}
	    	if(num > 0)
		    	cmtMsgOffset = cmtMsgOffset + num;

    		Message cmtMsgMessage = Message.obtain();
    		cmtMsgMessage.obj = commentData;
    		loadCmtMsgHandler.sendMessage(cmtMsgMessage);

    	}else{

    		exceptionHandler.sendEmptyMessage(0);
    	}



	}

	private void loadPriData() {
		// TODO Auto-generated method stub
    	String jsonData;
    	
    	jsonData = ConnUtil.getMsgs("getPrivateMsgs", userID, Integer.toString(priMsgOffset), windowSize, "");
    	Log.e("MsgDealView.java loadpriData", userID + " " + priMsgOffset + " " + windowSize + " " + jsonData);

    	int num;
    	JsonUtil jsonUtil = new JsonUtil();
    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){

	    	ArrayList<PrivateMsgPackage> privateMsgs = jsonUtil.parseMsgListFromJson(jsonData, "private");
	
	    	if(null == privateMsgs && (!"[]".equals(privateMsgs)) && (!"".equals(privateMsgs))){

				Message msg = new Message();
				msg.arg1 = 2;
				exceptionHandler.sendMessage(msg);
				return;
	    	}
	    	num = privateMsgs.size();
	    	pRtNum = num;
	    	for(int i = 0; i < num; i ++){
	    		HashMap<String,Object> item = new HashMap<String,Object>();
	    		item.put("msgID", privateMsgs.get(i).getMsgID());
	    		item.put("author", privateMsgs.get(i).getAuthor());
	    		item.put("authorID", privateMsgs.get(i).getAuthorID());
	    		item.put("bigPhotoUrl", privateMsgs.get(i).getBigPhotoUrl());
	    		item.put("msg", privateMsgs.get(i).getMsg());
	    		item.put("releaseTime", privateMsgs.get(i).getReleaseTime());
	    		item.put("ErrorMsg", privateMsgs.get(i).getErrorMessage());
	    		privateData.add(item);
	    	}
	    	if(num > 0)
		    	priMsgOffset = priMsgOffset + num;

    		Message priMsgMessage = Message.obtain();
    		priMsgMessage.obj = privateData;
    		loadPriMsgHandler.sendMessage(priMsgMessage);


    	}else{

    		exceptionHandler.sendEmptyMessage(0);
    	}


	}

	private OnClickListener cmtBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			TabHost tb = getTabHost();
			tb.setCurrentTabByTag("评论");
		}
	};
	
	private OnClickListener msgBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			TabHost tb = getTabHost();
			tb.setCurrentTabByTag("消息");
		}
	};
	
	private OnClickListener cmtMsgMoreListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if(null != cmtMsgMore)
				cmtMsgMore.setVisibility(View.GONE);
			if(null != cmtMsgLoading)
				cmtMsgLoading.setVisibility(View.VISIBLE);
			loadCmtMsgThread = new Thread(new LoadCmtMsgHandleThread());
			loadCmtMsgThread.start();
		}

	};


	
	private OnClickListener priMsgMoreListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if(null != priMsgMore)
				priMsgMore.setVisibility(View.GONE);
			if(null != priMsgLoading)
				priMsgLoading.setVisibility(View.VISIBLE);
			loadPriMsgThread = new Thread(new LoadPriMsgHandleThread());
			loadPriMsgThread.start();
		}

	};



	private OnItemClickListener cmtMsgClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

	    		proDialog = ProgressDialog.show(MsgDealView.this, "连接中..", "连接中..请稍后....", true, true);

				int listBound;
				if(commentMsgLV.getFooterViewsCount() > 0)
					listBound = commentMsgLV.getCount() - 1;
				else
					listBound = commentMsgLV.getCount();
				if(arg2 < listBound){
			        jumpBack = 1;
					HashMap<String,Object> item=(HashMap<String,Object>)commentMsgLV.getItemAtPosition(arg2);
	
					String blogID = (String)item.get("blogID");
					String blogContent = (String)item.get("blogContent");
					String blogAuthor = (String)item.get("blogAuthor");
					String blogAuthorID = (String)item.get("blogAuthorID");
					String blogTime = (String)item.get("blogTime");
					String sImgUrl = (String)item.get("sImgUrl");
					String mImgUrl = (String)item.get("mImgUrl");
					String oImgUrl = (String)item.get("oImgUrl");
					String photoUrl = (String)item.get("bigPhotoUrl");
	
		    		
					Intent intent = new Intent();
					intent.setClass(MsgDealView.this, BlogView.class);
					Bundle bundle = new Bundle();
					bundle.putString("blogContent", blogContent);
					bundle.putString("blogID", blogID);
					bundle.putString("authorID", blogAuthorID);
					bundle.putString("blogAuthor", blogAuthor);
					bundle.putString("photoUrl", photoUrl);
					bundle.putString("blogTime", blogTime);
					bundle.putString("smallImgUrl", sImgUrl);
					bundle.putString("middleImgUrl", mImgUrl);
					bundle.putString("originalImgUrl", oImgUrl);
					bundle.putString("source", "message");
					intent.putExtras(bundle);
		    		startActivity(intent);
		    		//建立下一个页面
				}

			}
	};

	private OnItemClickListener privateMsgClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

	    		proDialog = ProgressDialog.show(MsgDealView.this, "连接中..", "连接中..请稍后....", true, true);

				int listBound;
				if(privateMsgLV.getFooterViewsCount() > 0)
					listBound = privateMsgLV.getCount() - 1;
				else
					listBound = privateMsgLV.getCount();
				if(arg2 < listBound){
			        jumpBack = 1;
					HashMap<String,Object> item=(HashMap<String,Object>)privateMsgLV.getItemAtPosition(arg2);
		    		
					String author = (String)item.get("author");
					String authorID = (String)item.get("authorID");
					herPhotoUrl = (String)item.get("bigPhotoUrl");
	
		    		
					Intent intent = new Intent();
					intent.setClass(MsgDealView.this, ChatView.class);
					Bundle bundle = new Bundle();
					bundle.putString("otherUserName", author);
					bundle.putString("otherUserID", authorID);
					bundle.putString("herPhotoUrl", herPhotoUrl);
					bundle.putString("myPhotoUrl", myPhotoUrl);
					bundle.putString("source", "message");
					intent.putExtras(bundle);
		    		startActivity(intent);
		    		//proDialog = ProgressDialog.show(HotChannel.this, "连接中..", "连接中..请稍后....", true, true);
					
		    		//建立下一个页面
				}

			}
	};
//	private OnClickListener toIndexListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			SysUtil mSysUtil= new SysUtil(MsgDealView.this);  
//			mSysUtil.jumpIndex();
//		}
//	};

	public class CmtLVAdapter extends BaseAdapter {

		private class viewHolder{
			private TextView authorTV;
			private TextView atTV;
			private TextView blogTV;
			private TextView blogTimeTV;
			private TextView cmtYouTV;
			private TextView commentTV;
			private TextView cmtTimeTV;
			private TextView replyBT;
			private ImageView cmtphotoIV;
			
		}
		
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private viewHolder holder;
		
		
		public CmtLVAdapter(Context mContext, ArrayList<HashMap<String, Object>> mList,
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
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	    public void removeItem(int position){
	    	if(getCount() == 0)
	    		return;
	        mList.remove(position);
	        this.notifyDataSetChanged();
	    }
	    public void addItems(ArrayList<HashMap<String,Object>> items){
	    	mList.addAll(items);
	        this.notifyDataSetChanged();
	    }

	    public void loadItems(ArrayList<HashMap<String, Object>> mList){
			this.mList = mList;
	        this.notifyDataSetChanged();
	    }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView != null) {
	            holder = (viewHolder) convertView.getTag();
	        } else {
	            convertView = mInflater.inflate(R.layout.commentmsglistui, null);
	            holder = new viewHolder();
	            holder.authorTV = (TextView)convertView.findViewById(valueViewID[0]);
	            holder.blogTV = (TextView)convertView.findViewById(valueViewID[1]);
	            holder.blogTimeTV = (TextView)convertView.findViewById(valueViewID[2]);
	            holder.commentTV = (TextView)convertView.findViewById(valueViewID[3]);
	            holder.cmtTimeTV = (TextView)convertView.findViewById(valueViewID[4]);
	            holder.replyBT = (TextView)convertView.findViewById(valueViewID[5]);
	            holder.cmtphotoIV = (ImageView)convertView.findViewById(valueViewID[6]);
	            convertView.setTag(holder);
	        }
	        
	        HashMap<String, Object> appInfo = mList.get(position);
	        if (appInfo != null) {
	            
	    		String author = (String)appInfo.get(keyString[0]);
	    		String blog = (String)appInfo.get(keyString[1]);
	    		String blogTime = (String)appInfo.get(keyString[2]);
	    		String comment = (String)appInfo.get(keyString[3]);
	    		String cmtTime = (String)appInfo.get(keyString[4]);
	    		String photoUrl = (String)appInfo.get(keyString[6]);
	    		holder.authorTV.setText(author);
	    		holder.blogTV.setText(blog);
	    		holder.blogTimeTV.setText(blogTime);
	    		holder.commentTV.setText(comment);
	    		holder.cmtTimeTV.setText(cmtTime);
	    		holder.replyBT.setOnClickListener(new replyListener(position));
	    		holder.cmtphotoIV.setTag(photoUrl);

    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.cmtphotoIV, 50,   
    					50);   
//    			new GetImageTask(holder.cmtphotoIV).execute(photoUrl);
	        }        
	        return convertView;

		}
		class replyListener implements OnClickListener {
	        private int position;

	        replyListener(int pos) {
	            position = pos;
	        }
	        
	        public void onClick(View v) {

	    		proDialog = ProgressDialog.show(MsgDealView.this, "连接中..", "连接中..请稍后....", true, true);
		    	if(commentAdapter.getCount() == 0)
		    		return;
	        	mode = 1;
	            int vid=v.getId();
	            if (vid == holder.replyBT.getId()){
	            	
	            	msgID = (String)commentData.get(position).get("msgID");
	            	rtargetName = (String)commentData.get(position).get("cmtAuthor");
	            	rtargetID = (String)commentData.get(position).get("cmtAuthorID");
	            	rblogID = (String)commentData.get(position).get("blogID");
	            	rblogAuthor = (String)commentData.get(position).get("blogAuthor");
	            	rblogAuthorID = (String)commentData.get(position).get("blogAuthorID");
	            	//replyET.setText("@" + rtargetName + ": ");
	            	itemPos = position;


					Intent intent = new Intent();
					intent.setClass(MsgDealView.this, CommentBlogView.class);

					Bundle bundle = new Bundle();
					bundle.putString("blogID", rblogID);
					bundle.putString("blogAuthorID", rblogAuthorID);
					bundle.putString("userID", userID);
					bundle.putString("targetID", rtargetID);
					bundle.putString("opeType", "replyUser");
					bundle.putString("source", "message");
					intent.putExtras(bundle);
		    		startActivity(intent);
						//cmtOffset = 0;
	            }

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
		    	String strMsg = e.getMessage();
		    	if(strMsg != null)
		        	Log.d("DrawImg", strMsg);  
		    }
		    if (drawable == null) {  
		        Log.e("DrawImg", "BlogView null drawable");
		        Resources r = MsgDealView.this.getResources();
		        drawable = r.getDrawable(R.drawable.ic_launcher);
		    }
		      
		    return drawable ;  
		}

	}

	public class PrivateMsgLVAdapter extends BaseAdapter {

		private class viewHolder{
			private TextView msgAuthorTV;
			private TextView priMsgTV;
			private TextView priMsgContent;
			private TextView priReply;
			private TextView priMsgTime;
			private ImageView priphotoIV;
			
		}
		
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private viewHolder holder;
		
		
		public PrivateMsgLVAdapter(Context mContext, ArrayList<HashMap<String, Object>> mList,
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
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	    public void removeItem(int position){
	    	if(getCount()==0)
	    		return;
	        mList.remove(position);
	        this.notifyDataSetChanged();
	    }
	    public void loadItems(ArrayList<HashMap<String, Object>> mList){
			this.mList = mList;
	        this.notifyDataSetChanged();
	    }
	    public void addItems(ArrayList<HashMap<String,Object>> items){
	    	mList.addAll(items);
	        this.notifyDataSetChanged();
	    }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView != null) {
	            holder = (viewHolder) convertView.getTag();
	        } else {
    			//new int[]{R.id.priMsgAuthor,R.id.priMsgContent,R.id.privateReply,R.id.priMsgTime}
	            convertView = mInflater.inflate(R.layout.privatemsglistui, null);
	            holder = new viewHolder();
	            holder.msgAuthorTV = (TextView)convertView.findViewById(valueViewID[0]);
	            holder.priMsgTV = (TextView)convertView.findViewById(valueViewID[1]);
	            holder.priMsgContent = (TextView)convertView.findViewById(valueViewID[2]);
	            holder.priReply = (TextView)convertView.findViewById(valueViewID[3]);
	            holder.priMsgTime = (TextView)convertView.findViewById(valueViewID[4]);
	            holder.priphotoIV = (ImageView)convertView.findViewById(valueViewID[5]);

	            convertView.setTag(holder);
	        }
	        
	        HashMap<String, Object> appInfo = mList.get(position);
	        if (appInfo != null) {
	            
	    		String msgAuthor = (String)appInfo.get(keyString[0]);
	    		String priMsgContent = (String)appInfo.get(keyString[2]);
	    		String priMsgTime = (String)appInfo.get(keyString[4]);
	    		String photoUrl = (String)appInfo.get(keyString[5]);
	    		holder.msgAuthorTV.setText(msgAuthor);
	    		holder.priMsgContent.setText(priMsgContent);
	    		holder.priMsgTime.setText(priMsgTime);
	    		holder.priReply.setOnClickListener(new PriReplyListener(position));
	    		holder.priphotoIV.setTag(photoUrl);

//    			new GetImageTask(holder.priphotoIV).execute(photoUrl);
    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.priphotoIV, 50,   
    					50);   
	        }        
	        return convertView;

		}
		class PriReplyListener implements OnClickListener {
	        private int position;

	        PriReplyListener(int pos) {
	            position = pos;
	        }
	        
	        public void onClick(View v) {

	    		proDialog = ProgressDialog.show(MsgDealView.this, "连接中..", "连接中..请稍后....", true, true);
	        	if(privateAdapter.getCount() == 0)
	        		return;
	        	mode = 2;
	            int vid=v.getId();
	            if (vid == holder.priReply.getId()){
	        		
	            	pmsgID = (String)privateData.get(position).get("msgID");
	            	pauthor = (String)privateData.get(position).get("author");
	            	pauthorID = (String)privateData.get(position).get("authorID");
	            	ptime = (String)privateData.get(position).get("releaseTime");
	            	pmsgContent = (String)privateData.get(position).get("msg");
	            	priToTV.setText("回复"+pauthor);
	            	itemPos = position;

	            	
	            }
	            	
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
		    	String strMsg = e.getMessage();
		    	if(strMsg != null)
		        	Log.d("DrawImg", strMsg);  
		    }
		    if (drawable == null) {  
		        Log.e("DrawImg", "BlogView null drawable");
		        Resources r = MsgDealView.this.getResources();
		        drawable = r.getDrawable(R.drawable.ic_launcher);
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
		//	placeholder = bmp;
		}

		public Bitmap getBitmapFromCache(String url) {
			if (cache.containsKey(url)) {
				return cache.get(url).get();
			}

			return null;
		}

		public void queueJob(final String url, final ImageView imageView,
				final int width, final int height) {
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
			//				imageView.setImageBitmap(placeholder);
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
				final int width, final int height) {
			imageViews.put(imageView, url);
			Bitmap bitmap = getBitmapFromCache(url);

			// check in UI thread, so no concurrency issues
			if (bitmap != null) {
				Log.d(null, "Item loaded from cache: " + url);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(bitmap);
			} else {
		//		imageView.setImageBitmap(placeholder);
				queueJob(url, imageView, width, height);
			}
		}

		private Bitmap downloadBitmap(String url, int width, int height) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						url).getContent());
		//		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
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
	
    class LoadCmtMsgHandleThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			loadCmtData();

		}
    	
    }

	
	class LoadCmtMsgHandler extends Handler{
		public LoadCmtMsgHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			commentAdapter.loadItems((ArrayList<HashMap<String,Object>>)msg.obj);

			if(cRtNum < windowSize){
				if(commentMsgLV.getFooterViewsCount() > 0)
		    		commentMsgLV.removeFooterView(cmtMsgFooterView);
			}
			else{
				cmtMsgMore.setVisibility(View.VISIBLE);
				cmtMsgLoading.setVisibility(View.GONE);
			}
		}
		
	}

    class LoadPriMsgHandleThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			loadPriData();

		}
    	
    }

	
	class LoadPriMsgHandler extends Handler{
		public LoadPriMsgHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			privateAdapter.loadItems((ArrayList<HashMap<String,Object>>)msg.obj);

			if(pRtNum < windowSize){
				if(privateMsgLV.getFooterViewsCount() > 0)
					privateMsgLV.removeFooterView(priMsgFooterView);
			}
			else{
				priMsgMore.setVisibility(View.VISIBLE);
				priMsgLoading.setVisibility(View.GONE);
			}
		}
		
	}

	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1){
				Toast.makeText(MsgDealView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}

			if(msg.arg1 == 2){

				if(cReTryTimes < 4){
					loadCmtMsgThread = new Thread(new LoadCmtMsgHandleThread());
					loadCmtMsgThread.start();
					cReTryTimes ++;
				}else
					cReTryTimes = 0;

				if(pReTryTimes < 4){
					loadPriMsgThread = new Thread(new LoadPriMsgHandleThread());
					loadPriMsgThread.start();
					pReTryTimes ++;
				}else
					pReTryTimes = 0;
			}
			return;
		}
	};

}
