package com.client.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import com.client.R;
import com.client.R.id;
import com.client.R.layout;
import com.client.entity.AD;
import com.client.entity.CampusPackage;
import com.client.entity.RegionPackage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.BlocksView.BADMessageHandler;
import com.client.view.BlocksView.UADMessageHandler;
import com.google.gson.reflect.TypeToken;

import android.view.MotionEvent;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.Iterator;

public class modify extends Activity {

	private Button confirmBT;
	private Button cancelBT;
	private Button modifypwBT;
	private EditText nicknameET;
	private TextView showTV;
	
	private Looper looper;
	private Handler uMessageHandler;  
	private Handler bMessageHandler;  
	private boolean isNetError;
	
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String username;
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_REGION = "MAP_LOGIN_REGION";
	private String SHARE_LOGIN_REGIONID = "MAP_LOGIN_REGIONID";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	
	ArrayList<String> regions;
	ArrayList<String> regionIDs;
	ArrayList<String> campuses;
	ArrayList<String> campusIDs;
	private Spinner regionSPN;
	private Spinner campusSPN;

	private boolean selected;
	private String selectedRegion;
	private String selectedRegionID;
	private String selectedCampus;
	private String selectedCampusID;
	private int selectedRegionIndex;
	private int selectedCampusIndex;
	
	private String nickname;
	private String region;
	private String campus;
	private String userID;
	
	private TextView curRegionTV;
	private TextView curSchoolTV;
	private boolean regionSelected;
	

