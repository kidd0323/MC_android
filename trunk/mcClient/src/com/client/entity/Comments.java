package com.client.entity;

import java.util.ArrayList;

public class Comments {

	private int commentNum;
	private ArrayList<String> authorList;
	private ArrayList<String> authorIDList;
	private ArrayList<String> smallPhotoUrlList;
	private ArrayList<String> middlePhotoUrlList;
	private ArrayList<String> bigPhotoUrlList;
	private ArrayList<String> commentList;
	private ArrayList<String> commentIDList;
	private ArrayList<String> targetIDList;
	private ArrayList<String> targetNameList;
	private ArrayList<String> timeList;
	private String ErrorMessage;
	
	public Comments() {
		super();
	}

	public Comments(int commentNum, ArrayList<String> authorList,
			ArrayList<String> authorIDList,
			ArrayList<String> smallPhotoUrlList,
			ArrayList<String> middlePhotoUrlList,
			ArrayList<String> bigPhotoUrlList, ArrayList<String> commentList,
			ArrayList<String> commentIDList, ArrayList<String> targetIDList,
			ArrayList<String> targetNameList, ArrayList<String> timeList,
			String errorMessage) {
		super();
		this.commentNum = commentNum;
		this.authorList = authorList;
		this.authorIDList = authorIDList;
		this.smallPhotoUrlList = smallPhotoUrlList;
		this.middlePhotoUrlList = middlePhotoUrlList;
		this.bigPhotoUrlList = bigPhotoUrlList;
		this.commentList = commentList;
		this.commentIDList = commentIDList;
		this.targetIDList = targetIDList;
		this.targetNameList = targetNameList;
		this.timeList = timeList;
		ErrorMessage = errorMessage;
	}

	public ArrayList<String> getSmallPhotoUrlList() {
		return smallPhotoUrlList;
	}

	public void setSmallPhotoUrlList(ArrayList<String> smallPhotoUrlList) {
		this.smallPhotoUrlList = smallPhotoUrlList;
	}

	public ArrayList<String> getMiddlePhotoUrlList() {
		return middlePhotoUrlList;
	}

	public void setMiddlePhotoUrlList(ArrayList<String> middlePhotoUrlList) {
		this.middlePhotoUrlList = middlePhotoUrlList;
	}

	public ArrayList<String> getBigPhotoUrlList() {
		return bigPhotoUrlList;
	}

	public void setBigPhotoUrlList(ArrayList<String> bigPhotoUrlList) {
		this.bigPhotoUrlList = bigPhotoUrlList;
	}

	public ArrayList<String> getCommentIDList() {
		return commentIDList;
	}



	public ArrayList<String> getTargetIDList() {
		return targetIDList;
	}

	public void setTargetIDList(ArrayList<String> targetIDList) {
		this.targetIDList = targetIDList;
	}

	public ArrayList<String> getTargetNameList() {
		return targetNameList;
	}

	public void setTargetNameList(ArrayList<String> targetNameList) {
		this.targetNameList = targetNameList;
	}

	public void setCommentIDList(ArrayList<String> commentIDList) {
		this.commentIDList = commentIDList;
	}



	public int getCommentNum() {
		return commentNum;
	}


	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}


	public ArrayList<String> getAuthorList() {
		return authorList;
	}


	public void setAuthorList(ArrayList<String> authorList) {
		this.authorList = authorList;
	}


	public ArrayList<String> getAuthorIDList() {
		return authorIDList;
	}


	public void setAuthorIDList(ArrayList<String> authorIDList) {
		this.authorIDList = authorIDList;
	}


	public ArrayList<String> getCommentList() {
		return commentList;
	}


	public void setCommentList(ArrayList<String> commentList) {
		this.commentList = commentList;
	}


	public ArrayList<String> getTimeList() {
		return timeList;
	}


	public void setTimeList(ArrayList<String> timeList) {
		this.timeList = timeList;
	}


	public String getErrorMessage() {
		return ErrorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	
	
}
