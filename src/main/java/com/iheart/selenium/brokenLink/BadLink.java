package com.iheart.selenium.brokenLink;

public class BadLink {
	String linkText;
	String url;
	int statusCode;
	
	public BadLink(String linkText, String url, int statusCode)
	{
		this.linkText = linkText;
		this.url = url;
		this.statusCode = statusCode;
	}
	
	public String getLinkText()
	{
		return linkText;
	}
	

	public String getUrl()
	{
		return url;
	}
	
	public int getStatusCode()
	{
		return statusCode;
	}
}
