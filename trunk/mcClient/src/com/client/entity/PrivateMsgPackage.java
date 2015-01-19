package com.client.entity;

public class PrivateMsgPackage {
	
	private String msgID;
	private String author;
	private String authorID;
	private String smallPhotoUrl;
	private String middlePhotoUrl;
	private String bigPhotoUrl;
	private String msg;
	private String releaseTime;
	private String ErrorMessage;
	
	public PrivateMsgPackage() {
		super();
	}


	public PrivateMsgPackage(String msgID, String author, String authorID,
			String smallPhotoUrl, String middlePhotoUrl, String bigPhotoUrl,
			String msg, String releaseTime, String errorMessage) {
		super();
		this.msgID = msgID;
		this.author = author;
		this.authorID = authorID;
		this.smallPhotoUrl = smallPhotoUrl;
		this.middlePhotoUrl = middlePhotoUrl;
		this.bigPhotoUrl = bigPhotoUrl;
		this.msg = msg;
		this.releaseTime = releaseTime;
		ErrorMessage = errorMessage;
	}


	public String getSmallPhotoUrl() {
		return smallPhotoUrl;
	}


	public void setSmallPhotoUrl(String smallPhotoUrl) {
		this.smallPhotoUrl = smallPhotoUrl;
	}


	public String getMiddlePhotoUrl() {
		return middlePhotoUrl;
	}


	public void setMiddlePhotoUrl(String middlePhotoUrl) {
		this.middlePhotoUrl = middlePhotoUrl;
	}


	public String getBigPhotoUrl() {
		return bigPhotoUrl;
	}


	public void setBigPhotoUrl(String bigPhotoUrl) {
		this.bigPhotoUrl = bigPhotoUrl;
	}


	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorID() {
		return authorID;
	}

	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}



}
