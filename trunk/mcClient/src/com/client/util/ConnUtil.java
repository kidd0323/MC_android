package com.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;
public class ConnUtil {
	
	private static String jsonData;
	//private static String validateURL="http://topexplorer.gotoip1.com/";
	private static String validateURL="http://www.mxiaoyuan.com/"; 
	
	private static CookieStore cookieStore = null;
	private static String Username = null;
	private static String Password = null;
	
	public static String testLoginState(){
		String postURL = validateURL + "user/getCurrentUser";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String strResult = null;
		HttpPost httpRequest =new HttpPost(postURI);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		httpRequest.setHeader("Connection", "Keep-Alive");
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
			
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse httpResponse=httpClient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				strResult=EntityUtils.toString(httpResponse.getEntity());
			}
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		return strResult;
	}
	
	public static String validateAccount(String username, String password){
		String postURL = validateURL + "user/login";
		Username = username;
		Password = password;
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String strResult = null;
		HttpPost httpRequest =new HttpPost(postURI);
		List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("password",password));	
		params.add(new BasicNameValuePair("from","android"));
		httpRequest.setHeader("Connection", "Keep-Alive");
		
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse=httpClient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				cookieStore = httpClient.getCookieStore();
				strResult=EntityUtils.toString(httpResponse.getEntity());
			}
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		return strResult;
	}

	public static String getADs(String ope, int num, String schoolID, String ADPosition){
		String dir = "channel/";
		String postURL = validateURL + dir + ope;// + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(postURI);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("num",Integer.toString(num)));	
		params.add(new BasicNameValuePair("ADPosition",ADPosition));
		params.add(new BasicNameValuePair("school_id",schoolID));
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		} 
		
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception";
		}
	}
	
	public static String getLocationData(String ope, String paraName, String para){
		String paraMap = para;
		String dir = "school/";
		
		String postURL = validateURL + dir + ope;// + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String strResult = null;
		HttpPost httpPost = new HttpPost(postURI);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(!"".equals(paraName)){
			params.add(new BasicNameValuePair(paraName,para));
		}
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}
	
	//指定msgID，得到这条信息的简要信息。包括内容，作者什么的，但没有评论链表
	public static String getBlogInfo(String ope, String schoolID, String channelID, String startMsgID, int num, String extraPara){

		String dir = "channel/";
		String postURL = validateURL + dir + ope;// + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			
			e1.printStackTrace();
		}
		//String postURL = validateURL + dir + ope + ".php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair numPair = new BasicNameValuePair("num", Integer.toString(num));
		NameValuePair schoolIDPair = new BasicNameValuePair("schoolID", schoolID);
		NameValuePair channelIdPair = new BasicNameValuePair("channelID", channelID);
		NameValuePair startIdPair = new BasicNameValuePair("startID", startMsgID);
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);

		params.add(schoolIDPair);
		params.add(channelIdPair);
		params.add(startIdPair);
		params.add(numPair);
		params.add(paraPair);
		
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				//return "error: " + ht.getStatusLine().toString();
				return "error";
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}	
	//指定msgID，得到这条信息的简要信息。包括内容，作者什么的，但没有评论链表
	public static String getBlogComments(String ope, String blogID, String offsetCommentID, int num, String extraPara){
		String dir = "post/";
		String postURL = validateURL + dir + ope;// + ".php";
		//String postURL = validateURL + dir + ope + ".php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair blogIDPair = new BasicNameValuePair("blogID", blogID);
		NameValuePair offsetCommentIDPair = new BasicNameValuePair("offsetCommentID", offsetCommentID);
		NameValuePair numPair = new BasicNameValuePair("num", Integer.toString(num));
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
		
		params.add(blogIDPair);
		params.add(offsetCommentIDPair);
		params.add(numPair);
		params.add(paraPair);
		
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error";
				//return "error: " + ht.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}

	public static String commentBlog(String ope, String blogID, String blogAuthorID, 
			String userID, String targetID, String comment, String commentTime,
			int type, String extraPara){

		String dir = "post/";
		/**
		 * 
		 */
		String postURL = validateURL + dir + ope;// + ".php";
		//String postURL = validateURL + dir + ope + ".php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			
			e1.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair blogIDPair = new BasicNameValuePair("blogID", blogID);
		NameValuePair blogAuthorIDPair = new BasicNameValuePair("blogAuthorID", blogAuthorID);
		NameValuePair userIDPair = new BasicNameValuePair("userID", userID);
		NameValuePair targetIDPair = new BasicNameValuePair("targetID", targetID);
		NameValuePair commentPair = new BasicNameValuePair("comment", comment);
		NameValuePair commentTimePair = new BasicNameValuePair("commentTime", commentTime);
		NameValuePair typePair = new BasicNameValuePair("type", Integer.toString(type));
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
		
		params.add(blogIDPair);
		params.add(blogAuthorIDPair);
		params.add(userIDPair);
		params.add(targetIDPair);
		params.add(commentPair);
		params.add(commentTimePair);
		params.add(typePair);
		params.add(paraPair);
		
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}
	//指定msgID，得到这条信息的简要信息。包括内容，作者什么的，但没有评论链表
	public static String getMsgs(String ope, String userID, String offsetCommentID, int num, String extraPara){

		String dir = "";
		if("getCommentMsgs".equals(ope))
			dir = "post/";
		if("getPrivateMsgs".equals(ope))
			dir = "message/";
		
		String postURL = validateURL + dir + ope;// + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			
			e1.printStackTrace();
		}
		/**
		 * 
		 */
		//String postURL = validateURL + dir + ope + ".php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair userIDPair = new BasicNameValuePair("userID", userID);
		NameValuePair offsetCommentIDPair = new BasicNameValuePair("offsetCommentID", offsetCommentID);
		NameValuePair numPair = new BasicNameValuePair("num", Integer.toString(num));
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
		
		params.add(userIDPair);
		params.add(offsetCommentIDPair);
		params.add(numPair);
		params.add(paraPair);
		
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error: " + ht.getStatusLine().toString();
				//return "error";
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}

	//指定msgID，删除这条消息
