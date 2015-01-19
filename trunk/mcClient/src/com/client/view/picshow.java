package com.client.view;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;



import com.client.R;
import com.client.entity.Blog;
import com.client.entity.Comments;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.ChannelView.ADMessageHandler;
import com.client.view.ChannelView.BlogLVAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;


//TODO: 解决ViewSwitcher多重实例的问题。
public class picshow extends Activity {
	private final String SHARE_CHANNEL_TAG = "MAP_SHARE_CHANNEL_TAG";
	private final String SHARE_CHANNEL = "MAP_CHANNEL";
	private final String SHARE_CHANNELID = "MAP_CHANNELID";
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private TextView channelTV;
	private TextView ADTV;
	private Button refreshBT;
	private Button msgBT;
	private Button imgBT;
	private Button setupBT;
	private Button backBT;
	private ImageSwitcher imgIS;
	private Gallery imgG;
	private ListView blogs;
	private ProgressDialog proDialog;
	private Handler messageHandler;  
	private String channel;
	private String channelID;
	private String blogID;
	private String authorID;
	private String blogContent;
	private Bundle bundle;
	private String sImgUrl;
	private String mImgUrl;
	private String blogAuthor;
	private String blogAuthorID;
	private String blogTime;
	private String oImgUrl;
	private String schoolID;
	private boolean threadDestropyFlag;
	private View footerView;
	private TextView more;
	private LinearLayout loading;
	private BlogLVAdapter blogLVAdapter;
	private TextView picauthorTV;
	private TextView pictimeTV;
	private TextView piccommentTV;
	private int windowsize = 10;

	private int offset = 0;
	private String loginState;
	public String []sImages=new String[10];
	public String []mImages=new String[10];
	public String []oImages=new String[10];
	public String oImage;

	private int rtNum = 1;
	private int curblogInfoID;
	private ArrayList<Blog> curblogInfo;
	
	private String curblogID;
	private String curauthorID;
	private String curblogAuthor;
	private String curblogContent ;
	private String cursImgUrl;
	private String curmImgUrl;
	private String curoImgUrl;
	private int num;
	private int window;
	private boolean clicked;
	private boolean set2;
	
