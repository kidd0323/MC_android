package com.client.entity;

public class Campus {
	private String regionID;
	private String region;
	private String campusID;
	private String campus;
	
	
	public Campus() {
		super();
	}




	public Campus(String regionID, String region, String campusID,
			String campus) {
		super();
		this.regionID = regionID;
		this.region = region;
		this.campusID = campusID;
		this.campus = campus;
	}




	public String getRegionID() {
		return regionID;
	}


	public void setRegionID(String regionID) {
		this.regionID = regionID;
	}


	public String getRegion() {
		return region;
	}


	public void setRegion(String region) {
		this.region = region;
	}


	public String getCampusID() {
		return campusID;
	}


	public void setCampusID(String campusID) {
		this.campusID = campusID;
	}


	public String getCampus() {
		return campus;
	}


	public void setCampus(String campus) {
		this.campus = campus;
	}
	
	
}