//	public static String removeMsg(String msgID, String type, String extraPara){
//
//		String dir = "message/";
//		
//		String postURL = validateURL + dir + "removeMsg";//.php";
//		//String postURL = validateURL + dir + "removeMsg.php";
//		URI postURI = null;
//		try {
//			postURI = new URI(postURL);
//		} catch (URISyntaxException e1) {
//			
//			e1.printStackTrace();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		HttpPost httpPost = new HttpPost(postURI);
//		NameValuePair msgIDPair = new BasicNameValuePair("msgID", msgID);
//		NameValuePair typePair = new BasicNameValuePair("type", type);
//		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
//		
//		params.add(msgIDPair);
//		params.add(typePair);
//		params.add(paraPair);
//		
//		HttpEntity he;
//		try {
//			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//			httpPost.setHeader("Connection", "Keep-Alive");
//			httpPost.setEntity(he);
//		} catch (UnsupportedEncodingException e1) {
//			
//			e1.printStackTrace();
//		} 
//		DefaultHttpClient httpClient = new DefaultHttpClient();
//		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
//		{
//			validateAccount(Username, Password);
//		}
//		httpClient.setCookieStore(cookieStore);
//		try {
//			HttpResponse ht = httpClient.execute(httpPost);
//			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//				HttpEntity het = ht.getEntity();
//				InputStream is = het.getContent();
//				BufferedReader br = new BufferedReader(new InputStreamReader(is));
//				String response = "";
//				String readLine = null;
//				while((readLine =br.readLine()) != null){
//					response = response + readLine;
//				}
//				is.close();
//				br.close();
//				
//				return response;
//			}else{
//				return "error";
//			}
//		} catch (ClientProtocolException e) {
//			
//			e.printStackTrace();
//			return "exception";
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//			return "exception";
//		}
//	}
	
	public static String sendPrivateMsg(String ope, String userID, String userName, String targetID, String targetName
			, String msg, String sendTime, int type, String extraPara){
		String dir = "message/";
		String postURL = validateURL + dir + ope;// + ".php";
		//String postURL = validateURL + dir + ope + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair userIDPair = new BasicNameValuePair("userID", userID);
		NameValuePair userNamePair = new BasicNameValuePair("userName", userName);
		NameValuePair targetIDPair = new BasicNameValuePair("targetID", targetID);
		NameValuePair targetNamePair = new BasicNameValuePair("targetName", targetName);
		NameValuePair msgPair = new BasicNameValuePair("msg", msg);
		NameValuePair sendTimePair = new BasicNameValuePair("sendTime", sendTime);
		NameValuePair typePair = new BasicNameValuePair("type", Integer.toString(type));
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
		params.add(userIDPair);
		params.add(userNamePair);
		params.add(targetIDPair);
		params.add(targetNamePair);
		params.add(msgPair);
		params.add(sendTimePair);
		params.add(typePair);
		params.add(paraPair);
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception";
		}
	}
	
	
	//得到两个用户的私信记录
	public static String getPrivateRecords(String ope, String userID, String otherUserID, String offsetMsgID, int num, String extraPara){
		String dir = "message/";
		String postURL = validateURL + dir + ope;// + ".php";
		//String postURL = validateURL + dir + ope + ".php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair userIDPair = new BasicNameValuePair("userID", userID);
		NameValuePair otherUserIDPair = new BasicNameValuePair("otherUserID", otherUserID);
		NameValuePair offsetMsgIDPair = new BasicNameValuePair("offsetMsgID", offsetMsgID);
		NameValuePair numPair = new BasicNameValuePair("num", Integer.toString(num));
		NameValuePair paraPair = new BasicNameValuePair("extraPara", extraPara);
		params.add(userIDPair);
		params.add(otherUserIDPair);
		params.add(offsetMsgIDPair);
		params.add(numPair);
		params.add(paraPair);
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			
			e.printStackTrace();
			return "exception";
		}
	}
	
	public static String modifySchool(String userID,String regionID, String campusID){
		String postURL = validateURL + "user/modifySchool";;
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String strResult = null;
		HttpPost httpRequest =new HttpPost(postURI);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		ArrayList<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("userID",userID));
		params.add(new BasicNameValuePair("regionID",regionID));
		params.add(new BasicNameValuePair("campusID",campusID));
		params.add(new BasicNameValuePair("from","android"));	
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse httpResponse=httpClient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				strResult=EntityUtils.toString(httpResponse.getEntity());
			}
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		return strResult;
	}
	
	public static String love(String userID){
		String postURL = validateURL + "love";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String strResult = "";
		HttpPost httpRequest =new HttpPost(postURI);
		ArrayList<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("userID",userID));
		params.add(new BasicNameValuePair("from","android"));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse httpResponse=httpClient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				strResult=EntityUtils.toString(httpResponse.getEntity());
			}
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		return strResult;
	}
	
	public static String transferBlog(String ope, String userID, String content, String imageUrl, 
			String blogID, String blogAuthorID, String time, String location, int option){

		String dir = "post/";
		String postURL = validateURL + dir + ope;// + ".php";
		URI postURI = null;
		try {
			postURI = new URI(postURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpPost httpPost = new HttpPost(postURI);
		NameValuePair userIDPair = new BasicNameValuePair("userID", userID);
		NameValuePair contentPair = new BasicNameValuePair("content", content);
		NameValuePair imageUrlPair = new BasicNameValuePair("imageUrl", imageUrl);
		NameValuePair blogIDPair = new BasicNameValuePair("blogID", blogID);
		NameValuePair blogAuthorIDPair = new BasicNameValuePair("blogAuthorID", blogAuthorID);
		NameValuePair timePair = new BasicNameValuePair("time", time);
		NameValuePair locationPair = new BasicNameValuePair("location", location);
		NameValuePair optionPair = new BasicNameValuePair("option", Integer.toString(option));
		
		params.add(userIDPair);
		params.add(contentPair);
		params.add(imageUrlPair);
		params.add(blogIDPair);
		params.add(blogAuthorIDPair);
		params.add(timePair);
		params.add(locationPair);
		params.add(optionPair);
		
		HttpEntity he;
		try {
			he = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Connection", "Keep-Alive");
			httpPost.setEntity(he);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if(null == cookieStore || cookieStore.getCookies().isEmpty() || cookieStore.getCookies().get(0).isExpired(new Date()))
		{
			validateAccount(Username, Password);
		}
		httpClient.setCookieStore(cookieStore);
		try {
			HttpResponse ht = httpClient.execute(httpPost);
			if(ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity het = ht.getEntity();
				InputStream is = het.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String response = "";
				String readLine = null;
				while((readLine =br.readLine()) != null){
					response = response + readLine;
				}
				is.close();
				br.close();
				
				return response;
			}else{
				return "error";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "exception";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception";
		}
	}

}