	private ArrayList<Blog> blogInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picshow);
		findViewsById();
		setListener();
		imgIS.setFactory(MyViewFactory.getInstance(this));
	}
	
	@Override
	public void onStart(){
		if(null == proDialog)
			proDialog = ProgressDialog.show(picshow.this, "", "读取中，请稍后...",true, true);
        //Looper looper = Looper.myLooper();
        //messageHandler = new ADMessageHandler(looper);
		SharedPreferences share = getSharedPreferences(SHARE_CHANNEL_TAG, 0);
		channel = share.getString(SHARE_CHANNEL, "");
		channelID = share.getString(SHARE_CHANNELID, "1");
		SharedPreferences loginShare = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		schoolID = loginShare.getString(SHARE_LOGIN_SCHOOLID, "1");
    	loginState = loginShare.getString(SHARE_LOGIN_STATE, "");
    	clicked = false;
    	set2 = false;
    	initView();
		super.onStart();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(null != proDialog)
			proDialog.dismiss();
		super.onDestroy();
	}

	private void findViewsById() {
		refreshBT = (Button)findViewById(R.id.imgchannelfresh);
		imgIS = (ImageSwitcher)findViewById(R.id.imgchannelis);
		imgG= (Gallery)findViewById(R.id.imgchannelg);
		picauthorTV = (TextView)findViewById(R.id.imgauthor);
		pictimeTV=(TextView)findViewById(R.id.imgtime);
		piccommentTV=(TextView)findViewById(R.id.pictv);
	}
	
	private void setListener() {
		refreshBT.setOnClickListener(refreshListener);
		imgIS.setOnClickListener(imgClickListener);
		//msgBT.setOnClickListener(msgClickListener);
	}
	private OnClickListener imgClickListener =new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

				blogID=curblogID;
				authorID=curauthorID;
				blogAuthor=curblogAuthor;
				blogContent=curblogContent;
				sImgUrl=cursImgUrl;
				mImgUrl=curmImgUrl;
				oImgUrl=curoImgUrl;

				Intent intent = new Intent();
				intent.setClass(picshow.this, BlogView.class);
				Bundle bundle = new Bundle();
				bundle.putString("blogContent", blogContent);
				bundle.putString("blogID", blogID);
				bundle.putString("authorID", authorID);
				bundle.putString("blogAuthor", blogAuthor);
				bundle.putString("blogTime", blogTime);
				bundle.putString("smallImgUrl", sImgUrl);
				bundle.putString("middleImgUrl", mImgUrl);
				bundle.putString("originalImgUrl", oImgUrl);
				bundle.putString("source", "picshow");
				intent.putExtras(bundle);
	    		startActivity(intent);
		}
	};
	
	
	
    private OnClickListener refreshListener =new OnClickListener()
    {
    	//删除所有图片，重新显示
    	@Override
		public void onClick(View arg0) {
    		proDialog = ProgressDialog.show(picshow.this, "", "正在刷新，请稍后...",true, true);
			SysUtil sysUtil = new SysUtil(picshow.this);
			sysUtil.refresh(picshow.class, null);
		}
    };

    private void initView(){

    	FetchBlogTask fetchBlogTask = new FetchBlogTask();
    	fetchBlogTask.execute();
    	
    }
    
    class FetchBlogTask extends AsyncTask{
    	
    	//private ArrayList<Blog> blogInfo;

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

	    	//ArrayList<HashMap<String,Object>> mData= new ArrayList<HashMap<String,Object>>();  

	    	String blogData = ConnUtil.getBlogInfo("getChannelBlogs", schoolID, "5", Integer.toString(offset), windowsize, "");
	    	Log.e("ChannelView.java blog list data", schoolID + " " + channel + " " + offset + " " + blogData);
	    	//ADTV.setText(blogData);
	    	//Log.e("channel", "schoolID: " + schoolID + "," + "channel: " + channel + ":");
	    	
	    	JsonUtil jsonUtil = new JsonUtil();
	    	blogInfo = jsonUtil.parseBlogListFromJson(blogData);

	    	num = blogInfo.size();
	    	//rtNum  = num;
	    	for(int i = 0; i < num; i ++){

	    		sImages[i]=blogInfo.get(i).getSmallImgUrl().toString();
	    		mImages[i]=blogInfo.get(i).getMiddleImgUrl().toString();
	    		oImages[i]=blogInfo.get(i).getOriginalImgUrl().toString();

	    	}
	    	//window=num;	
	    	Log.e("num",Integer.toString(num));
	    	if (num < windowsize)
	    	{
	    		rtNum=0;
	    	}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub

			imgG.setAdapter(new ImageAdapter(picshow.this));
			if(set2){
				imgG.setSelection(2);
			}
	        //imgG.setOnItemSelectedListener(itemListener);
			imgG.setOnItemClickListener(listener);
	        //imgG.setSelection(2);

			PicFetch picFetch = new PicFetch();
			if (set2)
			{
				picFetch.execute(2);
				set2=false;
			}
			else{
			picFetch.execute(0);}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub[]
			super.onPreExecute();
		}
    	
    }
    private OnItemClickListener listener = new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			// TODO Auto-generated method stub
			Log.e("position", Integer.toString(position));
			clicked = true;
			PicFetch picFetch = new PicFetch();
			picFetch.execute(position);
		}
    };
    
    

	class PicFetch extends AsyncTask{
		
		//private String blogDatanew;
		private int position;
		private ProgressDialog proDialg;

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub

			position = (Integer)params[0];
			//Log.e("cur position",params.toString());
			//blogDatanew = ConnUtil.getBlogInfo("getChannelBlogs", schoolID, "5", Integer.toString(offset),windowsize, "");		    	
	    	
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
//			
//			JsonUtil jsonUtilnew = new JsonUtil();
//	    	ArrayList<Blog> blogInfo = jsonUtilnew.parseBlogListFromJson(blogDatanew);
//	    	curblogInfoID=position;
//	    	curblogInfo=blogInfo;
			Log.e("picshow.java", Integer.toString(offset));
			if (position==windowsize-1&&windowsize==num)
			{
				offset = offset+position;
				
				//imgG.setSelection(0);
				//Log.e("1", "aaa");

		    	FetchBlogTask fetchBlogTask = new FetchBlogTask();
		    	fetchBlogTask.execute();
				clicked = false;
				
				return;
			}
			if (windowsize>num&&position>=num-1)
			{
				offset=0;
				rtNum=1;
				
				//imgG.setSelection(0);
				//Log.e("2", "aaa");
				Toast.makeText(getApplicationContext(), "图片浏览完毕，回到首张", Toast.LENGTH_SHORT).show();
		    	FetchBlogTask fetchBlogTask = new FetchBlogTask();
		    	fetchBlogTask.execute();
				clicked = false;
				
				return;
				
			}
			if (position==0&&offset>2&&clicked)
			{
				offset = offset-2;
				//imgG.setSelection(2);
				set2 = true;
				//Log.e("3", "aaa");

		    	FetchBlogTask fetchBlogTask = new FetchBlogTask();
		    	fetchBlogTask.execute();
				clicked = false;
				return;
			}
			if(position==0&&offset<=2&&offset>0&&clicked)
			{
				offset = 0;
				
				//imgG.setSelection(0);
				//Log.e("4", "aaa");
				Toast.makeText(getApplicationContext(), "图片浏览完毕，回到首张", Toast.LENGTH_SHORT).show();
		    	FetchBlogTask fetchBlogTask = new FetchBlogTask();
		    	fetchBlogTask.execute();
				clicked = false;
				
				return;		
			}
			imgIS.setImageDrawable(loadImageFromNetwork(mImages[position]));

			picauthorTV.setText(blogInfo.get(position).getAuthor());
			pictimeTV.setText(blogInfo.get(position).getReleaseTime());
			piccommentTV.setText(blogInfo.get(position).getContent());
			
			curblogID=blogInfo.get(position).getBlogID();
			curauthorID=blogInfo.get(position).getAuthorID();
			curblogAuthor=blogInfo.get(position).getAuthor();
			curblogContent=blogInfo.get(position).getContent();
			cursImgUrl=blogInfo.get(position).getSmallImgUrl();
			curmImgUrl=blogInfo.get(position).getMiddleImgUrl();
			curoImgUrl=blogInfo.get(position).getOriginalImgUrl();

			clicked = false;
			proDialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if(null != proDialog)
				proDialog.dismiss();
			
			proDialog = ProgressDialog.show(picshow.this, "", "获取更多图片中，请稍后...",true, true);
			
			super.onPreExecute();
		}
		
	}
	static class MyViewFactory implements ViewFactory{	
		private static Context context = null;
		private static MyViewFactory instance = null;
		private MyViewFactory(Context context){
			this.context = context;
		}
		@Override
		public View makeView() {			
			ImageView iv = new ImageView(context);	
			iv.setLayoutParams(new ImageSwitcher.LayoutParams( 
	                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
			return iv;
		}
		public static MyViewFactory getInstance(Context context)
		{
			if(instance == null)
			{
				instance = new MyViewFactory(context);
			}
			return instance;
		}
	}
	
	public class ImageAdapter extends BaseAdapter { 
		int mGalleryItemBackground;
		private Context myContext; 
		
		public ImageAdapter(Context c) 
		{ this.myContext = c; 
        // 获得Gallery组件的属性
        TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
        mGalleryItemBackground = typedArray.getResourceId(
                R.styleable.Gallery_android_galleryItemBackground, 0);   
        //mGalleryItemBackground=typedArray.getColor(R.drawable.black, 0);
        } 
		
		public int getCount() 
		{ return sImages.length; } 
		
		public Object getItem(int position)
		{ return position; } 
		public long getItemId(int position)
		{ return position; } 

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(this.myContext); 
			i.setScaleType(ImageView.ScaleType.FIT_CENTER); 
			/* Set the Width/Height of the ImageView. */ 
			
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			if(DisplayMetrics.DENSITY_HIGH == dm.densityDpi){
			i.setLayoutParams(new Gallery.LayoutParams(115, 115)); 
			}
			else
			{
				i.setLayoutParams(new Gallery.LayoutParams(75,75));
			}
			
			new FetchGalleryImageTask(i).execute(sImages[position], Integer.toString(mGalleryItemBackground));
			/* Image should be scaled as width/height are set. */ 
			return i; 
		} 

			/** Returns the size (0.0f to 1.0f) of the views 
			* depending on the \'offset\' to the center. */ 
		public float getScale(boolean focused, int offset) { 
		/* Formula: 1 / (2 ^ offset) */ 
		return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
		} 
	} 
	
	class FetchGalleryImageTask extends AsyncTask<String, Void, Bitmap>{
		private ImageView resultView;
		private int mGalleryItemBackground;

		public FetchGalleryImageTask(ImageView resultView) {
			this.resultView = resultView;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Bitmap bm = null;
			try{
				URL aURL = new URL((String)arg0[0]); 
				mGalleryItemBackground = Integer.parseInt(arg0[1]);
				URLConnection conn = aURL.openConnection(); 
				conn.connect(); 
				InputStream is = conn.getInputStream(); 
				BufferedInputStream bis = new BufferedInputStream(is); 
				bm = BitmapFactory.decodeStream(bis); 
				bis.close(); 
				is.close(); 

			} catch (IOException e) { 
				Log.e("DEBUGTAG", "Remote Image Exception", e); 
				resultView.setImageResource(R.drawable.ic_launcher);
			}
			
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub

			/* Apply the Bitmap to the ImageView that will be returned. */ 
			resultView.setImageBitmap(result); 
			//i.setAdjustViewBounds(true);
			resultView.setBackgroundResource(mGalleryItemBackground);

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
				
			super.onPreExecute();
		}
		

		
	}
	
	private Drawable loadImageFromNetwork(String imageUrl)  
	{  
	    Drawable drawable = null;  
	    try {  
	        // 可以在这里通过文件名来判断，是否本地有此图片  
	        drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "mImage.jpg");
	    } catch (IOException e) {  
	    	String strMsg = e.getMessage();
	    	if(strMsg != null)
	        	Log.e("DrawImg", e.getMessage());    
	    }  
	    if (drawable == null) {  
	        Log.e("DrawImg", "null drawable");
	        Resources r = this.getBaseContext().getResources();
	        drawable = r.getDrawable(R.drawable.ic_launcher);
	    }
	    
	    return drawable ;  
	}  
}