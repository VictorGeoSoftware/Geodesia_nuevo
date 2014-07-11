package com.victor.geodesia_nuevo;

public class FoundPointsModel 
{
	private String project;
	private String pointName;
	private String latitude;
	private String longitude;
	private String x;
	private String y;
	
	public FoundPointsModel(String project, String pointName, String latitude,
			String longitude, String x, String y)
	{
		this.project = project;
		this.pointName = pointName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.x = x;
		this.y = y;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

}
