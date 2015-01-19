package com.client.view;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.client.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class OriginalImgView extends Activity {
	private ImageView oBlogImgIV;
	private String oImgUrl;
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.originalimgviewui);
		bundle = getIntent().getExtras();
		oImgUrl = bundle.getString("originalImgUrl");
        showImage(); 
	}
	private void showImage() {
		oBlogImgIV = (ImageView)findViewById(R.id.origBlogImg);
	    BitmapManager.INSTANCE.loadBitmap(oImgUrl, oBlogImgIV, 50, 50);  
		//new GetImageTask().execute(oImgUrl);
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
	
	private class GetImageTask extends AsyncTask<String, Void, Drawable>{

		@Override
		protected Drawable doInBackground(String... arg0) {
			return loadImageFromNetwork(arg0[0]);
		}
		protected void onPostExecute(Drawable result){
			oBlogImgIV.setImageDrawable(result);
		}
		
	}
	private Drawable loadImageFromNetwork(String imageUrl)  
	{  
	    Drawable drawable = null;  
	    try {  
	        // 可以在这里通过文件名来判断，是否本地有此图片  
	        drawable = Drawable.createFromStream(  
	                new URL(imageUrl).openStream(), "oImage.jpg");  
	    } catch (IOException e) {  
	        Log.d("DrawImg", e.getMessage());  
	    }  
	    if (drawable == null) {  
	        Log.d("DrawImg", "null drawable");  
	    }
	    return drawable ;  
	}
}
