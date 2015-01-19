package com.client.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

import org.json.JSONObject;

import com.client.entity.Blog;
import com.client.entity.ChatMessage;
import com.client.entity.CommentMsgPackage;
import com.client.entity.PrivateMsgPackage;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;


public class JsonUtil {
	Type type = null;
	Object obj = null;
	public Object parseObjectFromJson(String jsonData, Class classType){

		try{
			Gson gson = new Gson();
			obj = gson.fromJson(jsonData, classType);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return obj;
		
	}
	public ArrayList parseBlogListFromJson(String jsonData){

		Type listType = new TypeToken<ArrayList<Blog>>(){}.getType();
		ArrayList<Blog> list;
		try{
			Gson gson = new Gson();
			//Object a = gson.fromJson(jsonData, SimpleBlogInfo.class);
			list = gson.fromJson(jsonData, listType);
			//ArrayList<SimpleBlogInfo> list = new ArrayList();
			//list.add((SimpleBlogInfo)a);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return list;
		
	}
	public ArrayList parseMsgListFromJson(String jsonData, String type){
		Type listType;
		Gson gson = new Gson();
		if("comment".equals(type)){
			listType = new TypeToken<ArrayList<CommentMsgPackage>>(){}.getType();
			
			ArrayList<CommentMsgPackage> list;
			try{
				list = gson.fromJson(jsonData, listType);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
			return list;
		}

		if("private".equals(type)){
			listType = new TypeToken<ArrayList<PrivateMsgPackage>>(){}.getType();
			ArrayList<PrivateMsgPackage> list;
			try{
				list = gson.fromJson(jsonData, listType);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
			return list;
		}
		//Object a = gson.fromJson(jsonData, SimpleBlogInfo.class);
		//ArrayList<SimpleBlogInfo> list = new ArrayList();
		//list.add((SimpleBlogInfo)a);
		return null;
		
	}
	
	public ArrayList parseChatListFromJson(String jsonData){

		Type listType = new TypeToken<ArrayList<ChatMessage>>(){}.getType();
		Gson gson = new Gson();
		//Object a = gson.fromJson(jsonData, SimpleBlogInfo.class);
		ArrayList<ChatMessage> list;
		try{
			list = gson.fromJson(jsonData, listType);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		//ArrayList<SimpleBlogInfo> list = new ArrayList();
		//list.add((SimpleBlogInfo)a);
		
		return list;
		
	}

}
