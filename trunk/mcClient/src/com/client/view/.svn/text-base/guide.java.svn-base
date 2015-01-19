package com.client.view;

import com.client.R;
import com.client.util.SysUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class guide extends Activity {
	private boolean isNetError;
	private Button nextpageBT;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.firstguide);
	}
    public void onStart(){
    	int flag = getIntent().getIntExtra("flag", 0);
    	if(flag == SysUtil.EXIT_APPLICATION)
    		finish();

        isNetError = false;
        findViewsById();
        setListener();
        super.onStart();
    	
    }
    private void findViewsById(){
    	
    	nextpageBT = (Button)findViewById(R.id.guidenextpage);
    }
    private void setListener(){
    	nextpageBT.setOnClickListener(nextpageListener);
    }
    
    private OnClickListener nextpageListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(guide.this, guidetwo.class);
			startActivity(intent);
			
		}
    	
    };

}
