package com.client.view;

import java.util.ArrayList;
import com.client.R;
import com.client.entity.Campus;
import com.client.entity.CampusPackage;
import com.client.entity.RegionPackage;
import com.client.util.ConnUtil;
import com.client.util.JsonUtil;
import com.client.util.SysUtil;
import com.client.view.modify.LoadCampusTask;
import com.client.view.modify.LoadRegionsTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MyCampusView extends Activity {

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_REGION = "MAP_LOGIN_REGION";
	private String SHARE_LOGIN_REGIONID = "MAP_LOGIN_REGIONID";
	private String SHARE_LOGIN_SCHOOL = "MAP_LOGIN_SCHOOL";
	private String SHARE_LOGIN_SCHOOLID = "MAP_LOGIN_SCHOOLID";
	private String SHARE_LOGIN_STATE = "MAP_LOGIN_STATE";
	private Button hangAroundBT;
	private Button goLoginBT;
	@SuppressWarnings("unused")
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	@SuppressWarnings("unused")
	private String SHARE_LOGIN_USERID = "MAP_LOGIN_USERID";
	private Spinner regionSPN;
	private Spinner campusSPN;
	private String selectedRegion;
	private String selectedRegionID;
	private String selectedCampus;
	private String selectedCampusID;
	private String userID;
	private String username;
	private String nickname;
	private int selectedRegionIndex;
	private int selectedCampusIndex;
	private boolean selected;
	private boolean regionSelected;
	ArrayList<String> regions;
	ArrayList<String> regionIDs;
	ArrayList<String> campuses;
	ArrayList<String> campusIDs;
	
	private ProgressDialog proDialog;
	
	//private ArrayAdapter<String> regionAadapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		userID = bundle.getString("MAP_USERID");
		username = bundle.getString("MAP_USERNAME");
		nickname = bundle.getString("MAP_NICKNAME");
		Log.e("mycampusview.java", nickname);
		
		setContentView(R.layout.mycampusui);
		findViewsById();
		setListener();
	}

	public void onStart(){
		selected = false;
    	regionSelected = false;
		proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
		
    	int flag = getIntent().getIntExtra("flag", 0);
		Log.e("StartView.java", "exit" + ":" + Integer.toString(flag)); 
    	if(flag == SysUtil.EXIT_APPLICATION){
    		finish();
    	}else{
    		initView();
    	}
		super.onStart();
	}
	public void onResume(){
    	int flag = getIntent().getIntExtra("flag", 0);
		Log.e("MyCampusView", "exit" + ":" + Integer.toString(flag)); 
    	if(flag == SysUtil.EXIT_APPLICATION){
    		finish();
    	}
		super.onResume();
	}

    protected void onNewIntent(Intent intent) {  
    	int flag = getIntent().getIntExtra("flag", 0); 
    	if(flag == SysUtil.EXIT_APPLICATION){ 
    		Log.e("MyCampusView", "exit" + ":" + Integer.toString(flag));
    		finish();
    	}  
    	super.onNewIntent(intent);  
	}  
	
	private void setListener() {
		goLoginBT.setOnClickListener(goLoginClickListener);
		hangAroundBT.setOnClickListener(hangAroundsClickListener);
		regionSPN.setOnItemSelectedListener(regionItemSeletedListener);
		campusSPN.setOnItemSelectedListener(campusItemSeletedListener);
		campusSPN.setOnTouchListener(new MyOnTouchListener());
	}
	public class MyOnTouchListener implements OnTouchListener {  
  
        public boolean onTouch(View v, MotionEvent event) {
			if(!regionSelected){
				Toast.makeText(MyCampusView.this, "请先选择区域！", Toast.LENGTH_SHORT).show();
				return true;
			}
			
            return false;  
        }  
    }  
