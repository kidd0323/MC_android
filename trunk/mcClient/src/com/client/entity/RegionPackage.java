package com.client.entity;

import java.util.ArrayList;

public class RegionPackage {

	private int regionNum;
	private ArrayList regionList;
	private ArrayList regionIDList;
	
	
	public RegionPackage() {
		super();
	}
	


	public RegionPackage(int regionNum, ArrayList regionList,
			ArrayList regionIDList) {
		super();
		this.regionNum = regionNum;
		this.regionList = regionList;
		this.regionIDList = regionIDList;
	}



	public ArrayList getRegionIDList() {
		return regionIDList;
	}



	public void setRegionIDList(ArrayList regionIDList) {
		this.regionIDList = regionIDList;
	}



	public int getRegionNum() {
		return regionNum;
	}
	public void setRegionNum(int regionNum) {
		this.regionNum = regionNum;
	}
	public ArrayList getRegionList() {
		return regionList;
	}
	public void setRegionList(ArrayList regionList) {
		this.regionList = regionList;
	}
	
	
}
