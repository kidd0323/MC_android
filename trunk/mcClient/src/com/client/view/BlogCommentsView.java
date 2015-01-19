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
import com.client.view.ChannelView.LoadBlogsHandleThread;
import com.client.view.ChannelView.LoadBlogsHandler;

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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlogCommentsView extends Activity {



	/**回复选项*/
	 private final static byte ITEM_MENU_REPLY = 0x01;
	 /**转发选项*/
	 private final static byte ITEM_MENU_PRIVATEMSG = 0x02;


	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private String SHARE_LOGIN_NICKNAME = "MAP_LOGIN_NICKNAME";
	private String SHARE_LOGIN_PHOTOURL = "MAP_LOGIN_PHOTOURL";
	
	private ListView commentsLV;
	private Button commentBT;
	private ProgressDialog proDialog;
	private String userID;
	private String nickname;
	private String targetID;
	private String targetName;
	private String blogAuthor;
	private String blogAuthorID;
	private String blogID;
	Bundle bundle;
	private int offset;
	private int rtNum = 0;
	private CmtLVAdapter commentAdapter;
	private View footerView;
	private LinearLayout loading;
	private Button refreshBT;
	private TextView more;
	private final int windowSize = 20;
	private Comments comments;
	private String tempTargetName;
	private String tempTargetID;
	private String myPhotoUrl;
	private String herPhotoUrl;
	private String loginState;
	private int morePos;
	private int reTryTimes;
	
	private LoadCmtHandler loadCmtHandler;
	private Thread loadCmtThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.blogcommentui);
		findViewsById();
	}

	private void findViewsById() {
		// TODO Auto-generated method stub
		
		commentsLV = (ListView)findViewById(R.id.blogComments);
		commentBT = (Button)findViewById(R.id.commentBlogBT);
		footerView = LayoutInflater.from(this).inflate(R.layout.blogcmtfooterview, null);
		refreshBT = (Button)findViewById(R.id.blogCommentRefresh);
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		if(null != proDialog)
			proDialog.dismiss();

		reTryTimes = 0;
    	SharedPreferences lshare = getSharedPreferences(SHARE_LOGIN_TAG,0);
    	nickname = lshare.getString(SHARE_LOGIN_NICKNAME, "");
    	userID  = lshare.getString(SHARE_LOGIN_USERID, "");
    	myPhotoUrl = lshare.getString(SHARE_LOGIN_PHOTOURL, "");
    	loginState = lshare.getString(SHARE_LOGIN_STATE, "");
    	
		bundle = getIntent().getExtras();
		blogID = bundle.getString("blogID");
		blogAuthor = bundle.getString("blogAuthor");
		blogAuthorID = bundle.getString("authorID");
		targetID = blogAuthorID;

        Looper looper = Looper.myLooper();
		loadCmtHandler = new LoadCmtHandler(looper);
		initView();
		setListener();
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if(null != proDialog)
			proDialog.dismiss();

		if(null != loadCmtThread)
			loadCmtHandler.removeCallbacks(loadCmtThread);
		super.onDestroy();
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		offset = 0;
		initListView();
		loadCmtThread = new Thread(new LoadCmtHandleThread());
		loadCmtThread.start();
	}


	private void setListener() {
		// TODO Auto-generated method stub

		refreshBT.setOnClickListener(refreshListener);
		commentBT.setOnClickListener(commentListener);
		commentsLV.setOnCreateContextMenuListener(contextMenuListener);
		
	}

	private void initListView() {
		// TODO Auto-generated method stub


		commentsLV.setCacheColorHint(Color.TRANSPARENT);
		ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String,Object>>();  

    	commentAdapter = new CmtLVAdapter(this,mData,R.layout.commentlistui,
    			new String[]{"cmtAuthor","comment","time", "targetName", "bigPhotoUrl"},
    			new int[]{R.id.commentAuthor,R.id.commentContent,R.id.time,R.id.cmtTarget,
    			R.id.commentlistphoto}
    	);

		commentsLV.addFooterView(footerView);
    	commentsLV.setAdapter(commentAdapter);

		morePos = 0;
    	
		more = (TextView)findViewById(R.id.blogcmtmore);
		loading = (LinearLayout)findViewById(R.id.blogcmtloading);
		if(more != null)
			more.setOnClickListener(moreListener);

		if(null != more)
			more.setVisibility(View.GONE);
		if(null != loading)
			loading.setVisibility(View.VISIBLE);
	}

	private OnCreateContextMenuListener contextMenuListener = new OnCreateContextMenuListener() {
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			
	    	if("true".equals(loginState)){
				menu.add(Menu.NONE, ITEM_MENU_REPLY, 0,"回复");
				menu.add(Menu.NONE, ITEM_MENU_PRIVATEMSG, 0,"私信");
	    	}else{
	    		new AlertDialog.Builder(BlogCommentsView.this).setTitle("").setCancelable(true).
	    		setMessage("请您先登录").setNeutralButton("登录", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
						intent.setClass(BlogCommentsView.this, LoginView.class);
			    		startActivity(intent);
						
						
					}
				}).setNegativeButton("返回", null).show();
				//Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT).show();
	    	}
			 
		}
	};
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); 

    	Log.e("BlogCommentsView more Pos", menuInfo.position + " " + morePos);
    	
        if(menuInfo.position == morePos && commentsLV.getFooterViewsCount() > 0){
        	return false;
        }

		tempTargetName = comments.getAuthorList().get(menuInfo.position - (offset - rtNum));
		tempTargetID = comments.getAuthorIDList().get(menuInfo.position - (offset - rtNum));
		herPhotoUrl = comments.getBigPhotoUrlList().get(menuInfo.position - (offset - rtNum));

    	Log.e("BlogCommentsView more Pos", tempTargetName + " " + menuInfo.position +
    			" " + morePos);
        //menuInfo.position; //这个为选中的ListView中的条目ID
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
        switch(item.getItemId())
        {
			case ITEM_MENU_REPLY:


				proDialog = ProgressDialog.show(BlogCommentsView.this, "连接中..", "连接中..请稍后....", true, true);
				intent.setClass(BlogCommentsView.this, CommentBlogView.class);

				bundle.putString("blogID", blogID);
				bundle.putString("blogAuthorID", blogAuthorID);
				bundle.putString("userID", userID);
				bundle.putString("targetID", tempTargetID);
				bundle.putString("opeType", "replyUser");
				intent.putExtras(bundle);
	    		startActivity(intent);

			break;
			case ITEM_MENU_PRIVATEMSG:
				
				proDialog = ProgressDialog.show(BlogCommentsView.this, "连接中..", "连接中..请稍后....", true, true);
				intent = new Intent();
				intent.setClass(BlogCommentsView.this, ChatView.class);
				bundle.putString("otherUserName", tempTargetName);
				bundle.putString("otherUserID", tempTargetID);
				bundle.putString("herPhotoUrl", herPhotoUrl);
				bundle.putString("myPhotoUrl", myPhotoUrl);
				bundle.putString("source", "comment");
				intent.putExtras(bundle);
	    		startActivity(intent);
	    		
			break;
        }


		return super.onContextItemSelected(item);
	}

	private OnClickListener commentListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			proDialog = ProgressDialog.show(BlogCommentsView.this, "连接中..", "连接中..请稍后....", true, true);
			
			Intent intent = new Intent();
			intent.setClass(BlogCommentsView.this, CommentBlogView.class);
			Bundle bundle = new Bundle();
			bundle.putString("blogID", blogID);
			bundle.putString("blogAuthorID", blogAuthorID);
			bundle.putString("userID", userID);
			bundle.putString("targetID", targetID);
			bundle.putString("opeType", "replyBlog");
			intent.putExtras(bundle);
    		startActivity(intent);

			
		}
	};

	private OnClickListener refreshListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			proDialog = ProgressDialog.show(BlogCommentsView.this, "刷新中..", "刷新中,请稍后...", true, true);
			SysUtil sysUtil = new SysUtil(BlogCommentsView.this);
			sysUtil.refresh(BlogCommentsView.class, bundle);

		}
	};
	private OnClickListener moreListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {

			if(null != more)
				more.setVisibility(View.GONE);
			if(null != loading)
				loading.setVisibility(View.VISIBLE);
			loadCmtThread = new Thread(new LoadCmtHandleThread());
			loadCmtThread.start();
		}

	};
	

	/**
	 * 为了增加ListView中对回复的点击事件，需要对adapter进行重构。
	 * @author Bven
	 *
	 */
	public class CmtLVAdapter extends BaseAdapter {

		private class viewHolder{
			private TextView authorTV;
			private TextView cmtTV;
			private TextView timeTV;
			private TextView cmtTargetTV;
			private ImageView cmtPhotoIV;
			
		}
		
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		@SuppressWarnings("unused")
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView != null) {
	            holder = (viewHolder) convertView.getTag();
	        } else {
	            convertView = mInflater.inflate(R.layout.commentlistui, null);
	            holder = new viewHolder();
	            holder.authorTV = (TextView)convertView.findViewById(valueViewID[0]);
	            holder.cmtTV = (TextView)convertView.findViewById(valueViewID[1]);
	            holder.timeTV = (TextView)convertView.findViewById(valueViewID[2]);
	            holder.cmtTargetTV = (TextView)convertView.findViewById(valueViewID[3]);
	            holder.cmtPhotoIV = (ImageView)convertView.findViewById(valueViewID[4]);
	            convertView.setTag(holder);
	        }
	        
	        HashMap<String, Object> appInfo = mList.get(position);
	        if (appInfo != null) {
	            
	    		String author = (String)appInfo.get(keyString[0]);
	    		String comment = (String)appInfo.get(keyString[1]);
	    		String ttime = (String)appInfo.get(keyString[2]);
	    		String target = (String)appInfo.get(keyString[3]);
	    		String photoUrl = (String)appInfo.get(keyString[4]);
	    		holder.authorTV.setText(author);
	    		holder.cmtTV.setText(comment);
	    		holder.timeTV.setText(ttime);
	    		holder.cmtPhotoIV.setTag(photoUrl);
            	if("".equals(targetName))
            		holder.cmtTargetTV.setText("");
            	else
    	    		holder.cmtTargetTV.setText(": " + "回复" + target + ": ");
            	

	    		if("".equals(photoUrl)||photoUrl == null){
	    			//Log.e("BlogCommentsView.java photourl", photoUrl);
	    			holder.cmtPhotoIV.setVisibility(View.GONE);
	    		}
	    		else{
//	    			new GetImageTask(holder.cmtPhotoIV).execute(photoUrl);

	    			BitmapManager.INSTANCE.loadBitmap(photoUrl, holder.cmtPhotoIV, 50,   
	    					50, "photo");   
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
		    	String strMsg = e.getMessage();
		    	if(strMsg != null)
		        	Log.d("DrawImg", strMsg);  
		    }
		    if (drawable == null) {  
		        Log.e("DrawImg", "BlogView null drawable");
		        Resources r = BlogCommentsView.this.getResources();
		        drawable = r.getDrawable(R.drawable.ic_launcher);
		    }
		      
		    return drawable ;  
		}
		class replyListener implements OnClickListener {
	        private int position;

	        replyListener(int pos) {
	            position = pos;
	        }
	        
	        public void onClick(View v) {
	            int vid=v.getId();
	            	
	        }
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
	

	private void loadData() {
		// TODO Auto-generated method stub

		ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String,Object>>();  
    	String commentData = ConnUtil.getBlogComments("getBlogComments", blogID, Integer.toString(offset), windowSize, "");
    	
    	Log.e("BlogCommentsViewView.java loadData", blogID + " " + offset + " " + commentData);


    	if((!"error".equals(commentData)) && (!"exception".equals(commentData))){
	    	JsonUtil jsonUtil = new JsonUtil();
	    	comments = (Comments)jsonUtil.parseObjectFromJson(commentData, Comments.class);
	    	if(null == comments && (!"".equals(comments))){
				Message msg = new Message();
				msg.arg1 = 2;
				exceptionHandler.sendMessage(msg);
				return;
	    	}
	    	//Log.e("BlogView.java target", comments.getTargetIDList() + " " + comments.getTargetNameList());
	    	int num = comments.getCommentNum();
	    	rtNum = num;
	    	morePos += num;
	    	//userInfoTV.setText(comments.getCommentList().get(0)+"!");
	    	for(int i = 0; i < num; i ++){
	    		HashMap<String,Object> item = new HashMap<String,Object>();
	    		item.put("cmtAuthor", comments.getAuthorList().get(i));
	    		item.put("cmtAuthorID", comments.getAuthorIDList().get(i));
	    		item.put("comment", comments.getCommentList().get(i));
	    		item.put("commentID", comments.getCommentIDList().get(i));
	    		item.put("time", comments.getTimeList().get(i));
	    		item.put("targetName", comments.getTargetNameList().get(i));
	    		item.put("bigPhotoUrl", comments.getBigPhotoUrlList().get(i));
	    		mData.add(item);
	    	}
	
	    	if(num > 0)
		    	offset = offset + num;

    		Message cmtMessage = Message.obtain();
    		cmtMessage.obj = mData;
    		loadCmtHandler.sendMessage(cmtMessage);

    	}else{


			Message msg = new Message();
			msg.arg1 = 1;
			exceptionHandler.sendMessage(msg);
    	}
    	
	}
	

    class LoadCmtHandleThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			loadData();

		}
    	
    }

	
	class LoadCmtHandler extends Handler{
		public LoadCmtHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			commentAdapter.addItems((ArrayList<HashMap<String,Object>>)msg.obj);

			if(rtNum < windowSize){
				if(commentsLV.getFooterViewsCount() > 0)
					commentsLV.removeFooterView(footerView);
			}
			else{
				more.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
			}
		}
		
	}

	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {

			if(msg.arg1 == 1){
				Toast.makeText(BlogCommentsView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}

			if(msg.arg1 == 2){
				if(reTryTimes < 4){
					loadCmtThread = new Thread(new LoadCmtHandleThread());
					loadCmtThread.start();
					reTryTimes ++;
				}else{
					reTryTimes = 0;
				}
			}
			return;
		}
	};
}