//	public class MyOnTouchListener implements OnTouchListener {  
//  
//        @Override  
//        public boolean onTouch(View v, MotionEvent event) {  
//        	if(proDialog != null)
//        		proDialog.dismiss();
//			proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
//			
//            return false;  
//        }  
//    }  
	
	public void onDestroy(){
		if(null != proDialog)
			proDialog.dismiss();
		super.onDestroy();
	}
	
	@SuppressWarnings("unchecked")
	private void initView() {
		LoadRegionsTask loadRegionsTask = new LoadRegionsTask();
		loadRegionsTask.execute();


	}
	
	private OnItemSelectedListener regionItemSeletedListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedRegionIndex = arg2;
				selectedRegion =(String)regionSPN.getItemAtPosition(arg2);
				if(arg2 > 0)
				{
					regionSelected = true;
					selectedRegionID = regionIDs.get(arg2 - 1);
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
				
				
			}
	};

	private OnItemSelectedListener campusItemSeletedListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

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
				
			}
	};
	

	private OnClickListener goLoginClickListener = new OnClickListener() {
			@Override
	    	public void onClick(View v){


				proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
				if(selectedRegionIndex == 0)
					Toast.makeText(getApplicationContext(), "请选择区域", Toast.LENGTH_SHORT).show();
				else if(selectedCampusIndex == 0)
					Toast.makeText(getApplicationContext(), "请选择校园", Toast.LENGTH_SHORT).show();
				else
					selected = true;
				if(selected){
					String str = ConnUtil.modifySchool(userID, selectedRegionID, selectedCampusID);
					if ("error".equals(str))
					{
						Toast.makeText(getApplicationContext(), "修改出错，稍后再试", Toast.LENGTH_SHORT).show();
					}
					else if(!"".equals(str))
					{
						Toast.makeText(getApplicationContext(), "已经提交", Toast.LENGTH_SHORT).show();
					}
					SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
					share.edit().putString(SHARE_LOGIN_REGION, selectedRegion).commit();
					share.edit().putString(SHARE_LOGIN_REGIONID, selectedRegionID).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOL, selectedCampus).commit();
					share.edit().putString(SHARE_LOGIN_SCHOOLID, selectedCampusID).commit();
					share = null;
					Intent intent = new Intent();
					intent.setClass(MyCampusView.this, BlocksView.class);
					Bundle bundle = new Bundle();
					bundle.putString("MAP_USERNAME", username);
					bundle.putString("MAP_NICKNAME", nickname);
					bundle.putString("MAP_USERID", userID);
					bundle.putString("MAP_CAMPUS", selectedCampus);
					bundle.putString("MAP_CAMPUSID", selectedCampusID);
					bundle.putString("MAP_REGION", selectedRegion);
					bundle.putString("MAP_REGIONID", selectedRegionID);
					intent.putExtras(bundle);
					MyCampusView.this.finish();
					startActivity(intent);
				}
			}

	};


	private OnClickListener hangAroundsClickListener = new OnClickListener() {

			@Override
	    	public void onClick(View v){
				
				HangAroundTask hangAroundTask = new HangAroundTask();
				hangAroundTask.execute();

			}
	};
	
	private void findViewsById() {
		hangAroundBT = (Button)findViewById(R.id.hangAround);
		goLoginBT = (Button)findViewById(R.id.goLogin);
		regionSPN = (Spinner)findViewById(R.id.regionSpinner);
		campusSPN = (Spinner)findViewById(R.id.campusSpinner);
	}
	
	class HangAroundTask extends AsyncTask{
		private String jsonData;
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			jsonData = new ConnUtil().getLocationData("getRecommendedCampus", "", "");

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub

	    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){
				Campus campus = (Campus)new JsonUtil().parseObjectFromJson(jsonData, Campus.class);
				
				if(null == campus && (!"".equals(campus))){

					Message msg = new Message();
					msg.arg1 = 3;
					exceptionHandler.sendMessage(msg);
					return;
				}
				
				selectedRegion = campus.getRegion();
				selectedRegionID = campus.getRegionID();
				selectedCampus = campus.getCampus();
				selectedCampusID = campus.getCampusID();
				selected = true;
				
				SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
				share.edit().putString(SHARE_LOGIN_REGION, selectedRegion).commit();
				share.edit().putString(SHARE_LOGIN_REGIONID, selectedRegion).commit();
				share.edit().putString(SHARE_LOGIN_SCHOOL, selectedCampus).commit();
				share.edit().putString(SHARE_LOGIN_SCHOOLID, selectedCampusID).commit();
				share.edit().putString(SHARE_LOGIN_STATE, "false").commit();
				share = null;
				Intent intent = new Intent();
				intent.setClass(MyCampusView.this, BlocksView.class);
				Bundle bundle = new Bundle();
				bundle.putString("MAP_USERNAME", username);
				bundle.putString("MAP_NICKNAME", nickname);
				bundle.putString("MAP_USERID", userID);
				bundle.putString("MAP_CAMPUS", selectedCampus);
				bundle.putString("MAP_CAMPUSID", selectedCampusID);
				bundle.putString("MAP_REGION", selectedRegion);
				bundle.putString("MAP_REGIONID", selectedRegionID);
				intent.putExtras(bundle);
				MyCampusView.this.finish();
				startActivity(intent);
	    	}else{

				Toast.makeText(MyCampusView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
	    	}
	    	if(null != proDialog)
	    		proDialog.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
			
			super.onPreExecute();
		}
		
	}

    class LoadRegionsTask extends AsyncTask{
    	
    	private String jsonData;				
    	private ArrayAdapter<String> regionAdapter;


		@Override
		protected Object doInBackground(Object... params) {

			jsonData = ConnUtil.getLocationData("getRegions", "", "");
	    	Log.e("MyCampusView.java regions", jsonData);
			//goLoginBT.setText(jsonData);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {

	    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){
		    		
				JsonUtil jsonUtil = new JsonUtil();
		
				RegionPackage regionPackage = (RegionPackage)jsonUtil.parseObjectFromJson(jsonData, RegionPackage.class);

				if(null == regionPackage && (!"".equals(regionPackage))){

					Message msg = new Message();
					msg.arg1 = 2;
					exceptionHandler.sendMessage(msg);
					return;
				}
				regions = (ArrayList<String>)regionPackage.getRegionList();
				regionIDs = (ArrayList<String>)regionPackage.getRegionIDList();
				regions.add(0, "区域选择");
				//goLoginBT.setText((String)region.getRegionList().get(0));
				regionAdapter = new ArrayAdapter<String>(MyCampusView.this, R.layout.spinner, regions);
				regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				regionSPN.setAdapter(regionAdapter);
				
				ArrayList<String> initCampus = new ArrayList<String>();
				initCampus.add("");
				ArrayAdapter<String> initCampusAdapter;
				initCampusAdapter = new ArrayAdapter<String>(MyCampusView.this, R.layout.spinner, initCampus);
				initCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				campusSPN.setAdapter(initCampusAdapter);


	    	}else{

				Toast.makeText(MyCampusView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
	    	}
	    	if(null != proDialog)
	    		proDialog.dismiss();

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			//proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
			
			super.onPreExecute();
		}
    	
    }

    class LoadCampusTask extends AsyncTask{
    	private String jsonData;
		private ArrayAdapter<String> campusAdapter;
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub

			jsonData = ConnUtil.getLocationData("getCampus", "regionID", selectedRegionID);
	    	Log.e("MyCampusView.java campuses" + ":" + selectedRegionID, jsonData);
	    	
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub


	    	if((!"error".equals(jsonData)) && (!"exception".equals(jsonData))){

		    	JsonUtil jsonUtil = new JsonUtil();
				CampusPackage campusPackage = (CampusPackage)jsonUtil.parseObjectFromJson(jsonData, CampusPackage.class);
				campuses = (ArrayList)campusPackage.getCampusList();
				campusIDs = (ArrayList)campusPackage.getCampusIDList();

	    	}else{

				Toast.makeText(MyCampusView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
						Toast.LENGTH_SHORT).show();
	    	}
			
			if(campuses == null){
				campuses = new ArrayList<String>();
			}
			campuses.add(0, "学校");
			//goLoginBT.setText((String)region.getRegionList().get(0));
			campusAdapter = new ArrayAdapter<String>(MyCampusView.this, R.layout.spinner, campuses);
			campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			campusSPN.setAdapter(campusAdapter);	
			
			proDialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			proDialog = ProgressDialog.show(MyCampusView.this, "", "读取中，请稍后...",true, true);
			super.onPreExecute();
		}
    	
    }
	Handler exceptionHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.arg1 == 1){
				Toast.makeText(MyCampusView.this, "操作失败:\n1.请检查您网络连接.\n2.请联系我们.!",
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
