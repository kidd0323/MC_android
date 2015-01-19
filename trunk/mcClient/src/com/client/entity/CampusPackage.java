package com.client.entity;

import java.util.ArrayList;

public class CampusPackage {

	private int campusNum;
	private ArrayList campusList;
	private ArrayList campusIDList;

	
	
	public CampusPackage() {
		super();
	}


	public CampusPackage(int campusNum, ArrayList campusList,
			ArrayList campusIDList) {
		super();
		this.campusNum = campusNum;
		this.campusList = campusList;
		this.campusIDList = campusIDList;
	}


	public ArrayList getCampusIDList() {
		return campusIDList;
	}


	public void setCampusIDList(ArrayList campusIDList) {
		this.campusIDList = campusIDList;
	}


	public int getCampusNum() {
		return campusNum;
	}

	public void setCampusNum(int campusNum) {
		this.campusNum = campusNum;
	}

	public ArrayList getCampusList() {
		return campusList;
	}

	public void setCampusList(ArrayList campusList) {
		this.campusList = campusList;
	}



	
}
