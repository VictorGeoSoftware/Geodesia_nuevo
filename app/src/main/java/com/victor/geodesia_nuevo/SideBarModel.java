package com.victor.geodesia_nuevo;

public class SideBarModel 
{
	private String title;
	private String subtTitle;
	
	public SideBarModel(String title, String subTitle)
	{
		this.setTitle(title);
		this.setSubtTitle(subTitle);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtTitle() {
		return subtTitle;
	}

	public void setSubtTitle(String subtTitle) {
		this.subtTitle = subtTitle;
	}
	
}
