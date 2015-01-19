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

public class guidetwo extends Activity {
	private boolean isNetError;
	private Button nextpageBT;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.secondguide);
	}

}
