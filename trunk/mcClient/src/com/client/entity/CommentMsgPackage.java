package com.client.entity;

import java.util.ArrayList;

public class CommentMsgPackage {
	
	private String msgID;
	private Blog blogObj;
	private String commentAuthor;
	private String commentAuthorID;
	private String smallPhotoUrl;
	private String middlePhotoUrl;
	private String bigPhotoUrl;
	private String targetUser;
	private String targetUserID;
	private String comment;
	private String commentTime;
	
	public CommentMsgPackage() {
		super();
	}



	public CommentMsgPackage(String msgID, Blog blogObj, String commentAuthor,
			String commentAuthorID, String smallPhotoUrl,
			String middlePhotoUrl, String bigPhotoUrl, String targetUser,
			String targetUserID, String comment, String commentTime) {
		super();
		this.msgID = msgID;
		this.blogObj = blogObj;
		this.commentAuthor = commentAuthor;
		this.commentAuthorID = commentAuthorID;
		this.smallPhotoUrl = smallPhotoUrl;
		this.middlePhotoUrl = middlePhotoUrl;
		this.bigPhotoUrl = bigPhotoUrl;
		this.targetUser = targetUser;
		this.targetUserID = targetUserID;
		this.comment = comment;
		this.commentTime = commentTime;
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

	public Blog getBlogObj() {
		return blogObj;
	}

	public void setBlogObj(Blog blogObj) {
		this.blogObj = blogObj;
	}

	public String getCommentAuthor() {
		return commentAuthor;
	}

	public void setCommentAuthor(String commentAuthor) {
		this.commentAuthor = commentAuthor;
	}

	public String getCommentAuthorID() {
		return commentAuthorID;
	}

	public void setCommentAuthorID(String commentAuthorID) {
		this.commentAuthorID = commentAuthorID;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getTargetUserID() {
		return targetUserID;
	}

	public void setTargetUserID(String targetUserID) {
		this.targetUserID = targetUserID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}


}
