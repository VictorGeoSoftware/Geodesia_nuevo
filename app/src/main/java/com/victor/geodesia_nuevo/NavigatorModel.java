package com.victor.geodesia_nuevo;


public class NavigatorModel 
{
	private boolean isDirectory;
	private String name;
	
	
	public NavigatorModel(boolean isDirectory, String name)
	{
		this.name = name;
		this.isDirectory = isDirectory;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}


}
