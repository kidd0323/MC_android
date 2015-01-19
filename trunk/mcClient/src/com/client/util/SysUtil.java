package com.client.util;

import com.client.view.BlocksView;
import com.client.view.LoginView;
import com.client.view.MyCampusView;
import com.client.view.StartView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SysUtil {
	public static final int  EXIT_APPLICATION = 0x0001;
	public static final int  JUMP_TO_INDEX = 0x0002;
	public static final int  REFRESH = 0x0003;
	private Context mContext;
	
	public SysUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public void exit(){
		Intent intent = new Intent();
		intent.setClass(mContext, MyCampusView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("flag", EXIT_APPLICATION);
		mContext.startActivity(intent);
	}
	
	public void jumpIndex(){
		Intent intent = new Intent();
		intent.setClass(mContext, BlocksView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("flag", JUMP_TO_INDEX);
		mContext.startActivity(intent);
	}

	public void refresh(Class viewClass, Bundle bundle){
		Intent intent = new Intent();
		intent.setClass(mContext, viewClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("flag", REFRESH);
		if(null != bundle)
			intent.putExtras(bundle);
		mContext.startActivity(intent);
	}


}
