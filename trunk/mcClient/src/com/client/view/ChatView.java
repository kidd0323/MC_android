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
import com.client.entity.ChatMessage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.view.BlogCommentsView.BitmapManager;
import com.client.view.BlogCommentsView.LoadCmtHandleThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ChatView extends Activity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_NICKNAME = "MAP_LOGIN_NICKNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private ListView chatLV;
	private EditText chatContentET;
	private Button chatBT;
	private String userName;
	private String userID;
	private String otherUserName;
	private String otherUserID;
	private String chatContent;
	private String chatTime;
	ChatLVAdapter chatLVAdapter;
	private String herPhotoUrl;
	private String myPhotoUrl;
	private Bundle bundle;
	private String source;
	protected static Format format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	

	private View chatHeaderView;
	private TextView chatRecordMore;
	private LinearLayout chatRecordLoading;
	private int offset;

	private final int windowSize = 20;

	private int chatRecordRtNum;
	private LoadChatHandler loadChatHandler;
	private ChatHandler chatHandler;
	private Thread loadChatThread;
	private Thread chatThread;
	private int reTryTimes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatui);
		findViewsById();
	}
	
	public void onStart(){

    	offset = 0;
    	reTryTimes = 0;
		initView();
        Looper looper = Looper.myLooper();
        loadChatHandler = new LoadChatHandler(looper);
        chatHandler = new ChatHandler(looper);
		setListener();
		super.onStart();
	}
	private void setListener() {
		// TODO Auto-generated method stub
		chatBT.setOnClickListener(chatClickListener);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(null != loadChatThread)
			loadChatHandler.removeCallbacks(loadChatThread);
		if(null != chatThread)
			chatHandler.removeCallbacks(chatThread);

		super.onDestroy();
	}

	private void initView() {
		// TODO Auto-generated method stub

		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		userName = share.getString(SHARE_LOGIN_NICKNAME, "");
		userID = share.getString(SHARE_LOGIN_USERID, "");
		chatLV.setCacheColorHint(Color.TRANSPARENT);
		chatLV.setAlwaysDrawnWithCacheEnabled(true);
		bundle = getIntent().getExtras();
		otherUserName = bundle.getString("otherUserName");
		otherUserID = bundle.getString("otherUserID");
		herPhotoUrl = bundle.getString("herPhotoUrl");
		myPhotoUrl = bundle.getString("myPhotoUrl");
		source = bundle.getString("source");
		
		initListView();
		loadChatThread = new Thread(new LoadChatHandleThread());
		loadChatThread.start();
		
	}
	
	private void findViewsById() {
		// TODO Auto-generated method stub
		chatLV = (ListView)findViewById(R.id.chatList);
		chatContentET = (EditText)findViewById(R.id.chatContent);
		chatBT = (Button)findViewById(R.id.chatSubmit);
		chatHeaderView = LayoutInflater.from(this).inflate(R.layout.chatheaderview, null);
		
	}
	

    private void initListView(){
	    	ArrayList<HashMap<String,Object>> mData= new ArrayList<HashMap<String,Object>>(); 
	    	
	    	chatLVAdapter = new ChatLVAdapter(this, mData,R.layout.chatrecordlistui,
	    			new String[]{"layout","from","msg", "releaseTime", "photoUrl"},
	    			new int[]{R.id.chatRecordLayout,R.id.chatRecordAuthor,R.id.chatRecordContent,
	    			R.id.chatTime, R.id.chatphoto, R.id.titlelayout, R.id.chatphotoMe});
	    	
	    	chatLV.addHeaderView(chatHeaderView);
	    	chatLV.setAdapter(chatLVAdapter);
	    	
	    	chatRecordMore = (TextView)findViewById(R.id.chatRecordMore);
	    	chatRecordLoading = (LinearLayout)findViewById(R.id.chatLoading);
	
			if(chatRecordMore != null)
				chatRecordMore.setOnClickListener(chatRecordMoreListener);

			if(null != chatRecordMore)
				chatRecordMore.setVisibility(View.GONE);
			if(null != chatRecordLoading)
				chatRecordLoading.setVisibility(View.VISIBLE);
	    	
	    	
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && null != source && source.equals("message"))
		{
			source = "";
			finish();
			Intent intent = new Intent();
			intent.setClass(ChatView.this, MsgDealView.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else{
		}
		return super.onKeyDown(keyCode, event);
	}
	private OnClickListener chatRecordMoreListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if(null != chatRecordMore)
				chatRecordMore.setVisibility(View.GONE);
			if(null != chatRecordLoading)
				chatRecordLoading.setVisibility(View.VISIBLE);
			loadChatThread = new Thread(new LoadChatHandleThread());
			loadChatThread.start();
		}

	};

	private OnClickListener chatClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			chatContent = chatContentET.getText().toString();
			
			if(!"".equals(chatContent.trim())){

				chatThread = new Thread(new ChatThread());
				chatThread.start();
			}
			else
				Toast.makeText(ChatView.this, "请输入内容!",
						Toast.LENGTH_SHORT).show();
			
		}
			

	};

	
	public class ChatLVAdapter extends BaseAdapter {

		private class viewHolder{

			private LinearLayout recordLayout;
			private LinearLayout titleLayout;
			private TextView recordAuthor;
			private TextView recordContent;
			private TextView chatTime;
			private ImageView photoIV;
			private ImageView photoMeIV;
			
		}
		
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private viewHolder holder;
		
		
		public ChatLVAdapter(Context mContext, ArrayList<HashMap<String, Object>> mList,
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
	    public void addItems(ArrayList<HashMap<String,Object>> items){
	    	mList.addAll(items);
	        this.notifyDataSetChanged();
	    }
	    public void addItem(HashMap<String, Object> item){
	    	mList.add(item);
	        this.notifyDataSetChanged();
	    }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView != null) {
	            holder = (viewHolder) convertView.getTag();
	        } else {
    			//new int[]{R.id.priMsgAuthor,R.id.priMsgContent,R.id.privateReply,R.id.priMsgTime}
	            convertView = mInflater.inflate(R.layout.chatrecordlistui, null);
	            holder = new viewHolder();
	            holder.titleLayout = (LinearLayout)convertView.findViewById(valueViewID[5]);
	            holder.recordLayout = (LinearLayout)convertView.findViewById(valueViewID[0]);
	            holder.recordAuthor = (TextView)convertView.findViewById(valueViewID[1]);
	            holder.recordContent = (TextView)convertView.findViewById(valueViewID[2]);
	            holder.chatTime = (TextView)convertView.findViewById(valueViewID[3]);
	            holder.photoIV = (ImageView)convertView.findViewById(valueViewID[4]);
	            holder.photoMeIV = (ImageView)convertView.findViewById(valueViewID[6]);

	            convertView.setTag(holder);
	        }
	        
	        HashMap<String, Object> appInfo = mList.get(position);
	        if (appInfo != null) {
	            
	    		String recordAuthor = (String)appInfo.get(keyString[1]);
	    		String recordContent = (String)appInfo.get(keyString[2]);
	    		String chatTime = (String)appInfo.get(keyString[3]);
	    		String photoUrl = (String)appInfo.get(keyString[4]);
	    		holder.recordAuthor.setText(recordAuthor);
	    		holder.recordContent.setText(recordContent);
	    		holder.chatTime.setText(chatTime);
	    		LinearLayout.LayoutParams authorLp = new LinearLayout.LayoutParams
	    		(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    		LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams
	    		(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    		LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams
	    		(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    		LinearLayout.LayoutParams photoLp = new LinearLayout.LayoutParams
	    		(50,50);
//    			authorLp.leftMargin = 5;
//    			authorLp.rightMargin = 70;
//    			authorLp.topMargin = 5;
//    			contentLp.leftMargin = 10;
//    			contentLp.topMargin = 5;
//    			contentLp.rightMargin = 50;
//    			timeLp.rightMargin = 5;
//    			timeLp.leftMargin = 5;
//    			timeLp.topMargin = 3;
//    			photoLp.rightMargin = 80;
//    			photoLp.leftMargin = 5;
//    			photoLp.topMargin = 3;
	    		holder.photoIV.setVisibility(View.GONE);
	    		holder.photoMeIV.setVisibility(View.GONE);

	    		if(recordAuthor.equals(userName)){
	    			authorLp.gravity = Gravity.RIGHT;
	    			contentLp.gravity = Gravity.RIGHT;
	    			timeLp.gravity = Gravity.RIGHT;
	    			photoLp.gravity = Gravity.RIGHT;
	    			holder.titleLayout.setGravity(Gravity.RIGHT);
		    		holder.photoMeIV.setVisibility(View.VISIBLE);
		    		holder.photoMeIV.setTag(photoUrl);
	    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.photoMeIV, 50,   
	    					50);   
	    		}
	    		else{
	    			authorLp.gravity = Gravity.LEFT;
	    			contentLp.gravity = Gravity.LEFT;
	    			timeLp.gravity = Gravity.LEFT;
	    			photoLp.gravity = Gravity.LEFT;
	    			holder.titleLayout.setGravity(Gravity.LEFT);
		    		holder.photoIV.setVisibility(View.VISIBLE);
		    		holder.photoIV.setTag(photoUrl);
	    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.photoIV, 50,   
	    					50);   
	    		}

//    			new GetImageTask(holder.photoIV).execute(photoUrl);
	    		holder.recordAuthor.setLayoutParams(authorLp);
	    		holder.recordContent.setLayoutParams(contentLp);
	    		holder.chatTime.setLayoutParams(timeLp);
	    		//holder.photoIV.setLayoutParams(photoLp);
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
		    	String strMsg = e.getMessage();
		    	if(strMsg != null)
		        	Log.d("DrawImg", strMsg);  
		    }
		    if (drawable == null) {  
		        Log.e("DrawImg", "BlogView null drawable");
		        Resources r = ChatView.this.getResources();
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
			//placeholder = bmp;
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
							//imageView.setImageBitmap(placeholder);
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
				//imageView.setImageBitmap(placeholder);
				queueJob(url, imageView, width, height);
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
	
    private void loadData(){
    	
    	String jsonData = ConnUtil.getPrivateRecords("getPrivateRecords", userID, otherUserID, Integer.toString(offset), windowSize, "");
    	Log.e("ChatView.java loadData", userID+" "+otherUserID + " " + offset + " " + jsonData);
    	ArrayList<HashMap<String,Object>> mData= new ArrayList<HashMap<String,Object>>(); 

    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){
	
	
	    	JsonUtil jsonUtil = new JsonUtil();
	    	ArrayList<ChatMessage> chatList = jsonUtil.parseChatListFromJson(jsonData);
	    	if(null == chatList && (!"[]".equals(chatList)) && (!"".equals(chatList))){

				Message msg = new Message();
				msg.arg1 = 2;
				exceptionHandler.sendMessage(msg);

	    		return;
	    	}
	    	int num = chatList.size();
	    	chatRecordRtNum = num;
	    	for(int i = 0; i < num; i ++){
	    		HashMap<String,Object> item = new HashMap<String,Object>();
	    		item.put("msgID", chatList.get(i).getMsgID());
	    		item.put("from", chatList.get(i).getFrom());
	    		item.put("fromID", chatList.get(i).getFromID());
	    		item.put("to", chatList.get(i).getTo());
	    		item.put("toID", chatList.get(i).getToID());
	    		item.put("msg", chatList.get(i).getMsg());
	    		item.put("releaseTime", chatList.get(i).getReleaseTime());
	    		if(chatList.get(i).getFromID().equals(userID))
	    			item.put("photoUrl", myPhotoUrl);
	    		else
	    			item.put("photoUrl", herPhotoUrl);
	    			
	    		mData.add(item);
	    	}
	    	
	    	//Log.e("go to hell", mData.toString());
	    	
	    	if(num > 0)
		    	offset = offset + num;

    		Message chatMessage = Message.obtain();
    		chatMessage.obj = mData;
    		loadChatHandler.sendMessage(chatMessage);
		    	
	    	//}
    	}else{
			Message msg = new Message();
			msg.arg1 = 1;
			exceptionHandler.sendMessage(msg);
    	}


	    	
    }

    class ChatThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			chatTime = format.format(new Date());
			
			String msgID = ConnUtil.sendPrivateMsg("sendPrivateMsg", userID, userName, otherUserID, 
					otherUserName, chatContent, chatTime, 3, "");

    		Message chatMessage = Message.obtain();
    		chatMessage.obj = msgID;
    		chatHandler.sendMessage(chatMessage);


		}
    	
    }

	
	class ChatHandler extends Handler{
		public ChatHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			String msgID = (String)msg.obj;
    		HashMap<String,Object> item = new HashMap<String,Object>();
    		item.put("msgID", msgID);
    		item.put("from", userName);
    		item.put("fromID", userID);
    		item.put("to", otherUserName);
    		item.put("toID", otherUserID);
    		item.put("msg", chatContent);
    		item.put("releaseTime", chatTime);
			item.put("photoUrl", myPhotoUrl);
    		chatLVAdapter.addItem(item);
    		chatContentET.setText("");
    		chatLV.setSelection(chatLV.getCount() - 1);

			Toast.makeText(ChatView.this, "回复成功!",
					Toast.LENGTH_SHORT).show();
		}
		
	}
    class LoadChatHandleThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			loadData();

		}
    	
    }

	
	class LoadChatHandler extends Handler{
		public LoadChatHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			chatLVAdapter.addItems((ArrayList<HashMap<String,Object>>)msg.obj);

			if(chatRecordRtNum < windowSize){
				if(chatLV.getHeaderViewsCount() > 0)
					chatLV.removeHeaderView(chatHeaderView);
			}
			else{
				chatRecordMore.setVisibility(View.VISIBLE);
				chatRecordLoading.setVisibility(View.GONE);
			}
		}
		
	}

	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1){
				Toast.makeText(ChatView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}

			if(msg.arg1 == 2){
				if(reTryTimes < 4){
					loadChatThread = new Thread(new LoadChatHandleThread());
					loadChatThread.start();
					reTryTimes ++;
				}else{
					reTryTimes = 0;
				}
			}
			return;
		}
	};
}