	private ProgressDialog proDialog;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.modify);
	}
    public void onStart(){
    	if(null == proDialog)
			proDialog = ProgressDialog.show(modify.this, "", "读取中，请稍后...",true, true);
    	int flag = getIntent().getIntExtra("flag", 0);
    	if(flag == SysUtil.EXIT_APPLICATION)
    		finish();

        isNetError = false;
    	regionSelected = false;
        findViewsById();
        initView();
        setListener();
        super.onStart();
    	
    }
    
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	
    	if(! (null == proDialog))
    		proDialog.dismiss();
		super.onDestroy();
	}
	private void findViewsById(){
    	
    	confirmBT = (Button)findViewById(R.id.confirm);
    	cancelBT = (Button)findViewById(R.id.cancel);
    	//modifypwBT = (Button)findViewById(R.id.modifypw);  	
    	regionSPN=(Spinner)findViewById(R.id.spinner1);
    	campusSPN=(Spinner)findViewById(R.id.spinner2);
    	//nicknameET=(EditText)findViewById(R.id.editText1);
    	//showTV=(TextView)findViewById(R.id.textView1);
    	curRegionTV=(TextView)findViewById(R.id.curregion);
    	curSchoolTV=(TextView)findViewById(R.id.curschool);
    	
    	
    }
    private void initView(){
    	SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
    	username = share.getString(SHARE_LOGIN_USERNAME, "");
		userID = share.getString(SHARE_LOGIN_USERID, "");
		region =share.getString(SHARE_LOGIN_REGION, "");
		campus =share.getString(SHARE_LOGIN_SCHOOL, "");
		
		curRegionTV.setText(region);
		curSchoolTV.setText(campus);
    	
		LoadRegionsTask loadRegionsTask = new LoadRegionsTask();
		loadRegionsTask.execute();

    }
    private void setListener(){
    	confirmBT.setOnClickListener(confirmListener);
    	cancelBT.setOnClickListener(cancelListener);
    	//modifypwBT.setOnClickListener(modifypwListener);
		regionSPN.setOnItemSelectedListener(regionItemSeletecListener);
		campusSPN.setOnItemSelectedListener(campusItemSeletedListener);
		campusSPN.setOnTouchListener(new MyOnTouchListener());
    }
	
	
	

	public class MyOnTouchListener implements OnTouchListener {  
  
        public boolean onTouch(View v, MotionEvent event) {  

			if(!regionSelected){
				Toast.makeText(modify.this, "请先选择区域！", Toast.LENGTH_SHORT).show();
				return true;
			}
			
            return false;  
        }  
    }  
	private OnItemSelectedListener regionItemSeletecListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				selectedRegionIndex = arg2;
				selectedRegion =(String)regionSPN.getItemAtPosition(arg2);

				if(arg2 > 0)
				{
					selectedRegionID = regionIDs.get(arg2 - 1);

					regionSelected = true;
					LoadCampusTask loadCampusTask = new LoadCampusTask();
					loadCampusTask.execute();
				}else{
					selected = false;
					if(campuses!=null)
						campuses.clear();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

	};
	private OnItemSelectedListener campusItemSeletedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			selectedCampusIndex = arg2;
			selectedCampus =(String)campusSPN.getItemAtPosition(arg2);
			if(arg2 > 0){
				selectedCampusID =(String)campusIDs.get(arg2 - 1);
				//selectedCampusID = "1";
			}
			if(arg2==0)
				selected = false;
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
};
    
    private OnClickListener confirmListener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {

    		proDialog = ProgressDialog.show(modify.this, "", "正在验证，请稍后...",true, true);
			
			SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
			userID = share.getString(SHARE_LOGIN_USERID, "");
			// TODO Auto-generated method stub

			
			String jsonDataNew = "";
			if (selectedRegionIndex != 0&&selectedCampusIndex != 0)
			{
				jsonDataNew = new ConnUtil().modifySchool(userID,selectedRegionID,selectedCampusID);
				if ("error".equals(jsonDataNew))
				{
					Toast.makeText(modify.this, "修改出错，稍后再试", Toast.LENGTH_SHORT).show();
				}
				else if(!"".equals(jsonDataNew))
				{
		    		share.edit().putString(SHARE_LOGIN_REGION, selectedRegion).commit();
		    		share.edit().putString(SHARE_LOGIN_REGIONID, selectedRegionID).commit();
		    		share.edit().putString(SHARE_LOGIN_SCHOOL, selectedCampus).commit();
		    		share.edit().putString(SHARE_LOGIN_SCHOOLID, selectedCampusID).commit();
		    		proDialog.dismiss();
					Toast.makeText(modify.this, "修改成功",
							Toast.LENGTH_SHORT).show();

					curRegionTV.setText(selectedRegion);
					curSchoolTV.setText(selectedCampus);
				}
			}
			else
			{
				Toast.makeText(modify.this, "区域或者学校未选择",
						Toast.LENGTH_SHORT).show();
			}
			
			if(!(null == proDialog))
				proDialog.dismiss();
			
		}
    	
    };
    private OnClickListener cancelListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			/*Intent intent = new Intent();
			intent.setClass(modify.this, BlocksView.class);
			startActivity(intent);*/
			//Modified by Bryan 20120229
			finish();
		}
    	
    };
    
    class LoadRegionsTask extends AsyncTask{
    	private String jsonData;

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
	    	
	    	jsonData = new ConnUtil().getLocationData("getRegions", "", "");
	    	Log.e("MyCampusView.java regions", jsonData);
			//goLoginBT.setText(jsonData);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub

	    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){
		
				JsonUtil jsonUtil = new JsonUtil();
				RegionPackage regionPackage = (RegionPackage)jsonUtil.parseObjectFromJson(jsonData, RegionPackage.class);
				if(null == regionPackage && (!"".equals(regionPackage))){

					Message msg = new Message();
					msg.arg1 = 2;
					exceptionHandler.sendMessage(msg);
					return;
				}
				ArrayAdapter<String> regionAdapter;
				regions = (ArrayList)regionPackage.getRegionList();
				regionIDs = (ArrayList)regionPackage.getRegionIDList();
				regions.add(0, "区域选择");
				//goLoginBT.setText((String)region.getRegionList().get(0));
				regionAdapter = new ArrayAdapter<String>(modify.this, R.layout.spinner, regions);
				regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				regionSPN.setAdapter(regionAdapter);
				
				ArrayList<String> initCampus = new ArrayList();
				initCampus.add("");
				ArrayAdapter<String> initCampusAdapter;
				initCampusAdapter = new ArrayAdapter<String>(modify.this, R.layout.spinner, initCampus);
				initCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				campusSPN.setAdapter(initCampusAdapter);


	    	}else{

				Toast.makeText(modify.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
	    	}
	    	proDialog.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			//proDialog = ProgressDialog.show(modify.this, "", "读取中，请稍后...",true, true);
			
			super.onPreExecute();
		}
    	
    }
    
    class LoadCampusTask extends AsyncTask{
    	
    	private String jsonData;
		private ArrayAdapter<String> campusAdapter;

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub

			jsonData = new ConnUtil().getLocationData("getCampus", "regionID", selectedRegionID);
	    	Log.e("MyCampusView.java campuses" + ":" + selectedRegionID, jsonData);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub

	    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){

		    	JsonUtil jsonUtil = new JsonUtil();
				CampusPackage campusPackage = (CampusPackage)jsonUtil.parseObjectFromJson(jsonData, CampusPackage.class);
				if(null == campusPackage && (!"".equals(campusPackage))){

					Message msg = new Message();
					msg.arg1 = 3;
					exceptionHandler.sendMessage(msg);
					return;
				}
				campuses = (ArrayList)campusPackage.getCampusList();
				campusIDs = (ArrayList)campusPackage.getCampusIDList();

	    	}else{
				Toast.makeText(modify.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
	    	}

			if(campuses == null){
				campuses = new ArrayList<String>();
			}
			campuses.add(0, "学校");
			campusAdapter = new ArrayAdapter<String>(modify.this, R.layout.spinner, campuses);
			campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			campusSPN.setAdapter(campusAdapter);	
			proDialog.dismiss();
			super.onPostExecute(result);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = ProgressDialog.show(modify.this, "", "读取中，请稍后...",true, true);
			
			super.onPreExecute();
		}
    	
    }

	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1){
				Toast.makeText(modify.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
			}

			if(msg.arg1 == 2){
				LoadRegionsTask loadRegionsTask = new LoadRegionsTask();
				loadRegionsTask.execute();
			}
			if(msg.arg1 == 3){
				LoadCampusTask loadCampusTask = new LoadCampusTask();
				loadCampusTask.execute();
			}
			return;
		}
	};

    
    
}
